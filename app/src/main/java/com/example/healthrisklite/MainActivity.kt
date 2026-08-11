package com.example.healthrisklite

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.healthrisklite.ui.dashboard.DashboardFragment
import com.example.healthrisklite.ui.input.InputFragment
import com.example.healthrisklite.ui.insight.InsightFragment
import com.example.healthrisklite.ui.profil.ProfilFragment
import com.example.healthrisklite.ui.riwayat.RiwayatFragment
// import Fragment lainnya...
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. Force Light Mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 2. ATUR STATUS BAR AGAR TRANSPARAN TAPI KONTEN TIDAK TERTUTUP (PENTING!)
        // Kode ini membuat Status Bar terlihat transparan, dan sistem UI (seperti FitsSystemWindows)
        // akan menangani area padding atasnya.
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            // Buat Status Bar transparan, tapi areanya tetap dipesan
            statusBarColor = Color.TRANSPARENT
            // Pastikan ikon di Status Bar terlihat gelap (agar terlihat di background ungu muda kamu)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        mAuth = FirebaseAuth.getInstance()

        // 3. Cek Login
        if (mAuth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // 4. Tampilkan Fragment Pertama kali dibuka (Dashboard)
        if (savedInstanceState == null) {
            replaceFragment(DashboardFragment())
        }

        // 5. Navigasi Bottom Navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav?.setOnItemSelectedListener { item ->
            // Pastikan ID menu di bottom_navigation.xml kamu sudah benar
            when (item.itemId) {
                R.id.navigation_dashboard -> replaceFragment(DashboardFragment())
                R.id.navigation_input -> replaceFragment(InputFragment())
                R.id.navigation_insight -> replaceFragment(InsightFragment())
                R.id.navigation_riwayat -> replaceFragment(RiwayatFragment())
                R.id.navigation_profil -> replaceFragment(ProfilFragment())
                else -> false
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}