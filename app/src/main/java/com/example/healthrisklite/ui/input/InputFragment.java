package com.example.healthrisklite.ui.input;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.healthrisklite.PanduanGulaActivity;
import com.example.healthrisklite.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class InputFragment extends Fragment {

    // Nilai default counter
    private int airMinum = 6;      // Gelas
    private int aktivitasFisik = 30; // Menit
    private int gula = 4;           // sdt

    // Jam Tidur & Bangun
    private int jamTidurHour = 22, jamTidurMinute = 0;
    private int jamBangunHour = 5, jamBangunMinute = 0;

    // View Components
    private TextView tvValTidur, tvJamTidur, tvJamBangun;
    private TextView tvValAir, tvValAktivitas, tvValGula;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_input2, container, false);

        // Inisialisasi View dari XML
        tvValTidur = view.findViewById(R.id.tvValTidur);
        tvJamTidur = view.findViewById(R.id.tvJamTidur);
        tvJamBangun = view.findViewById(R.id.tvJamBangun);

        tvValAir = view.findViewById(R.id.tvValAir);
        tvValAktivitas = view.findViewById(R.id.tvValAktivitas);
        tvValGula = view.findViewById(R.id.tvValGula);

        LinearLayout btnJamTidur = view.findViewById(R.id.btnJamTidur);
        LinearLayout btnJamBangun = view.findViewById(R.id.btnJamBangun);

        TextView btnKurangAir = view.findViewById(R.id.btnKurangAir);
        TextView btnTambahAir = view.findViewById(R.id.btnTambahAir);

        TextView btnKurangAktivitas = view.findViewById(R.id.btnKurangAktivitas);
        TextView btnTambahAktivitas = view.findViewById(R.id.btnTambahAktivitas);

        TextView btnKurangGula = view.findViewById(R.id.btnKurangGula);
        TextView btnTambahGula = view.findViewById(R.id.btnTambahGula);

        MaterialCardView btnPanduanGula = view.findViewById(R.id.btnPanduanGula);
        Button btnSimpan = view.findViewById(R.id.btnSimpan);

        // Update tampilan awal
        updateTampilan();

        // Event Listener Picker Jam
        btnJamTidur.setOnClickListener(v -> showTimePicker(true));
        btnJamBangun.setOnClickListener(v -> showTimePicker(false));

        // Event Listener Air
        btnKurangAir.setOnClickListener(v -> { if (airMinum > 0) { airMinum--; updateTampilan(); } });
        btnTambahAir.setOnClickListener(v -> { airMinum++; updateTampilan(); });

        // Event Listener Aktivitas
        btnKurangAktivitas.setOnClickListener(v -> { if (aktivitasFisik >= 5) { aktivitasFisik -= 5; updateTampilan(); } });
        btnTambahAktivitas.setOnClickListener(v -> { aktivitasFisik += 5; updateTampilan(); });

        // Event Listener Gula
        btnKurangGula.setOnClickListener(v -> { if (gula > 0) { gula--; updateTampilan(); } });
        btnTambahGula.setOnClickListener(v -> { gula++; updateTampilan(); });

        // Panduan Gula
        if (btnPanduanGula != null) {
            btnPanduanGula.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), PanduanGulaActivity.class);
                startActivity(intent);
            });
        }

        // Tombol Simpan
        btnSimpan.setOnClickListener(v -> simpanData());

        return view;
    }

    private void showTimePicker(boolean isTidur) {
        int initialHour = isTidur ? jamTidurHour : jamBangunHour;
        int initialMinute = isTidur ? jamTidurMinute : jamBangunMinute;

        TimePickerDialog dialog = new TimePickerDialog(requireContext(), (view, hourOfDay, minute) -> {
            if (isTidur) {
                jamTidurHour = hourOfDay;
                jamTidurMinute = minute;
            } else {
                jamBangunHour = hourOfDay;
                jamBangunMinute = minute;
            }
            updateTampilan();
        }, initialHour, initialMinute, true);

        dialog.show();
    }

    private int hitungDurasiTidurJam() {
        int totalMenitTidur = (jamTidurHour * 60) + jamTidurMinute;
        int totalMenitBangun = (jamBangunHour * 60) + jamBangunMinute;

        int selisihMenit;
        if (totalMenitBangun < totalMenitTidur) {
            selisihMenit = (1440 - totalMenitTidur) + totalMenitBangun;
        } else {
            selisihMenit = totalMenitBangun - totalMenitTidur;
        }

        return Math.round(selisihMenit / 60f);
    }

    private void updateTampilan() {
        if (tvJamTidur != null) tvJamTidur.setText(String.format(Locale.getDefault(), "%02d:%02d", jamTidurHour, jamTidurMinute));
        if (tvJamBangun != null) tvJamBangun.setText(String.format(Locale.getDefault(), "%02d:%02d", jamBangunHour, jamBangunMinute));

        int durasiTidur = hitungDurasiTidurJam();
        if (tvValTidur != null) tvValTidur.setText("Total durasi: " + durasiTidur + " jam");

        if (tvValAir != null) tvValAir.setText(airMinum + " gelas");
        if (tvValAktivitas != null) tvValAktivitas.setText(aktivitasFisik + " menit");
        if (tvValGula != null) tvValGula.setText(gula + " sdt");
    }

    private void simpanData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "Pengguna belum login!", Toast.LENGTH_SHORT).show();
            return;
        }

        int durasiTidur = hitungDurasiTidurJam();

        // Kalkulasi Skor
        int skor = 100;
        if (durasiTidur < 7) skor -= (7 - durasiTidur) * 5;
        if (airMinum < 8) skor -= (8 - airMinum) * 4;
        if (gula > 6) skor -= (gula - 6) * 5;
        if (aktivitasFisik < 30) skor -= (30 - aktivitasFisik) / 5 * 2;

        if (skor < 10) skor = 10;
        if (skor > 100) skor = 100;

        String tanggalHariIni = new SimpleDateFormat("EEEE, d MMMM yyyy", new Locale("id", "ID")).format(new Date());
        int finalSkor = skor;

        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(user.getUid());

        // 1. UPDATE DASHBOARD (Gunakan Integer/Long agar cocok)
        Map<String, Object> updateDashboard = new HashMap<>();
        updateDashboard.put("air", airMinum);
        updateDashboard.put("gula", gula);
        updateDashboard.put("tidur", durasiTidur);
        updateDashboard.put("aktivitas", aktivitasFisik);
        updateDashboard.put("last_health_score", finalSkor);

        userRef.updateChildren(updateDashboard);

        // 2. TAMBAH KE RIWAYAT
        DatabaseReference riwayatRef = userRef.child("riwayat").push();
        Map<String, Object> itemRiwayat = new HashMap<>();
        itemRiwayat.put("tanggal", tanggalHariIni);
        itemRiwayat.put("skor", finalSkor);
        itemRiwayat.put("air", airMinum);
        itemRiwayat.put("gula", gula);
        itemRiwayat.put("tidur", durasiTidur);
        itemRiwayat.put("aktivitas", aktivitasFisik);

        riwayatRef.setValue(itemRiwayat).addOnSuccessListener(aVoid -> {
            updateStreakFirebase(userRef);
            Toast.makeText(getContext(), "Berhasil Disimpan! Skor: " + finalSkor, Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Gagal menyimpan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    // METHOD UPDATE STREAK
    private void updateStreakFirebase(DatabaseReference userRef) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = sdf.format(new Date());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String lastInputDate = snapshot.child("last_input_date").getValue(String.class);
                Long currentStreakVal = snapshot.child("user_streak").getValue(Long.class);
                int currentStreak = currentStreakVal != null ? currentStreakVal.intValue() : 0;

                if (lastInputDate == null || lastInputDate.isEmpty()) {
                    currentStreak = 1;
                } else if (lastInputDate.equals(today)) {
                    if (currentStreak == 0) currentStreak = 1;
                } else {
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DAY_OF_YEAR, -1);
                    String yesterday = sdf.format(cal.getTime());

                    if (lastInputDate.equals(yesterday)) {
                        currentStreak++;
                    } else {
                        currentStreak = 1;
                    }
                }

                Map<String, Object> streakData = new HashMap<>();
                streakData.put("last_input_date", today);
                streakData.put("user_streak", currentStreak);

                userRef.updateChildren(streakData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}