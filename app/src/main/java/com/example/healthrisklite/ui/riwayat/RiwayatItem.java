package com.example.healthrisklite.ui.riwayat;

public class RiwayatItem {
    private String tanggal;
    private int score;
    private int tidur;
    private int air;
    private int aktivitas;
    private int gula;

    public RiwayatItem(String tanggal, int score, int tidur, int air, int aktivitas, int gula) {
        this.tanggal = tanggal;
        this.score = score;
        this.tidur = tidur;
        this.air = air;
        this.aktivitas = aktivitas;
        this.gula = gula;
    }

    // Getter
    public String getTanggal() { return tanggal; }
    public int getScore() { return score; }
    public int getTidur() { return tidur; }
    public int getAir() { return air; }
    public int getAktivitas() { return aktivitas; }
    public int getGula() { return gula; }
}