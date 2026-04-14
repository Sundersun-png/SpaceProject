package com.example.spaceproject;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.HashSet;
import java.util.Random;
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

        // Mission type selection
        cardMissionPilot.setOnClickListener(v     -> selectMission("Asteroid Field Navigation"));
        cardMissionEngineer.setOnClickListener(v  -> selectMission("Reactor Meltdown"));
        cardMissionMedic.setOnClickListener(v     -> selectMission("Virus Outbreak"));
        cardMissionSoldier.setOnClickListener(v   -> selectMission("Alien Attack"));
        cardMissionScientist.setOnClickListener(v -> selectMission("Potion Making"));

        // Launch button
        findViewById(R.id.btnLaunchMission).setOnClickListener(v -> {
            if (selectedCrew.isEmpty()) {
                Toast.makeText(this, "Select at least one crew member!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedMission == null) {
                Toast.makeText(this, "Select a mission type!", Toast.LENGTH_SHORT).show();
                return;
            }

            if ("Reactor Meltdown".equals(selectedMission)) {
                startActivity(new Intent(this, InventoryActivity.class));
                finish();
                return;
            }

            if ("Potion Making".equals(selectedMission)) {
                startActivity(new Intent(this, ScientistLabActivity.class));
                finish();
                return;
            }

            if ("Alien Attack".equals(selectedMission)) {
                startActivity(new Intent(this, SoldierMissionActivity.class));
                finish();
                return;
            }

            // Build crew name string and sum skill
            StringBuilder names = new StringBuilder();
            int crewSkill = 0;
            for (CrewMember m : selectedCrew) {
                if (names.length() > 0) names.append(" & ");
                names.append(m.name);
                crewSkill += m.getSkill();
            }

            int luck = new Random().nextInt(10);
            boolean won = (crewSkill + luck) >= 10;

            StatisticsActivity.totalMissions++;
            if (won) {
                StatisticsActivity.missionsWon++;
                GameData.addCoins(GameData.MISSION_WIN_REWARD);
                tvCoins.setText(String.valueOf(GameData.coins));
                Toast.makeText(this,
                    "🏆 MISSION WON!\n" + names + " → " + selectedMission
                    + "\n+5🪙  Coins: " + GameData.coins,
                    Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this,
                    "💀 MISSION FAILED\n" + names + " → " + selectedMission,
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

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            startActivity(new Intent(this, NavigationActivity.class));
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tvCoins != null) tvCoins.setText(String.valueOf(GameData.coins));
        buildCrewCards();
    }

    // ── Build one card per crew member ────────────────────────────────────────

    private void buildCrewCards() {
        crewSelectionContainer.removeAllViews();

        if (GameData.crewList.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No crew recruited yet");
            empty.setTextColor(0xFF888888);
            empty.setTextSize(14f);
            empty.setPadding(0, 8, 0, 8);
            crewSelectionContainer.addView(empty);
            return;
        }

        for (CrewMember m : GameData.crewList) {
            boolean isSelected = selectedCrew.contains(m);

            CardView card = new CardView(this);
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            cardParams.setMargins(0, 0, 0, 8);
            card.setLayoutParams(cardParams);
            card.setRadius(24f);
            card.setCardElevation(6f);
            card.setCardBackgroundColor(isSelected ? 0xFF90EE90 : 0xCCFFFFFF);

            LinearLayout inner = new LinearLayout(this);
            inner.setOrientation(LinearLayout.HORIZONTAL);
            inner.setPadding(24, 20, 24, 20);
            inner.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            inner.setGravity(android.view.Gravity.CENTER_VERTICAL);

            // Role icon
            TextView tvIcon = new TextView(this);
            tvIcon.setTextSize(26f);
            tvIcon.setText(roleIcon(m.role));
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            iconParams.setMarginEnd(16);
            tvIcon.setLayoutParams(iconParams);

            // Info column
            LinearLayout info = new LinearLayout(this);
            info.setOrientation(LinearLayout.VERTICAL);
            info.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            TextView tvName = new TextView(this);
            tvName.setText(m.name);
            tvName.setTextColor(0xFF111111);
            tvName.setTextSize(14f);
            tvName.setTypeface(null, Typeface.BOLD);

            TextView tvRole = new TextView(this);
            tvRole.setText(m.role);
            tvRole.setTextColor(0xFF444444);
            tvRole.setTextSize(12f);

            TextView tvStats = new TextView(this);
            tvStats.setText("Skill: " + m.getSkill());
            tvStats.setTextColor(0xFF666666);
            tvStats.setTextSize(11f);

            info.addView(tvName);
            info.addView(tvRole);
            info.addView(tvStats);

            // Badge
            TextView tvBadge = new TextView(this);
            tvBadge.setText(isSelected ? "✓ SELECTED" : "TAP TO SELECT");
            tvBadge.setTextSize(10f);
            tvBadge.setTextColor(isSelected ? 0xFF2E7D32 : 0xFF666666);
            tvBadge.setTypeface(null, isSelected ? Typeface.BOLD : Typeface.NORMAL);

            inner.addView(tvIcon);
            inner.addView(info);
            inner.addView(tvBadge);
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
        switch (role) {
            case "Pilot":     return "✈️";
            case "Engineer":  return "⚙️";
            case "Medic":     return "⚕️";
            case "Scientist": return "🔬";
            case "Soldier":   return "🛡️";
            default:          return "👤";
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
