package com.example.healthrisklite;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class PanduanGulaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panduan_gula);

        // Pasang event listener tombol back di header
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }
}