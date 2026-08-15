# 🏥 HealthRiskLite

![Android](https://img.shields.io/badge/Platform-Android-green?logo=android)
![Language](https://img.shields.io/badge/Language-Java-orange?logo=java)
![Database](https://img.shields.io/badge/Backend-Firebase-ffca28?logo=firebase)
![License](https://img.shields.io/badge/License-MIT-blue)

**HealthRiskLite** adalah aplikasi Android berbasis *real-time tracking* yang dirancang untuk membantu pengguna memantau pola hidup harian, seperti konsumsi air minum, asupan gula, durasi tidur, dan aktivitas fisik. Aplikasi ini menghitung skor kesehatan secara otomatis serta memberikan wawasan (*insight*) dan rekomendasi kesehatan yang dipersonalisasi.

---

## 📸 Tampilan Aplikasi

| Dashboard | Input Data | Insight Kesehatan | Profil Pengguna |
| <img width="720" height="1600" alt="dashboard" src="https://github.com/user-attachments/assets/79f1607e-167b-4d3f-8f1d-0af0a0c88324" />
 | <img width="720" height="1600" alt="input" src="https://github.com/user-attachments/assets/c7719383-5e5a-4fbc-ac0a-44af43b2e7a5" />
 | <img width="720" height="1600" alt="insight" src="https://github.com/user-attachments/assets/4fcb36f1-c4c5-4f3c-98be-d59261e4a69e" />
 | <img width="1080" height="2400" alt="profil" src="https://github.com/user-attachments/assets/d193c9c7-3586-431b-8f44-6a6d9c571af1" /> |

---

## ✨ Fitur Utama

- **📊 Dashboard Interaktif**: Menampilkan ringkasan skor kesehatan harian dan status gaya hidup secara *real-time*.
- **📝 Input Catatan Harian**: Memudahkan pengguna dalam mencatat:
  - 💧 Konsumsi Air Minum (gelas)
  - 🍬 Asupan Gula (sendok teh)
  - 😴 Durasi Tidur (jam)
  - 🏃 Aktivitas Fisik / Olahraga (menit)
- **💡 Realtime Insight & Analisis**:
  - Klasifikasi status kesehatan otomatis: **Baik** 🟢, **Cukup Baik** 🟡, atau **Buruk** 🔴.
  - Penilaian tingkat risiko (Risiko Rendah, Sedang, atau Tinggi).
  - Saran dan rekomendasi khusus per indikator berdasarkan standar kesehatan harian.
- **📜 Riwayat Catatan**: Menyimpan seluruh log kesehatan terdahulu yang dapat dipantau kapan saja.
- **👤 Manajemen Profil**:
  - Otentikasi aman melalui **Google Sign-In** dan Firebase Auth.
  - Ubah nama pengguna secara *real-time*.
  - Pembersihan (*reset*) data riwayat secara aman.
- **📖 Panduan Konsumsi Gula**: Layar khusus yang berisi edukasi batas aman asupan gula harian.

---

## 🛠️ Teknologi yang Digunakan

- **Language**: Java
- **IDE**: Android Studio
- **UI Components**: Material Design 3, `ConstraintLayout`, `MaterialCardView`, `BottomNavigationView`
- **Backend Services**:
  - **Firebase Authentication** (Google Sign-In)
  - **Firebase Realtime Database** (Penyimpanan dan sinkronisasi data *real-time*)
- **Architecture**: Fragment-based Architecture

---

## 📂 Struktur Proyek

```text
app/src/main/java/com/example/healthrisklite/
├── MainActivity.java
├── LoginActivity.java
├── RegisterActivity.java
├── PanduanGulaActivity.java
└── ui/
    ├── dashboard/
    │   └── DashboardFragment.java
    ├── input/
    │   └── InputFragment.java
    ├── insight/
    │   └── InsightFragment.java
    ├── profil/
    │   └── ProfilFragment.java
    └── riwayat/
        ├── RiwayatFragment.java
        ├── RiwayatAdapter.java
        └── RiwayatItem.java
