package com.example.healthrisklite

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnGoogleLogin: MaterialCardView
    private lateinit var tvRegisterLink: TextView

    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        prefs = getSharedPreferences("HealthPrefs", Context.MODE_PRIVATE)

        // Auto-login jika akun Firebase aktif
        if (mAuth.currentUser != null || prefs.getBoolean("is_logged_in", false)) {
            pindahKeDashboard()
            return
        }

        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etUsernameLogin)
        etPassword = findViewById(R.id.etPasswordLogin)
        btnLogin = findViewById(R.id.btnLogin)
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin)
        tvRegisterLink = findViewById(R.id.tvRegisterLink)

        // Konfigurasi Google Sign-In (Solusi 2: Memakai Web Client ID Bawaan)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        btnLogin.setOnClickListener { prosesLoginEmailFirebase() }

        btnGoogleLogin.setOnClickListener {
            // Sign out sesi Google lama agar dialog pilih akun selalu muncul
            googleSignInClient.signOut().addOnCompleteListener {
                val signInIntent = googleSignInClient.signInIntent
                googleLauncher.launch(signInIntent)
            }
        }

        tvRegisterLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    // Callback Hasil Google Sign-In
    private val googleLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                // Ambil ID Token dan kirim ke Firebase
                firebaseAuthWithGoogle(account.idToken)
            } catch (e: ApiException) {
                Log.e("GoogleSignInError", "Code: ${e.statusCode}", e)
                Toast.makeText(
                    this,
                    "Google Sign-In Gagal (Code: ${e.statusCode}). Pastikan SHA-1 sudah dipasang!",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            Toast.makeText(this, "Login dibatalkan pengguna.", Toast.LENGTH_SHORT).show()
        }
    }

    // Proses Verifikasi Token Google ke Firebase Server
    private fun firebaseAuthWithGoogle(idToken: String?) {
        if (idToken.isNullOrEmpty()) {
            Toast.makeText(this, "ID Token NULL. Cek file google-services.json!", Toast.LENGTH_LONG).show()
            return
        }

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser

                    // Simpan status login di local storage
                    prefs.edit().apply {
                        putBoolean("is_logged_in", true)
                        putString("user_nama", user?.displayName ?: "Pengguna")
                        putString("user_email", user?.email ?: "")
                        apply()
                    }

                    Toast.makeText(this, "Login Google Berhasil! Halo ${user?.displayName}", Toast.LENGTH_SHORT).show()
                    pindahKeDashboard()
                } else {
                    val errorMsg = task.exception?.localizedMessage ?: "Gagal Autentikasi"
                    Log.e("FirebaseAuthError", errorMsg)
                    Toast.makeText(this, "Firebase Gagal: $errorMsg", Toast.LENGTH_LONG).show()
                }
            }
    }

    // Proses Login Email/Password Biasa
    private fun prosesLoginEmailFirebase() {
        val email = etEmail.text?.toString()?.trim() ?: ""
        val password = etPassword.text?.toString()?.trim() ?: ""

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Masukkan Email dan Password!", Toast.LENGTH_SHORT).show()
            return
        }

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    prefs.edit().apply {
                        putBoolean("is_logged_in", true)
                        putString("user_nama", user?.displayName ?: "Pengguna")
                        putString("user_email", user?.email ?: "")
                        apply()
                    }
                    Toast.makeText(this, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                    pindahKeDashboard()
                } else {
                    Toast.makeText(this, "Login Gagal: Email atau Password Salah!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Fungsi Pembantu Pindah ke MainActivity & MemberSIHKAN Stack Activity
    private fun pindahKeDashboard() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}