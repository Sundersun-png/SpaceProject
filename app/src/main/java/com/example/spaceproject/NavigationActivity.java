package com.example.spaceproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class NavigationActivity extends AppCompatActivity {

    private TextView tvCoins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        tvCoins = findViewById(R.id.tvCoins);
        updateCoins();

        // Quarter Button
        findViewById(R.id.btnQuarters).setOnClickListener(v -> {
            startActivity(new Intent(this, QuartersActivity.class));
        });

        // Simulator Button (MainActivity handles Simulator)
        findViewById(R.id.btnSimulator).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
        });

        // Mission Control Button
        findViewById(R.id.btnMissionControl).setOnClickListener(v -> {
            startActivity(new Intent(this, MissionControlActivity.class));
        });

        // Statistics Button
        findViewById(R.id.btnStatistics).setOnClickListener(v -> {
            startActivity(new Intent(this, StatisticsActivity.class));
        });

        // Hospital Button
        findViewById(R.id.btnHospital).setOnClickListener(v -> {
            startActivity(new Intent(this, HospitalActivity.class));
        });

        // Add Crew Member Button
        Button btnAddCrewMember = findViewById(R.id.btnAddCrewMember);
        btnAddCrewMember.setOnClickListener(v -> {
            if (GameData.crewList.size() >= 2) {
                Toast.makeText(this, "Max limit reached. Complete 5 missions to recruit more.", Toast.LENGTH_LONG).show();
            } else {
                startActivity(new Intent(this, RecruitActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCoins();
        
        Button btnAddCrewMember = findViewById(R.id.btnAddCrewMember);
        if (GameData.crewList.size() >= 2) {
            btnAddCrewMember.setAlpha(0.4f);
        } else {
            btnAddCrewMember.setAlpha(1.0f);
        }
    }

    private void updateCoins() {
        if (tvCoins != null) {
            tvCoins.setText(String.valueOf(GameData.coins));
        }
    }
}
