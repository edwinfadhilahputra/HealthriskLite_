package com.example.healthrisklite.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.healthrisklite.R;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashboardFragment extends Fragment {

    private TextView tvGreetingNama;
    private TextView tvSkorKesehatan;
    private CircularProgressIndicator progressBarSkor;
    private TextView tvValAir, tvValGula, tvValTidur, tvValAktivitas;
    private TextView tvStreakTitle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        tvGreetingNama = view.findViewById(R.id.tvGreetingNama);
        tvSkorKesehatan = view.findViewById(R.id.tvSkorKesehatan);
        progressBarSkor = view.findViewById(R.id.progressBarSkor);

        tvValAir = view.findViewById(R.id.tvValAir);
        tvValGula = view.findViewById(R.id.tvValGula);
        tvValTidur = view.findViewById(R.id.tvValTidur);
        tvValAktivitas = view.findViewById(R.id.tvValAktivitas);
        tvStreakTitle = view.findViewById(R.id.tvStreakTitle);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDashboardData();
    }

    private void loadDashboardData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(user.getUid());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded() || !snapshot.exists()) return;

                // 1. Ambil Nama (Sinkron dengan Profil & Firebase Auth)
                String nama = snapshot.child("nama").getValue(String.class);
                if (nama == null || nama.isEmpty()) {
                    nama = user.getDisplayName() != null ? user.getDisplayName() : "Pengguna Sehat";
                }

                if (tvGreetingNama != null) {
                    tvGreetingNama.setText("Halo, " + nama + " 👋");
                }

                // 2. Skor Kesehatan
                Object skorObj = snapshot.child("last_health_score").getValue();
                int skor = skorObj != null ? Integer.parseInt(skorObj.toString()) : 0;

                if (tvSkorKesehatan != null) tvSkorKesehatan.setText(String.valueOf(skor));
                if (progressBarSkor != null) {
                    progressBarSkor.setMax(100);
                    progressBarSkor.setProgress(skor);
                }

                // 3. Ringkasan Input
                Object air = snapshot.child("air").getValue();
                Object gula = snapshot.child("gula").getValue();
                Object tidur = snapshot.child("tidur").getValue();
                Object aktivitas = snapshot.child("aktivitas").getValue();

                if (tvValAir != null) tvValAir.setText((air != null ? air.toString() : "0") + " gelas");
                if (tvValGula != null) tvValGula.setText((gula != null ? gula.toString() : "0") + " sdt");
                if (tvValTidur != null) tvValTidur.setText((tidur != null ? tidur.toString() : "0") + " jam");
                if (tvValAktivitas != null) tvValAktivitas.setText((aktivitas != null ? aktivitas.toString() : "0") + " mnt");

                // 4. Streak
                Object streakObj = snapshot.child("user_streak").getValue();
                int streak = streakObj != null ? Integer.parseInt(streakObj.toString()) : 0;
                if (tvStreakTitle != null) {
                    tvStreakTitle.setText(streak > 0 ? "Streak 🔥 " + streak + " Hari Berturut-turut" : "Mulai Streak Sehatmu Hari Ini! 🔥");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}