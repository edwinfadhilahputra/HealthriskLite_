package com.example.healthrisklite.ui.riwayat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.healthrisklite.R;
import java.util.List;

public class RiwayatAdapter extends RecyclerView.Adapter<RiwayatAdapter.ViewHolder> {

    private final List<RiwayatItem> listRiwayat;

    public RiwayatAdapter(List<RiwayatItem> listRiwayat) {
        this.listRiwayat = listRiwayat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_riwayat_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RiwayatItem item = listRiwayat.get(position);

        holder.tvTanggal.setText(item.getTanggal());
        holder.tvScore.setText(String.valueOf(item.getScore()));

        if (item.getScore() >= 80) {
            holder.tvBadge.setText("Baik");
            holder.tvBadge.setTextColor(0xFF15803D);
        } else if (item.getScore() >= 60) {
            holder.tvBadge.setText("Cukup");
            holder.tvBadge.setTextColor(0xFFB45309);
        } else {
            holder.tvBadge.setText("Kurang");
            holder.tvBadge.setTextColor(0xFFB91C1C);
        }

        holder.tvTidur.setText(item.getTidur() + " jam");
        holder.tvAir.setText(item.getAir() + " gelas");
        holder.tvAktivitas.setText(item.getAktivitas() + " m");
        holder.tvGula.setText(item.getGula() + " sdt");
    }

    @Override
    public int getItemCount() {
        return listRiwayat.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTanggal, tvBadge, tvScore;
        TextView tvTidur, tvAir, tvAktivitas, tvGula;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTanggal = itemView.findViewById(R.id.tvRiwayatTanggal);
            tvBadge = itemView.findViewById(R.id.tvRiwayatBadge);
            tvScore = itemView.findViewById(R.id.tvRiwayatScore);

            tvTidur = itemView.findViewById(R.id.tvRiwayatTidur);
            tvAir = itemView.findViewById(R.id.tvRiwayatAir);
            tvAktivitas = itemView.findViewById(R.id.tvRiwayatAktivitas);
            tvGula = itemView.findViewById(R.id.tvRiwayatGula);
        }
    }
}