package com.example.spaceproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MissionControlActivity extends AppCompatActivity {

    private boolean crewASelected = false;
    private boolean crewBSelected = false;
    private String selectedMission = null;

    private TextView tvCoins;
    private CardView cardSelectCrewA, cardSelectCrewB;
    private TextView tvCrewABadge, tvCrewBBadge;

    private CardView cardMissionPilot, cardMissionEngineer, cardMissionMedic, cardMissionSoldier, cardMissionScientist;
    private TextView tvCheckPilot, tvCheckEngineer, tvCheckMedic, tvCheckSoldier, tvCheckScientist;

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

        // Crew selection toggles
        cardSelectCrewA.setOnClickListener(v -> {
            crewASelected = !crewASelected;
            refreshCrewUI();
        });
        cardSelectCrewB.setOnClickListener(v -> {
            crewBSelected = !crewBSelected;
            refreshCrewUI();
        });

        // Mission type selection
        cardMissionPilot.setOnClickListener(v     -> selectMission("Asteroid Field Navigation"));
        cardMissionEngineer.setOnClickListener(v  -> selectMission("Reactor Meltdown"));
        cardMissionMedic.setOnClickListener(v     -> selectMission("Virus Outbreak"));
        cardMissionSoldier.setOnClickListener(v   -> selectMission("Alien Attack"));
        cardMissionScientist.setOnClickListener(v -> selectMission("Potion Making"));

        // Launch button
        findViewById(R.id.btnLaunchMission).setOnClickListener(v -> {
            if (!crewASelected && !crewBSelected) {
                Toast.makeText(this, "Select at least one crew member!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedMission == null) {
                Toast.makeText(this, "Select a mission type!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Route Engineer mission to the Engineering Inventory screen
            if ("Reactor Meltdown".equals(selectedMission)) {
                startActivity(new Intent(this, InventoryActivity.class));
                finish();
                return;
            }

            String crew;
            if (crewASelected && crewBSelected) crew = "Alex & Blake";
            else if (crewASelected)             crew = "Alex";
            else                                crew = "Blake";

            // Determine outcome — skill of crew assigned to MissionControl + luck
            int crewSkill = 0;
            boolean foundAssigned = false;
            for (CrewMember m : GameData.crewList) {
                if ("MissionControl".equals(m.location)) {
                    crewSkill += m.getSkill();
                    foundAssigned = true;
                }
            }
            if (!foundAssigned) {
                // Fall back: use any available crew
                for (CrewMember m : GameData.crewList) crewSkill += m.getSkill();
                if (GameData.crewList.isEmpty()) {
                    crewSkill = crewASelected ? 8 : 0;
                    crewSkill += crewBSelected ? 7 : 0;
                }
            }
            int luck = new Random().nextInt(10); // 0–9
            boolean won = (crewSkill + luck) >= 10;

            StatisticsActivity.totalMissions++;
            if (won) {
                StatisticsActivity.missionsWon++;
                GameData.addCoins(GameData.MISSION_WIN_REWARD);
                tvCoins.setText(String.valueOf(GameData.coins));
                Toast.makeText(this,
                    "🏆 MISSION WON!\n" + crew + " → " + selectedMission
                    + "\n+5🪙  Coins: " + GameData.coins,
                    Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this,
                    "💀 MISSION FAILED\n" + crew + " → " + selectedMission,
                    Toast.LENGTH_LONG).show();
            }
        });

        // Bottom nav
        LinearLayout navSimulator = findViewById(R.id.navSimulator);
        LinearLayout navQuarters  = findViewById(R.id.navQuarters);

        navSimulator.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        navQuarters.setOnClickListener(v -> {
            startActivity(new Intent(this, QuartersActivity.class));
            finish();
        });

        LinearLayout navHospital = findViewById(R.id.navHospital);
        navHospital.setOnClickListener(v -> {
            startActivity(new Intent(this, HospitalActivity.class));
            finish();
        });

        LinearLayout navStats = findViewById(R.id.navStats);
        navStats.setOnClickListener(v -> {
            startActivity(new Intent(this, StatisticsActivity.class));
            finish();
        });

        // navMission is the current screen — no action needed

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            startActivity(new Intent(this, NavigationActivity.class));
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tvCoins != null) tvCoins.setText(String.valueOf(GameData.coins));
    }

    private void refreshCrewUI() {
        if (crewASelected) {
            cardSelectCrewA.setCardBackgroundColor(0xFF90EE90);
            tvCrewABadge.setText("✓ SELECTED");
            tvCrewABadge.setTextColor(0xFF2E7D32);
        } else {
            cardSelectCrewA.setCardBackgroundColor(0xCCFFFFFF);
            tvCrewABadge.setText("TAP TO SELECT");
            tvCrewABadge.setTextColor(0xFF666666);
        }

        if (crewBSelected) {
            cardSelectCrewB.setCardBackgroundColor(0xFF90EE90);
            tvCrewBBadge.setText("✓ SELECTED");
            tvCrewBBadge.setTextColor(0xFF2E7D32);
        } else {
            cardSelectCrewB.setCardBackgroundColor(0xCCFFFFFF);
            tvCrewBBadge.setText("TAP TO SELECT");
            tvCrewBBadge.setTextColor(0xFF666666);
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
