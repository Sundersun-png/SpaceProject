package com.example.spaceproject;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.HashSet;
import java.util.Set;

public class MissionControlActivity extends AppCompatActivity {

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

        cardMissionPilot.setOnClickListener(v     -> selectMission(GameData.MISSION_ASTEROID));
        cardMissionEngineer.setOnClickListener(v  -> selectMission(GameData.MISSION_REACTOR));
        cardMissionMedic.setOnClickListener(v     -> selectMission(GameData.MISSION_VIRUS));
        cardMissionSoldier.setOnClickListener(v   -> selectMission(GameData.MISSION_ALIEN));
        cardMissionScientist.setOnClickListener(v -> selectMission(GameData.MISSION_POTION));

        findViewById(R.id.btnLaunchMission).setOnClickListener(v -> {
            if (selectedCrew.isEmpty()) {
                Toast.makeText(this, "Select a crew member!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedMission == null) {
                Toast.makeText(this, "Select a mission type!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedCrew.size() > 1) {
                Toast.makeText(this, "Select only 1 specialized crew member for this mission!", Toast.LENGTH_SHORT).show();
                return;
            }

            CrewMember member = selectedCrew.iterator().next();

            // Strict Role Restrictions
            if (GameData.MISSION_ASTEROID.equals(selectedMission)) {
                if (!"Pilot".equals(member.role)) {
                    Toast.makeText(this, "Only the Pilot can go to Asteroid Field Navigation!", Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(this, AsteroidAttackActivity.class));
                finish();
            } else if (GameData.MISSION_REACTOR.equals(selectedMission)) {
                if (!"Engineer".equals(member.role)) {
                    Toast.makeText(this, "Only the Engineer can go to Reactor Meltdown!", Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(this, EngineerMissionActivity.class));
                finish();
            } else if (GameData.MISSION_POTION.equals(selectedMission)) {
                if (!"Scientist".equals(member.role)) {
                    Toast.makeText(this, "Only the Scientist can go to Potion Making!", Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(this, ScientistLabActivity.class));
                finish();
            } else if (GameData.MISSION_ALIEN.equals(selectedMission)) {
                if (!"Soldier".equals(member.role)) {
                    Toast.makeText(this, "Only the Soldier can go to Alien Attack!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(this, "Launching Alien Attack...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Launching " + selectedMission + "...", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.navSimulator).setOnClickListener(v -> { startActivity(new Intent(this, MainActivity.class)); finish(); });
        findViewById(R.id.navQuarters).setOnClickListener(v -> { startActivity(new Intent(this, QuartersActivity.class)); finish(); });
        findViewById(R.id.navHospital).setOnClickListener(v -> { startActivity(new Intent(this, HospitalActivity.class)); finish(); });
        findViewById(R.id.navStats).setOnClickListener(v -> { startActivity(new Intent(this, StatisticsActivity.class)); finish(); });
        findViewById(R.id.btnBack).setOnClickListener(v -> { startActivity(new Intent(this, NavigationActivity.class)); finish(); });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tvCoins != null) tvCoins.setText(String.valueOf(GameData.coins));
        buildCrewCards();
    }

    private void buildCrewCards() {
        crewSelectionContainer.removeAllViews();
        if (GameData.crewList.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No crew recruited yet");
            empty.setTextColor(0xFF888888);
            crewSelectionContainer.addView(empty);
            return;
        }

        for (CrewMember m : GameData.crewList) {
            boolean isSelected = selectedCrew.contains(m);
            CardView card = new CardView(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
            lp.setMargins(0, 0, 0, 8);
            card.setLayoutParams(lp);
            card.setRadius(24f);
            card.setCardBackgroundColor(isSelected ? 0xFF90EE90 : 0xCCFFFFFF);

            LinearLayout inner = new LinearLayout(this);
            inner.setPadding(24, 20, 24, 20);
            inner.setGravity(Gravity.CENTER_VERTICAL);

            TextView tvIcon = new TextView(this);
            tvIcon.setText(roleIcon(m.role));
            tvIcon.setTextSize(26f);

            LinearLayout info = new LinearLayout(this);
            info.setOrientation(LinearLayout.VERTICAL);
            info.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1f));
            info.setPadding(16, 0, 0, 0);

            TextView tvName = new TextView(this);
            tvName.setText(m.name);
            tvName.setTypeface(null, Typeface.BOLD);
            TextView tvRole = new TextView(this);
            tvRole.setText(m.role);
            TextView tvStats = new TextView(this);
            tvStats.setText("Skill: " + m.getSkill());

            info.addView(tvName); info.addView(tvRole); info.addView(tvStats);
            inner.addView(tvIcon); inner.addView(info);
            card.addView(inner);

            card.setOnClickListener(v -> {
                selectedCrew.clear();
                selectedCrew.add(m);
                buildCrewCards();
            });
            crewSelectionContainer.addView(card);
        }
    }

    private String roleIcon(String role) {
        switch (role) {
            case "Pilot": return "✈️"; case "Engineer": return "⚙️";
            case "Medic": return "⚕️"; case "Scientist": return "🔬";
            case "Soldier": return "🛡️"; default: return "👤";
        }
    }

    private void selectMission(String mission) {
        selectedMission = mission;
        tvCheckPilot.setText(mission.equals(GameData.MISSION_ASTEROID) ? "✓" : "");
        tvCheckEngineer.setText(mission.equals(GameData.MISSION_REACTOR) ? "✓" : "");
        tvCheckMedic.setText(mission.equals(GameData.MISSION_VIRUS) ? "✓" : "");
        tvCheckSoldier.setText(mission.equals(GameData.MISSION_ALIEN) ? "✓" : "");
        tvCheckScientist.setText(mission.equals(GameData.MISSION_POTION) ? "✓" : "");
    }
}
