package com.example.healthrisklite.ui.profil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.healthrisklite.LoginActivity;
import com.example.healthrisklite.PanduanGulaActivity;
import com.example.healthrisklite.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfilFragment extends Fragment {

    private FirebaseAuth mAuth;
    private SharedPreferences prefs;

    private TextView tvNama, tvEmail, tvTotalLog;
    private String currentNama = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profil, container, false);

        mAuth = FirebaseAuth.getInstance();
        prefs = requireActivity().getSharedPreferences("HealthPrefs", Context.MODE_PRIVATE);

        // Inisialisasi View Dasar
        tvNama = view.findViewById(R.id.tvNamaProfil);
        tvEmail = view.findViewById(R.id.tvEmailProfil);
        tvTotalLog = view.findViewById(R.id.tvTotalCatatan);
        Button btnLogout = view.findViewById(R.id.btnLogout);

        // 1. Action Edit Nama (Klik Nama Pengguna)
        if (tvNama != null) {
            tvNama.setOnClickListener(v -> showDialogEditNama());
        }

        // 2. Action Panduan Gula (Cari ID opsional dari XML)
        int resIdPanduan = getResources().getIdentifier("btnPanduanGulaProfil", "id", requireContext().getPackageName());
        if (resIdPanduan != 0) {
            View btnPanduanGula = view.findViewById(resIdPanduan);
            if (btnPanduanGula != null) {
                btnPanduanGula.setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(), PanduanGulaActivity.class);
                    startActivity(intent);
                });
            }
        }

        // 3. Action Hapus Data (Cari ID opsional dari XML)
        int resIdHapus = getResources().getIdentifier("btnHapusDataProfil", "id", requireContext().getPackageName());
        if (resIdHapus != 0) {
            View btnHapusData = view.findViewById(resIdHapus);
            if (btnHapusData != null) {
                btnHapusData.setOnClickListener(v -> konfirmasiHapusData());
            }
        }

        // 4. Action Tombol Logout
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> konfirmasiLogout());
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfilDataRealtime();
    }

    private void loadProfilDataRealtime() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        if (tvEmail != null && currentUser.getEmail() != null) {
            tvEmail.setText(currentUser.getEmail());
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(currentUser.getUid());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded() || !snapshot.exists()) return;

                // Nama Realtime
                String nama = snapshot.child("nama").getValue(String.class);
                if (nama == null || nama.isEmpty()) {
                    nama = currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Pengguna Sehat";
                }
                currentNama = nama;
                if (tvNama != null) {
                    tvNama.setText(nama + " ✏️");
                }

                // Email Realtime
                String email = snapshot.child("email").getValue(String.class);
                if (email == null || email.isEmpty()) {
                    email = currentUser.getEmail() != null ? currentUser.getEmail() : "user@healthrisklite.com";
                }
                if (tvEmail != null) tvEmail.setText(email);

                // Hitung Jumlah Catatan
                long totalCatatan = snapshot.child("riwayat").getChildrenCount();
                if (tvTotalLog != null) {
                    tvTotalLog.setText(totalCatatan + " Catatan");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // DIALOG POPUP EDIT NAMA
    private void showDialogEditNama() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit Nama Pengguna");

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(currentNama);
        if (input.getText().length() > 0) {
            input.setSelection(input.getText().length());
        }
        builder.setView(input);

        builder.setPositiveButton("Simpan", (dialog, which) -> {
            String namaBaru = input.getText().toString().trim();
            if (!namaBaru.isEmpty()) {
                simpanNamaBaru(currentUser, namaBaru);
            } else {
                Toast.makeText(requireContext(), "Nama tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Batal", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void simpanNamaBaru(FirebaseUser user, String namaBaru) {
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(user.getUid());

        userRef.child("nama").setValue(namaBaru).addOnSuccessListener(aVoid -> {
            Toast.makeText(requireContext(), "Nama berhasil diperbarui!", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(requireContext(), "Gagal memperbarui database: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(namaBaru)
                .build();
        user.updateProfile(profileUpdates);
    }

    // DIALOG HAPUS DATA RIWAYAT
    private void konfirmasiHapusData() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Hapus Seluruh Data Riwayat?")
                .setMessage("Tindakan ini akan menghapus semua catatan riwayat kesehatan Anda. Data tidak dapat dikembalikan.")
                .setPositiveButton("Hapus", (dialog, which) -> prosesHapusDataFirebase())
                .setNegativeButton("Batal", null)
                .show();
    }

    private void prosesHapusDataFirebase() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        DatabaseReference riwayatRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(currentUser.getUid())
                .child("riwayat");

        riwayatRef.removeValue().addOnSuccessListener(aVoid -> {
            Toast.makeText(getContext(), "Seluruh riwayat berhasil dihapus!", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Gagal menghapus data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void konfirmasiLogout() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Keluar Akun")
                .setMessage("Apakah Anda yakin ingin logout dari aplikasi?")
                .setPositiveButton("Ya, Keluar", (dialog, which) -> prosesLogout())
                .setNegativeButton("Batal", null)
                .show();
    }

    private void prosesLogout() {
        mAuth.signOut();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
        GoogleSignIn.getClient(requireActivity(), gso).signOut();

        prefs.edit().clear().apply();

        Toast.makeText(requireContext(), "Berhasil Keluar", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}