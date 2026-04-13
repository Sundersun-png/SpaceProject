package com.example.spaceproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// ═══════════════════════════════════════════════════════════════
// NavigationActivity.java — Hub Screen
// Linked to: activity_navigation.xml
// Shows coins top-right. All nav buttons go to their screens.
// ═══════════════════════════════════════════════════════════════

public class NavigationActivity extends AppCompatActivity {

    private TextView tvCoins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        tvCoins = findViewById(R.id.tvCoins);

        // ── QUARTERS ─────────────────────────────────────────────
        Button btnQuarters = findViewById(R.id.btnQuarters);
        btnQuarters.setOnClickListener(v ->
                startActivity(new Intent(this, QuartersActivity.class))
        );

        // ── SIMULATOR ────────────────────────────────────────────
        Button btnSimulator = findViewById(R.id.btnSimulator);
        btnSimulator.setOnClickListener(v ->
                startActivity(new Intent(this, SimulatorActivity.class))
        );

        // ── MISSION CONTROL ───────────────────────────────────────
        Button btnMissionControl = findViewById(R.id.btnMissionControl);
        btnMissionControl.setOnClickListener(v ->
                startActivity(new Intent(this, MissionControlActivity.class))
        );

        // ── STATISTICS ────────────────────────────────────────────
        Button btnStatistics = findViewById(R.id.btnStatistics);
        btnStatistics.setOnClickListener(v ->
                Toast.makeText(this, "Statistics — coming soon", Toast.LENGTH_SHORT).show()
        );

        // ── HOSPITAL ──────────────────────────────────────────────
        Button btnHospital = findViewById(R.id.btnHospital);
        btnHospital.setOnClickListener(v ->
                Toast.makeText(this, "Hospital — coming soon", Toast.LENGTH_SHORT).show()
        );

        // ── ADD CREW MEMBER ───────────────────────────────────────
        Button btnAddCrewMember = findViewById(R.id.btnAddCrewMember);
        btnAddCrewMember.setOnClickListener(v -> {
            if (GameData.crewList.size() >= 2) {
                Toast.makeText(this,
                        "Max limit reached. Complete 5 missions to recruit more.",
                        Toast.LENGTH_LONG).show();
            } else {
                startActivity(new Intent(this, RecruitActivity.class));
            }
        });
    }

    // ── onResume ──────────────────────────────────────────────────
    // Refresh coins and button state every time we return here
    @Override
    protected void onResume() {
        super.onResume();
        // Update coin display
        tvCoins.setText("🪙 " + GameData.coins);

        // Grey out Add Crew Member if crew is full
        Button btnAdd = findViewById(R.id.btnAddCrewMember);
        btnAdd.setAlpha(GameData.crewList.size() >= 2 ? 0.4f : 1.0f);
    }
}