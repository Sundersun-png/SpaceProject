package com.example.spaceproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MissionControlActivity extends AppCompatActivity {

    private boolean crewASelected = false;
    private boolean crewBSelected = false;
    private String selectedMission = null;

    private CardView cardSelectCrewA, cardSelectCrewB;
    private TextView tvCrewABadge, tvCrewBBadge;

    private CardView cardMissionPilot, cardMissionEngineer, cardMissionMedic, cardMissionSoldier, cardMissionScientist;
    private TextView tvCheckPilot, tvCheckEngineer, tvCheckMedic, tvCheckSoldier, tvCheckScientist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_control);

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

            String crew;
            if (crewASelected && crewBSelected) crew = "Alex & Blake";
            else if (crewASelected)             crew = "Alex";
            else                                crew = "Blake";

            Toast.makeText(this,
                "Mission launched!\n" + crew + " → " + selectedMission,
                Toast.LENGTH_LONG).show();
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

        // navMission is the current screen — no action needed
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
