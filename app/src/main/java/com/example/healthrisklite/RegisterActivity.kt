package com.example.healthrisklite

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var etNama: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnDaftar: Button
    private lateinit var tvLogin: TextView

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inisialisasi Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        // Inisialisasi View (Pastikan ID di XML activity_register.xml sesuai)
        etNama = findViewById(R.id.etNama)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnDaftar = findViewById(R.id.btnDaftar)
        tvLogin = findViewById(R.id.tvLogin)

        btnDaftar.setOnClickListener {
            validasiDanDaftar()
        }

        tvLogin.setOnClickListener {
            finish() // Kembali ke halaman Login
        }
    }

    private fun validasiDanDaftar() {
        val nama = etNama.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (nama.isEmpty()) {
            etNama.error = "Nama tidak boleh kosong"
            etNama.requestFocus()
            return
        }

        if (email.isEmpty()) {
            etEmail.error = "Email tidak boleh kosong"
            etEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Format email tidak valid"
            etEmail.requestFocus()
            return
        }

        if (password.isEmpty()) {
            etPassword.error = "Password tidak boleh kosong"
            etPassword.requestFocus()
            return
        }

        if (password.length < 6) {
            etPassword.error = "Password minimal 6 karakter"
            etPassword.requestFocus()
            return
        }

        prosesDaftarFirebase(nama, email, password)
    }

    private fun prosesDaftarFirebase(nama: String, email: String, password: String) {
        btnDaftar.isEnabled = false
        btnDaftar.text = "Memproses..."

        // Step 1: Buat Akun Firebase Auth
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = mAuth.currentUser?.uid

                    if (userId != null) {
                        // Step 2: Buat Data Pengguna
                        val userData = hashMapOf(
                            "uid" to userId,
                            "nama" to nama,
                            "email" to email,
                            "createdAt" to System.currentTimeMillis()
                        )

                        // Step 3: Simpan Data ke Database Node '/users/{UID}'
                        val dbRef = FirebaseDatabase.getInstance().getReference("users")
                        dbRef.child(userId).setValue(userData)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Pendaftaran Berhasil!", Toast.LENGTH_SHORT).show()

                                // Step 4: Masuk ke MainActivity
                                val intent = Intent(this, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                btnDaftar.isEnabled = true
                                btnDaftar.text = "DAFTAR SEKARANG"
                                Toast.makeText(this, "Gagal simpan data: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                } else {
                    btnDaftar.isEnabled = true
                    btnDaftar.text = "DAFTAR SEKARANG"
                    val errorMsg = task.exception?.message ?: "Pendaftaran gagal"
                    Toast.makeText(this, "Gagal: $errorMsg", Toast.LENGTH_LONG).show()
                }
            }
    }
}