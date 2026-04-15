package com.example.spaceproject;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.HashSet;
import java.util.Set;

public class QuartersActivity extends AppCompatActivity {

    private final Set<CrewMember> selectedCrew = new HashSet<>();

    private TextView     tvCoins, tvNoCrewInQuarters;
    private LinearLayout crewListContainer;
    private View         crewListScroll;
    private Button       btnAddCrew, btnDeleteCrew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quarters);

        tvCoins              = findViewById(R.id.tvCoins);
        tvNoCrewInQuarters   = findViewById(R.id.tvNoCrewInQuarters);
        crewListContainer    = findViewById(R.id.crewListContainer);
        crewListScroll       = findViewById(R.id.crewListScroll);
        btnAddCrew           = findViewById(R.id.btnAddCrew);
        btnDeleteCrew        = findViewById(R.id.btnDeleteCrew);

        updateSpecialButtons();

        btnAddCrew.setOnClickListener(v -> {
            startActivity(new Intent(this, RecruitActivity.class));
            finish();
        });

        btnDeleteCrew.setOnClickListener(v -> {
            if (selectedCrew.isEmpty()) {
                Toast.makeText(this, "Select a crew member to delete!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (GameData.crewList.size() - selectedCrew.size() < 2) {
                Toast.makeText(this, "Minimum 2 crew members must remain in quarters!", Toast.LENGTH_SHORT).show();
                return;
            }
            GameData.crewList.removeAll(selectedCrew);
            selectedCrew.clear();
            Toast.makeText(this, "Crew deleted!", Toast.LENGTH_SHORT).show();
            buildCrewList();
            updateSpecialButtons();
        });

        // Restore Energy — restores currentEnergy back to maxEnergy
        findViewById(R.id.btnRestoreEnergy).setOnClickListener(v -> {
            if (selectedCrew.isEmpty()) {
                Toast.makeText(this, "Select a crew member first!", Toast.LENGTH_SHORT).show();
                return;
            }
            for (CrewMember m : selectedCrew) {
                m.currentEnergy = m.maxEnergy;
            }
            Toast.makeText(this, "Energy restored for " + selectedCrew.size() + " crew member(s)!", Toast.LENGTH_SHORT).show();
            buildCrewList();
        });

        // Move to Mission Control (Now MainActivity)
        findViewById(R.id.btnMoveToMissionControl).setOnClickListener(v -> {
            if (selectedCrew.isEmpty()) {
                Toast.makeText(this, "Select a crew member first!", Toast.LENGTH_SHORT).show();
                return;
            }
            for (CrewMember m : selectedCrew) m.location = "MissionControl";
            selectedCrew.clear();
            Toast.makeText(this, "Crew moved to Mission Control!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        // Move to Simulator (Now MissionControlActivity)
        findViewById(R.id.btnMoveToSimulator).setOnClickListener(v -> {
            if (selectedCrew.isEmpty()) {
                Toast.makeText(this, "Select a crew member first!", Toast.LENGTH_SHORT).show();
                return;
            }
            for (CrewMember m : selectedCrew) m.location = "Simulator";
            selectedCrew.clear();
            Toast.makeText(this, "Crew moved to Simulator!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MissionControlActivity.class));
            finish();
        });

        // Send to Hospital
        findViewById(R.id.btnSendToHospital).setOnClickListener(v -> {
            if (selectedCrew.isEmpty()) {
                Toast.makeText(this, "Select a crew member first!", Toast.LENGTH_SHORT).show();
                return;
            }
            for (CrewMember m : selectedCrew) {
                m.location = "Hospital";
                HospitalActivity.patients.add(
                        new HospitalActivity.Patient(m.name, HospitalActivity.PatientStatus.CRITICAL));
            }
            selectedCrew.clear();
            startActivity(new Intent(this, HospitalActivity.class));
            finish();
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            startActivity(new Intent(this, NavigationActivity.class));
            finish();
        });

        // Bottom nav logic
        findViewById(R.id.navQuarters).setOnClickListener(v -> buildCrewList());
        findViewById(R.id.navSimulator).setOnClickListener(v -> { startActivity(new Intent(this, MainActivity.class)); finish(); });
        findViewById(R.id.navMission).setOnClickListener(v -> { startActivity(new Intent(this, MissionControlActivity.class)); finish(); });
        findViewById(R.id.navHospital).setOnClickListener(v -> { startActivity(new Intent(this, HospitalActivity.class)); finish(); });
        findViewById(R.id.navStats).setOnClickListener(v -> { startActivity(new Intent(this, StatisticsActivity.class)); finish(); });
    }

    private void updateSpecialButtons() {
        if (GameData.successfulMissionsCount >= 3) {
            btnAddCrew.setVisibility(View.VISIBLE);
            btnDeleteCrew.setVisibility(View.VISIBLE);
        } else {
            btnAddCrew.setVisibility(View.GONE);
            btnDeleteCrew.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tvCoins != null) tvCoins.setText(String.valueOf(GameData.coins));
        buildCrewList();
    }

    private void buildCrewList() {
        crewListContainer.removeAllViews();
        if (GameData.crewList.isEmpty()) {
            tvNoCrewInQuarters.setVisibility(View.VISIBLE);
            crewListScroll.setVisibility(View.GONE);
            return;
        }

        tvNoCrewInQuarters.setVisibility(View.GONE);
        crewListScroll.setVisibility(View.VISIBLE);

        for (CrewMember m : GameData.crewList) {
            boolean isSelected = selectedCrew.contains(m);
            CardView card = new CardView(this);
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(-1, -2);
            cardParams.setMargins(0, 0, 0, 12);
            card.setLayoutParams(cardParams);
            card.setRadius(24f);
            card.setCardBackgroundColor(isSelected ? 0xFF2E4A2E : 0xCC1A1A2E);

            LinearLayout inner = new LinearLayout(this);
            inner.setPadding(32, 24, 32, 24);
            inner.setGravity(android.view.Gravity.CENTER_VERTICAL);

            TextView tvIcon = new TextView(this);
            tvIcon.setTextSize(28f);
            tvIcon.setText(roleIcon(m.role));

            LinearLayout info = new LinearLayout(this);
            info.setOrientation(LinearLayout.VERTICAL);
            info.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1f));
            info.setPadding(20, 0, 0, 0);

            TextView tvName = new TextView(this);
            tvName.setText(m.name);
            tvName.setTextColor(0xFFFFFFFF);
            tvName.setTypeface(null, Typeface.BOLD);

            TextView tvRole = new TextView(this);
            tvRole.setText(m.role + " | Skill: " + m.getSkill());
            tvRole.setTextColor(0xFFAADDFF);

            TextView tvStats = new TextView(this);
            tvStats.setText("Energy: " + m.currentEnergy + "/" + m.maxEnergy + " | XP: " + m.experience);
            tvStats.setTextColor(0xFFAAAAAA);

            TextView tvLoc = new TextView(this);
            tvLoc.setText("Loc: " + m.location);
            tvLoc.setTextColor(0xFF888888);
            tvLoc.setTextSize(10f);

            info.addView(tvName);
            info.addView(tvRole);
            info.addView(tvStats);
            info.addView(tvLoc);

            TextView tvBadge = new TextView(this);
            tvBadge.setText(isSelected ? "✓" : "");
            tvBadge.setTextColor(0xFF90EE90);
            tvBadge.setTextSize(20f);

            inner.addView(tvIcon);
            inner.addView(info);
            inner.addView(tvBadge);
            card.addView(inner);

            card.setOnClickListener(v -> {
                if (selectedCrew.contains(m)) selectedCrew.remove(m);
                else selectedCrew.add(m);
                buildCrewList();
            });
            crewListContainer.addView(card);
        }
    }

    private String roleIcon(String role) {
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
