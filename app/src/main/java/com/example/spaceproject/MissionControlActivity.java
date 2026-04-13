package com.example.spaceproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MissionControlActivity extends AppCompatActivity {

    private CrewMember selectedCrewMember = null;
    private String selectedMission = null;

    private TextView tvCoins;
    private CardView cardSelectCrewA, cardSelectCrewB;
    private TextView tvCrewABadge, tvCrewBBadge;
    private TextView tvCrewAName, tvCrewARole, tvCrewAStats;
    private TextView tvCrewBName, tvCrewBRole, tvCrewBStats;

    private CardView cardMissionPilot, cardMissionEngineer, cardMissionMedic, cardMissionSoldier, cardMissionScientist;
    private TextView tvCheckPilot, tvCheckEngineer, tvCheckMedic, tvCheckSoldier, tvCheckScientist;

    private List<CrewMember> inMissionControl = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_control);

        tvCoins = findViewById(R.id.tvCoins);

        // Crew cards
        cardSelectCrewA = findViewById(R.id.cardSelectCrewA);
        cardSelectCrewB = findViewById(R.id.cardSelectCrewB);
        tvCrewABadge    = findViewById(R.id.tvCrewABadge);
        tvCrewBBadge    = findViewById(R.id.tvCrewBBadge);

        tvCrewAName = findViewById(R.id.tvMissionCrewAName);
        tvCrewARole = findViewById(R.id.tvMissionCrewARole);
        tvCrewAStats = findViewById(R.id.tvMissionCrewAStats);
        tvCrewBName = findViewById(R.id.tvMissionCrewBName);
        tvCrewBRole = findViewById(R.id.tvMissionCrewBRole);
        tvCrewBStats = findViewById(R.id.tvMissionCrewBStats);

        // Mission type cards
        cardMissionPilot      = findViewById(R.id.cardMissionPilot);
        cardMissionEngineer   = findViewById(R.id.cardMissionEngineer);
        cardMissionMedic      = findViewById(R.id.cardMissionMedic);
        cardMissionSoldier    = findViewById(R.id.cardMissionSoldier);
        cardMissionScientist  = findViewById(R.id.cardMissionScientist);

        tvCheckPilot     = findViewById(R.id.tvCheckPilot);
        tvCheckEngineer  = findViewById(R.id.tvCheckEngineer);
        tvCheckMedic     = findViewById(R.id.tvCheckMedic);
        tvCheckSoldier   = findViewById(R.id.tvCheckSoldier);
        tvCheckScientist = findViewById(R.id.tvCheckScientist);

        setupCrew();

        // Crew selection toggles
        cardSelectCrewA.setOnClickListener(v -> {
            if (inMissionControl.size() > 0) {
                selectedCrewMember = inMissionControl.get(0);
                refreshCrewUI();
            }
        });
        cardSelectCrewB.setOnClickListener(v -> {
            if (inMissionControl.size() > 1) {
                selectedCrewMember = inMissionControl.get(1);
                refreshCrewUI();
            }
        });

        // Mission type selection
        cardMissionPilot.setOnClickListener(v     -> selectMission("Asteroid Field Navigation"));
        cardMissionEngineer.setOnClickListener(v  -> selectMission("Reactor Meltdown"));
        cardMissionMedic.setOnClickListener(v     -> selectMission("Virus Outbreak"));
        cardMissionSoldier.setOnClickListener(v   -> selectMission("Alien Attack"));
        cardMissionScientist.setOnClickListener(v -> selectMission("Potion Making"));

        // Launch button
        findViewById(R.id.btnLaunchMission).setOnClickListener(v -> {
            if (selectedCrewMember == null) {
                Toast.makeText(this, "Select a crew member!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedMission == null) {
                Toast.makeText(this, "Select a mission type!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Logic for Asteroid Field Navigation
            if ("Asteroid Field Navigation".equals(selectedMission)) {
                if (!"Pilot".equals(selectedCrewMember.role)) {
                    Toast.makeText(this, "Only a Pilot can navigate asteroid fields!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (selectedCrewMember.getSkill() < 9) {
                    Toast.makeText(this, "Pilot skill level must be at least 9!", Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(this, AsteroidAttackActivity.class));
                return;
            }

            // Route Engineer mission to the Engineering Inventory screen
            if ("Reactor Meltdown".equals(selectedMission)) {
                startActivity(new Intent(this, InventoryActivity.class));
                finish();
                return;
            }

            // Determine outcome — skill + luck
            int crewSkill = selectedCrewMember.getSkill();
            int luck = new Random().nextInt(10); // 0–9
            boolean won = (crewSkill + luck) >= 10;

            StatisticsActivity.totalMissions++;
            if (won) {
                StatisticsActivity.missionsWon++;
                GameData.addCoins(GameData.MISSION_WIN_REWARD);
                tvCoins.setText(String.valueOf(GameData.coins));
                Toast.makeText(this,
                    "🏆 MISSION WON!\n" + selectedCrewMember.name + " → " + selectedMission
                    + "\n+5🪙  Coins: " + GameData.coins,
                    Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this,
                    "💀 MISSION FAILED\n" + selectedCrewMember.name + " → " + selectedMission,
                    Toast.LENGTH_LONG).show();
            }
        });

        // Bottom nav
        findViewById(R.id.navSimulator).setOnClickListener(v -> { startActivity(new Intent(this, MainActivity.class)); finish(); });
        findViewById(R.id.navQuarters).setOnClickListener(v -> { startActivity(new Intent(this, QuartersActivity.class)); finish(); });
        findViewById(R.id.navHospital).setOnClickListener(v -> { startActivity(new Intent(this, HospitalActivity.class)); finish(); });
        findViewById(R.id.navStats).setOnClickListener(v -> { startActivity(new Intent(this, StatisticsActivity.class)); finish(); });

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            startActivity(new Intent(this, NavigationActivity.class));
            finish();
        });
    }

    private void setupCrew() {
        inMissionControl.clear();
        for (CrewMember m : GameData.crewList) {
            if ("MissionControl".equals(m.location)) inMissionControl.add(m);
        }

        if (inMissionControl.size() > 0) {
            cardSelectCrewA.setVisibility(View.VISIBLE);
            CrewMember m = inMissionControl.get(0);
            tvCrewAName.setText(m.name);
            tvCrewARole.setText(m.role);
            tvCrewAStats.setText("Skill: " + m.getSkill());
        } else {
            cardSelectCrewA.setVisibility(View.GONE);
        }

        if (inMissionControl.size() > 1) {
            cardSelectCrewB.setVisibility(View.VISIBLE);
            CrewMember m = inMissionControl.get(1);
            tvCrewBName.setText(m.name);
            tvCrewBRole.setText(m.role);
            tvCrewBStats.setText("Skill: " + m.getSkill());
        } else {
            cardSelectCrewB.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tvCoins != null) tvCoins.setText(String.valueOf(GameData.coins));
        setupCrew();
        refreshCrewUI();
    }

    private void refreshCrewUI() {
        cardSelectCrewA.setCardBackgroundColor(0xCCFFFFFF);
        tvCrewABadge.setText("TAP TO SELECT");
        tvCrewABadge.setTextColor(0xFF666666);
        cardSelectCrewB.setCardBackgroundColor(0xCCFFFFFF);
        tvCrewBBadge.setText("TAP TO SELECT");
        tvCrewBBadge.setTextColor(0xFF666666);

        if (selectedCrewMember != null) {
            if (inMissionControl.size() > 0 && selectedCrewMember == inMissionControl.get(0)) {
                cardSelectCrewA.setCardBackgroundColor(0xFF90EE90);
                tvCrewABadge.setText("✓ SELECTED");
                tvCrewABadge.setTextColor(0xFF2E7D32);
            } else if (inMissionControl.size() > 1 && selectedCrewMember == inMissionControl.get(1)) {
                cardSelectCrewB.setCardBackgroundColor(0xFF90EE90);
                tvCrewBBadge.setText("✓ SELECTED");
                tvCrewBBadge.setTextColor(0xFF2E7D32);
            }
        }
    }

    private void selectMission(String mission) {
        selectedMission = mission;
        tvCheckPilot.setText(mission.equals("Asteroid Field Navigation") ? "✓" : "");
        tvCheckEngineer.setText(mission.equals("Reactor Meltdown")       ? "✓" : "");
        tvCheckMedic.setText(mission.equals("Virus Outbreak")            ? "✓" : "");
        tvCheckSoldier.setText(mission.equals("Alien Attack")            ? "✓" : "");
        tvCheckScientist.setText(mission.equals("Potion Making")         ? "✓" : "");

        cardMissionPilot.setCardBackgroundColor(mission.equals("Asteroid Field Navigation") ? 0xFF90EE90 : 0xCCFFFFFF);
        cardMissionEngineer.setCardBackgroundColor(mission.equals("Reactor Meltdown")       ? 0xFF90EE90 : 0xCCFFFFFF);
        cardMissionMedic.setCardBackgroundColor(mission.equals("Virus Outbreak")            ? 0xFF90EE90 : 0xCCFFFFFF);
        cardMissionSoldier.setCardBackgroundColor(mission.equals("Alien Attack")            ? 0xFF90EE90 : 0xCCFFFFFF);
        cardMissionScientist.setCardBackgroundColor(mission.equals("Potion Making")         ? 0xFF90EE90 : 0xCCFFFFFF);
    }
}
