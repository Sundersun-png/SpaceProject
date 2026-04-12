package com.example.spaceproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private CrewMember crewA;
    private CrewMember crewB;

    private TextView tvCrewAName, tvCrewARole, tvCrewAStats;
    private TextView tvCrewBName, tvCrewBRole, tvCrewBStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulator);

        // Create crew members
        crewA = new CrewMember("Alex", "Engineer", 8);
        crewB = new CrewMember("Blake", "Pilot", 7);

        // Bind views
        tvCrewAName  = findViewById(R.id.tvCrewAName);
        tvCrewARole  = findViewById(R.id.tvCrewARole);
        tvCrewAStats = findViewById(R.id.tvCrewAStats);

        tvCrewBName  = findViewById(R.id.tvCrewBName);
        tvCrewBRole  = findViewById(R.id.tvCrewBRole);
        tvCrewBStats = findViewById(R.id.tvCrewBStats);

        Button btnTrain = findViewById(R.id.btnTrain);

        // Display initial state
        updateUI();

        // Train button wires up to both crew members
        btnTrain.setOnClickListener(v -> {
            crewA.train(0);
            crewB.train(0);
            updateUI();
            Toast.makeText(this, "Training complete!", Toast.LENGTH_SHORT).show();
        });

        // Bottom nav
        LinearLayout navMission  = findViewById(R.id.navMission);
        LinearLayout navQuarters = findViewById(R.id.navQuarters);
        LinearLayout navHospital = findViewById(R.id.navHospital);

        navMission.setOnClickListener(v ->
            startActivity(new Intent(this, MissionControlActivity.class)));

        navQuarters.setOnClickListener(v ->
            startActivity(new Intent(this, QuartersActivity.class)));

        navHospital.setOnClickListener(v ->
            startActivity(new Intent(this, HospitalActivity.class)));

        LinearLayout navStats = findViewById(R.id.navStats);
        navStats.setOnClickListener(v ->
            startActivity(new Intent(this, StatisticsActivity.class)));

        // navSimulator is the current screen — no action needed
    }

    private void updateUI() {
        tvCrewAName.setText(crewA.name);
        tvCrewARole.setText(crewA.role);
        tvCrewAStats.setText("XP: " + crewA.experience + "    Skill: " + crewA.getSkill());

        tvCrewBName.setText(crewB.name);
        tvCrewBRole.setText(crewB.role);
        tvCrewBStats.setText("XP: " + crewB.experience + "    Skill: " + crewB.getSkill());
    }
}