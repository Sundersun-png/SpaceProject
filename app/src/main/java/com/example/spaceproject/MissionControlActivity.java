package com.example.spaceproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MissionControlActivity extends AppCompatActivity {

    private List<CrewMember> crewList;
    private CrewMember crewA = null;
    private CrewMember crewB = null;
    private String selectedMission = "";

    private TextView tvCrewA, tvCrewB, tvMissionType;
    private Button btnLaunch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_control);

        crewList     = CrewRepository.getCrewList();
        tvCrewA      = findViewById(R.id.tvCrewA);
        tvCrewB      = findViewById(R.id.tvCrewB);
        tvMissionType = findViewById(R.id.tvMissionType);
        btnLaunch    = findViewById(R.id.btnLaunchMission);

        // Auto-fill crew A and B from selected crew in Quarters/Simulator
        List<CrewMember> selected = new ArrayList<>();
        for (CrewMember c : crewList) {
            if (c.isSelected()) selected.add(c);
        }
        if (selected.size() >= 1) {
            crewA = selected.get(0);
            tvCrewA.setText(crewA.getName() + "\n" + crewA.getSpecialization());
        }
        if (selected.size() >= 2) {
            crewB = selected.get(1);
            tvCrewB.setText(crewB.getName() + "\n" + crewB.getSpecialization());
        }

        // Mission type buttons
        int[] missionBtnIds = {
                R.id.btnAsteroid,
                R.id.btnReactor,
                R.id.btnVirus,
                R.id.btnAlien,
                R.id.btnPotion
        };
        String[] missionNames = {
                "Asteroid Field",
                "Reactor Meltdown",
                "Virus Outbreak",
                "Alien Attack",
                "Potion Making"
        };

        for (int i = 0; i < missionBtnIds.length; i++) {
            final String missionName = missionNames[i];
            Button btn = findViewById(missionBtnIds[i]);
            if (btn != null) {
                btn.setOnClickListener(v -> {
                    selectedMission = missionName;
                    tvMissionType.setText("Selected: " + missionName);
                    Toast.makeText(this, missionName + " selected", Toast.LENGTH_SHORT).show();
                });
            }
        }

        // Launch Mission
        btnLaunch.setOnClickListener(v -> {
            if (crewA == null || crewB == null) {
                Toast.makeText(this, "Select 2 crew members first", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedMission.isEmpty()) {
                Toast.makeText(this, "Select a mission type first", Toast.LENGTH_SHORT).show();
                return;
            }
            runMission();
        });
    }

    private void runMission() {
        // Generate threat based on mission
        int threatHp    = 30 + new Random().nextInt(20);
        int threatSkill = 5  + new Random().nextInt(10);
        int threatRes   = 2  + new Random().nextInt(5);

        int crewAEnergy = crewA.getEnergy();
        int crewBEnergy = crewB.getEnergy();

        // Simple combat loop
        boolean missionSuccess = false;
        int maxRounds = 20;

        for (int round = 0; round < maxRounds; round++) {
            // Crew A attacks
            int damageByA = Math.max(0, crewA.getSkill() - threatRes);
            threatHp -= damageByA;
            if (threatHp <= 0) { missionSuccess = true; break; }

            // Threat attacks A
            int damageToA = Math.max(0, threatSkill - 2);
            crewAEnergy -= damageToA;

            // Crew B attacks
            int damageByB = Math.max(0, crewB.getSkill() - threatRes);
            threatHp -= damageByB;
            if (threatHp <= 0) { missionSuccess = true; break; }

            // Threat attacks B
            int damageToB = Math.max(0, threatSkill - 2);
            crewBEnergy -= damageToB;

            // Both crew dead = fail
            if (crewAEnergy <= 0 && crewBEnergy <= 0) break;
        }

        // Apply results
        crewA.setEnergy(Math.max(0, crewAEnergy));
        crewB.setEnergy(Math.max(0, crewBEnergy));

        if (missionSuccess) {
            crewA.gainExp(2);
            crewB.gainExp(2);
            Statistics.getInstance().recordWin();
            Toast.makeText(this,
                    "Mission SUCCESS! " + crewA.getName() + " & " + crewB.getName() + " gained XP!",
                    Toast.LENGTH_LONG).show();
        } else {
            Statistics.getInstance().recordLoss();
            Toast.makeText(this,
                    "Mission FAILED! Your crew needs to recover.",
                    Toast.LENGTH_LONG).show();
        }

        // Deselect crew after mission
        crewA.setSelected(false);
        crewB.setSelected(false);

        finish();
    }
}