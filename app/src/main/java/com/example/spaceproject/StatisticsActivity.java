package com.example.spaceproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        Statistics stats = Statistics.getInstance();

        TextView tvWon   = findViewById(R.id.tvMissionsWon);
        TextView tvLost  = findViewById(R.id.tvMissionsLost);
        TextView tvTotal = findViewById(R.id.tvTotalMissions);
        TextView tvCoins = findViewById(R.id.tvCoinsEarned);
        TextView tvRate  = findViewById(R.id.tvWinRate);
        Button   btnBack = findViewById(R.id.btnBack);

        tvWon.setText(String.valueOf(stats.getMissionsWon()));
        tvLost.setText(String.valueOf(stats.getMissionsLost()));
        tvTotal.setText(String.valueOf(stats.getTotalMissions()));
        tvCoins.setText(String.valueOf(stats.getCoinsEarned()));
        tvRate.setText(String.format("%.1f%%", stats.getWinRate()));

        btnBack.setOnClickListener(v -> finish());
    }
}