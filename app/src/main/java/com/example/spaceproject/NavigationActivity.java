package com.example.spaceproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class NavigationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This links NavigationActivity to activity_navigation.xml
        setContentView(R.layout.activity_navigation);

        // ── ADD CREW MEMBER BUTTON ──────────────────────────────────────
        // Checks crew count before allowing navigation to RecruitActivity.
        // GameData.crewCount is a shared counter stored in GameData.java
        // so all Activities can read and update the same value.
        Button btnAddCrewMember = findViewById(R.id.btnAddCrewMember);
        btnAddCrewMember.setOnClickListener(v -> {

            if (GameData.crewCount >= 2) {
                // Block recruitment — crew is full
                // Toast shows a short popup message on screen
                Toast.makeText(
                        this,
                        "Max limit reached. You cannot add more than 2 crew members at the start.",
                        Toast.LENGTH_LONG
                ).show();

            } else {
                // Crew is not full — open the recruit screen
                Intent intent = new Intent(NavigationActivity.this, RecruitActivity.class);
                startActivity(intent);
            }
        });

        // ── OTHER NAV BUTTONS ───────────────────────────────────────────
        // Wire these up once you create those Activity files.
        // They are left here as placeholders so the app compiles.

        Button btnQuarters = findViewById(R.id.btnQuarters);
        btnQuarters.setOnClickListener(v ->
                Toast.makeText(this, "Quarters — coming soon", Toast.LENGTH_SHORT).show()
        );

        Button btnSimulator = findViewById(R.id.btnSimulator);
        btnSimulator.setOnClickListener(v ->
                Toast.makeText(this, "Simulator — coming soon", Toast.LENGTH_SHORT).show()
        );

        Button btnMissionControl = findViewById(R.id.btnMissionControl);
        btnMissionControl.setOnClickListener(v ->
                Toast.makeText(this, "Mission Control — coming soon", Toast.LENGTH_SHORT).show()
        );

        Button btnStatistics = findViewById(R.id.btnStatistics);
        btnStatistics.setOnClickListener(v ->
                Toast.makeText(this, "Statistics — coming soon", Toast.LENGTH_SHORT).show()
        );

        Button btnHospital = findViewById(R.id.btnHospital);
        btnHospital.setOnClickListener(v ->
                Toast.makeText(this, "Hospital — coming soon", Toast.LENGTH_SHORT).show()
        );
    }

    // ── onResume ────────────────────────────────────────────────────────
    // onResume runs every time this screen becomes visible again —
    // including when the user comes BACK from RecruitActivity.
    // This lets the Add Crew Member button stay correctly locked/unlocked
    // after returning from the recruit screen.
    @Override
    protected void onResume() {
        super.onResume();

        Button btnAddCrewMember = findViewById(R.id.btnAddCrewMember);
        if (GameData.crewCount >= 2) {
            // Grey out the button visually to signal it is locked
            btnAddCrewMember.setAlpha(0.4f);
        } else {
            btnAddCrewMember.setAlpha(1.0f);
        }
    }
}