package com.example.spaceproject;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

// ═══════════════════════════════════════════════════════════════
// MissionActivity.java — Placeholder for combat screen
// Receives crewAName, crewBName, mission from MissionControlActivity
// Full combat logic to be implemented next sprint
// ═══════════════════════════════════════════════════════════════

public class MissionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);

        String crewA   = getIntent().getStringExtra("crewAName");
        String crewB   = getIntent().getStringExtra("crewBName");
        String mission = getIntent().getStringExtra("mission");

        TextView tvMissionInfo = findViewById(R.id.tvMissionInfo);
        tvMissionInfo.setText(
                "Mission: " + mission + "\n\n" +
                        "Crew A: " + crewA + "\n" +
                        "vs\n" +
                        "Crew B: " + crewB + "\n\n" +
                        "Combat system coming soon!"
        );

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() { finish(); }
        });
    }
}
