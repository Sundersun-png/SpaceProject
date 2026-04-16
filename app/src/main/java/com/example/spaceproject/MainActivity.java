package com.example.spaceproject;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * SIMULATOR ACTIVITY (Single Missions)
 */
public class MainActivity extends AppCompatActivity {

    private final Set<CrewMember> selectedCrew = new HashSet<>();
    private String selectedMission = null;

    private TextView tvCoins;
    private LinearLayout crewSelectionContainer;

    private CardView cardMissionPilot, cardMissionEngineer, cardMissionMedic, cardMissionSoldier, cardMissionScientist;
    private TextView tvCheckPilot, tvCheckEngineer, tvCheckMedic, tvCheckSoldier, tvCheckScientist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_control);

        // Find all views
        tvCoins               = findViewById(R.id.tvCoins);
        crewSelectionContainer = findViewById(R.id.crewSelectionContainer);

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

        // Mission selection listeners
        cardMissionPilot.setOnClickListener(v     -> selectMission(GameData.MISSION_ASTEROID));
        cardMissionEngineer.setOnClickListener(v  -> selectMission(GameData.MISSION_REACTOR));
        cardMissionMedic.setOnClickListener(v     -> selectMission(GameData.MISSION_VIRUS));
        cardMissionSoldier.setOnClickListener(v   -> selectMission(GameData.MISSION_ALIEN));
        cardMissionScientist.setOnClickListener(v -> selectMission(GameData.MISSION_POTION));

        findViewById(R.id.btnLaunchMission).setOnClickListener(v -> launchMission());

        // --- Navigation (Simulator mapping) ---
        findViewById(R.id.navQuarters).setOnClickListener(v -> {
            startActivity(new Intent(this, QuartersActivity.class));
            finish();
        });
        findViewById(R.id.navSimulator).setOnClickListener(v -> {
            // Already here
        });
        findViewById(R.id.navMission).setOnClickListener(v -> {
            startActivity(new Intent(this, MissionControlActivity.class));
            finish();
        });
        findViewById(R.id.navHospital).setOnClickListener(v -> {
            startActivity(new Intent(this, HospitalActivity.class));
            finish();
        });
        findViewById(R.id.navStats).setOnClickListener(v -> {
            startActivity(new Intent(this, StatisticsActivity.class));
            finish();
        });
        
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            startActivity(new Intent(this, NavigationActivity.class));
            finish();
        });
    }

    private void selectMission(String mission) {
        selectedMission = mission;
        if (tvCheckPilot != null) tvCheckPilot.setText(GameData.MISSION_ASTEROID.equals(mission) ? "✓" : "");
        if (tvCheckEngineer != null) tvCheckEngineer.setText(GameData.MISSION_REACTOR.equals(mission) ? "✓" : "");
        if (tvCheckMedic != null) tvCheckMedic.setText(GameData.MISSION_VIRUS.equals(mission) ? "✓" : "");
        if (tvCheckSoldier != null) tvCheckSoldier.setText(GameData.MISSION_ALIEN.equals(mission) ? "✓" : "");
        if (tvCheckScientist != null) tvCheckScientist.setText(GameData.MISSION_POTION.equals(mission) ? "✓" : "");

        if (cardMissionPilot != null) cardMissionPilot.setCardBackgroundColor(GameData.MISSION_ASTEROID.equals(mission) ? 0xFF90EE90 : 0xCCFFFFFF);
        if (cardMissionEngineer != null) cardMissionEngineer.setCardBackgroundColor(GameData.MISSION_REACTOR.equals(mission) ? 0xFF90EE90 : 0xCCFFFFFF);
        if (cardMissionMedic != null) cardMissionMedic.setCardBackgroundColor(GameData.MISSION_VIRUS.equals(mission) ? 0xFF90EE90 : 0xCCFFFFFF);
        if (cardMissionSoldier != null) cardMissionSoldier.setCardBackgroundColor(GameData.MISSION_ALIEN.equals(mission) ? 0xFF90EE90 : 0xCCFFFFFF);
        if (cardMissionScientist != null) cardMissionScientist.setCardBackgroundColor(GameData.MISSION_POTION.equals(mission) ? 0xFF90EE90 : 0xCCFFFFFF);
    }

    private void launchMission() {
        if (selectedCrew.isEmpty()) {
            Toast.makeText(this, "Select at least one crew member!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedMission == null) {
            Toast.makeText(this, "Select a mission type!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if any selected crew is in Hospital
        for (CrewMember m : selectedCrew) {
            if ("Hospital".equalsIgnoreCase(m.location)) {
                Toast.makeText(this, m.name + " is in Hospital and cannot join missions!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (GameData.MISSION_ASTEROID.equals(selectedMission)) {
            if (!hasRole("Pilot")) {
                Toast.makeText(this, "Only a Pilot can launch this mission!", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(this, AsteroidAttackActivity.class));
        } else if (GameData.MISSION_ALIEN.equals(selectedMission)) {
            if (!hasRole("Soldier")) {
                Toast.makeText(this, "Only a Soldier can launch this mission!", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(this, SoldierMissionActivity.class));
        } else if (GameData.MISSION_REACTOR.equals(selectedMission)) {
            if (!hasRole("Engineer")) {
                Toast.makeText(this, "Only an Engineer can launch this mission!", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(this, EngineerMissionActivity.class));
        } else if (GameData.MISSION_POTION.equals(selectedMission)) {
            if (!hasRole("Scientist")) {
                Toast.makeText(this, "Only a Scientist can launch this mission!", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(this, ScientistTrainingActivity.class));
        } else if (GameData.MISSION_VIRUS.equals(selectedMission)) {
            if (!hasRole("Medic")) {
                Toast.makeText(this, "Only a Medic can launch this mission!", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(this, MedicLabActivity.class));
        } else {
            Toast.makeText(this, "Mission started: " + selectedMission, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean hasRole(String role) {
        for (CrewMember m : selectedCrew) {
            if (role.equalsIgnoreCase(m.role)) return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tvCoins != null) tvCoins.setText(String.valueOf(GameData.coins));
        buildCrewCards();
    }

    private void buildCrewCards() {
        crewSelectionContainer.removeAllViews();
        List<CrewMember> available = new ArrayList<>();
        for (CrewMember m : GameData.crewList) {
            // Simulator location check remains, but logic will block starting if they moved to Hospital
            if ("Simulator".equals(m.location)) available.add(m);
        }

        if (available.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No crew in Simulator. Move them from Quarters.");
            empty.setTextColor(0xFFBBBBBB);
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(0, 20, 0, 20);
            crewSelectionContainer.addView(empty);
            return;
        }

        for (CrewMember m : available) {
            boolean isSelected = selectedCrew.contains(m);
            CardView card = new CardView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
            params.setMargins(0, 0, 0, 12);
            card.setLayoutParams(params);
            card.setRadius(24f);
            card.setCardBackgroundColor(isSelected ? 0xFF90EE90 : 0xCCFFFFFF);

            LinearLayout inner = new LinearLayout(this);
            inner.setPadding(30, 20, 30, 20);
            inner.setGravity(android.view.Gravity.CENTER_VERTICAL);

            TextView tvIcon = new TextView(this);
            tvIcon.setText(roleIcon(m.role));
            tvIcon.setTextSize(26f);

            LinearLayout info = new LinearLayout(this);
            info.setOrientation(LinearLayout.VERTICAL);
            info.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1f));
            info.setPadding(30, 0, 0, 0);

            TextView tvName = new TextView(this);
            tvName.setText(m.name);
            tvName.setTypeface(null, Typeface.BOLD);
            tvName.setTextColor(0xFF000000);

            TextView tvStats = new TextView(this);
            tvStats.setText(m.role + " | Skill: " + m.getSkill() + " | Energy: " + m.currentEnergy);
            tvStats.setTextSize(11f);
            tvStats.setTextColor(0xFF444444);

            info.addView(tvName);
            info.addView(tvStats);
            inner.addView(tvIcon);
            inner.addView(info);
            card.addView(inner);

            card.setOnClickListener(v -> {
                if (selectedCrew.contains(m)) selectedCrew.remove(m);
                else selectedCrew.add(m);
                buildCrewCards();
            });
            crewSelectionContainer.addView(card);
        }
    }

    private String roleIcon(String role) {
        if (role == null) return "👤";
        switch (role) {
            case "Pilot": return "✈️";
            case "Engineer": return "⚙️";
            case "Medic": return "⚕️";
            case "Scientist": return "🔬";
            case "Soldier": return "🛡️";
            default: return "👤";
        }
    }
}
