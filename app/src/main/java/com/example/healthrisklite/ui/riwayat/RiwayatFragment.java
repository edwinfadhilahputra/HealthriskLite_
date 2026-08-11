package com.example.healthrisklite.ui.riwayat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthrisklite.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RiwayatFragment extends Fragment {

    private RecyclerView recyclerView;
    private RiwayatAdapter adapter;
    private List<RiwayatItem> listRiwayat = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_riwayat, container, false);

        recyclerView = view.findViewById(R.id.rvRiwayat);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRiwayatData();
    }

    private void loadRiwayatData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        DatabaseReference riwayatRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(user.getUid())
                .child("riwayat");

        riwayatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded()) return;

                listRiwayat.clear();
                for (DataSnapshot itemSnap : snapshot.getChildren()) {
                    String tgl = itemSnap.child("tanggal").getValue(String.class);

                    Object skorObj = itemSnap.child("skor").getValue();
                    Object airObj = itemSnap.child("air").getValue();
                    Object gulaObj = itemSnap.child("gula").getValue();
                    Object tidurObj = itemSnap.child("tidur").getValue();
                    Object aktObj = itemSnap.child("aktivitas").getValue();

                    int skor = skorObj != null ? Integer.parseInt(skorObj.toString()) : 0;
                    int air = airObj != null ? Integer.parseInt(airObj.toString()) : 0;
                    int gula = gulaObj != null ? Integer.parseInt(gulaObj.toString()) : 0;
                    int tidur = tidurObj != null ? Integer.parseInt(tidurObj.toString()) : 0;
                    int aktivitas = aktObj != null ? Integer.parseInt(aktObj.toString()) : 0;

                    listRiwayat.add(0, new RiwayatItem(tgl != null ? tgl : "-", skor, tidur, air, aktivitas, gula));
                }

                if (recyclerView != null) {
                    adapter = new RiwayatAdapter(listRiwayat);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}