package com.example.healthrisklite.ui.insight;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.healthrisklite.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InsightFragment extends Fragment {

    private TextView tvStatusKesehatan, tvKategoriRisiko;
    private TextView tvSaranAir, tvSaranGula, tvSaranTidur, tvSaranAktivitas;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_insight, container, false);

        tvStatusKesehatan = view.findViewById(R.id.tvStatusKesehatan);
        tvKategoriRisiko = view.findViewById(R.id.tvKategoriRisiko);

        tvSaranAir = view.findViewById(R.id.tvSaranAir);
        tvSaranGula = view.findViewById(R.id.tvSaranGula);
        tvSaranTidur = view.findViewById(R.id.tvSaranTidur);
        tvSaranAktivitas = view.findViewById(R.id.tvSaranAktivitas);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadInsightDataRealtime();
    }

    private void loadInsightDataRealtime() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(user.getUid());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded() || !snapshot.exists()) return;

                // 1. Ambil Nilai dari Firebase
                Object skorObj = snapshot.child("last_health_score").getValue();
                Object airObj = snapshot.child("air").getValue();
                Object gulaObj = snapshot.child("gula").getValue();
                Object tidurObj = snapshot.child("tidur").getValue();
                Object aktivitasObj = snapshot.child("aktivitas").getValue();

                int skor = skorObj != null ? Integer.parseInt(skorObj.toString()) : 0;
                int air = airObj != null ? Integer.parseInt(airObj.toString()) : 0;
                int gula = gulaObj != null ? Integer.parseInt(gulaObj.toString()) : 0;
                int tidur = tidurObj != null ? Integer.parseInt(tidurObj.toString()) : 0;
                int aktivitas = aktivitasObj != null ? Integer.parseInt(aktivitasObj.toString()) : 0;

                // 2. Kategori Disamakan dengan Dashboard: Baik, Cukup Baik, Buruk
                if (tvStatusKesehatan != null && tvKategoriRisiko != null) {
                    if (skor >= 75) {
                        tvStatusKesehatan.setText("Baik 🟢");
                        tvKategoriRisiko.setText("Risiko Rendah");
                    } else if (skor >= 50) {
                        tvStatusKesehatan.setText("Cukup Baik 🟡");
                        tvKategoriRisiko.setText("Risiko Sedang");
                    } else {
                        tvStatusKesehatan.setText("Buruk 🔴");
                        tvKategoriRisiko.setText("Risiko Tinggi");
                    }
                }

                // 3. Saran Realtime
                if (tvSaranAir != null) {
                    tvSaranAir.setText(air >= 8 ?
                            "Konsumsi air sudah terintegrasi dengan baik (" + air + " gelas/hari)." :
                            "Konsumsi air baru " + air + " gelas. Targetkan 8 gelas sehari.");
                }

                if (tvSaranGula != null) {
                    tvSaranGula.setText(gula <= 4 ?
                            "Asupan gula aman dan terkontrol (" + gula + " sdt/hari)." :
                            "Konsumsi " + gula + " sdt gula berpotensi tinggi. Batasi minuman manis.");
                }

                if (tvSaranTidur != null) {
                    tvSaranTidur.setText(tidur >= 7 ?
                            "Durasi tidur tercukupi (" + tidur + " jam)." :
                            "Durasi tidur hanya " + tidur + " jam. Istirahat minimal 7-8 jam per malam.");
                }

                if (tvSaranAktivitas != null) {
                    tvSaranAktivitas.setText(aktivitas >= 30 ?
                            "Aktivitas fisik tercapai (" + aktivitas + " menit/hari)." :
                            "Baru " + aktivitas + " menit. Tingkatkan aktivitas hingga 30 menit.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Gagal memuat insight: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}