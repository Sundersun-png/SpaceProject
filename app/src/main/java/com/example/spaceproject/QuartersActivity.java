package com.example.spaceproject;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QuartersActivity extends AppCompatActivity {

    private final Set<CrewMember> selectedCrew = new HashSet<>();

    private TextView     tvCoins, tvNoCrewInQuarters;
    private LinearLayout crewListContainer;
    private View         crewListScroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quarters);

        tvCoins              = findViewById(R.id.tvCoins);
        tvNoCrewInQuarters   = findViewById(R.id.tvNoCrewInQuarters);
        crewListContainer    = findViewById(R.id.crewListContainer);
        crewListScroll       = findViewById(R.id.crewListScroll);

        // Restore Energy — restores XP bonus for selected crew
        findViewById(R.id.btnRestoreEnergy).setOnClickListener(v -> {
            if (selectedCrew.isEmpty()) {
                Toast.makeText(this, "Select a crew member first!", Toast.LENGTH_SHORT).show();
                return;
            }
            for (CrewMember m : selectedCrew) {
                // Reset experience loss — bring back to base state
                if (m.experience < 0) m.experience = 0;
            }
            Toast.makeText(this, "Energy restored for " + selectedCrew.size() + " crew member(s)!", Toast.LENGTH_SHORT).show();
            buildCrewList();
        });

        // Move to Simulator
        findViewById(R.id.btnMoveToSimulator).setOnClickListener(v -> {
            if (selectedCrew.isEmpty()) {
                Toast.makeText(this, "Select a crew member first!", Toast.LENGTH_SHORT).show();
                return;
            }
            for (CrewMember m : selectedCrew) m.location = "Simulator";
            selectedCrew.clear();
            Toast.makeText(this, "Crew moved to Simulator!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        // Move to Mission Control
        findViewById(R.id.btnMoveToMissionControl).setOnClickListener(v -> {
            if (selectedCrew.isEmpty()) {
                Toast.makeText(this, "Select a crew member first!", Toast.LENGTH_SHORT).show();
                return;
            }
            for (CrewMember m : selectedCrew) m.location = "MissionControl";
            selectedCrew.clear();
            Toast.makeText(this, "Crew moved to Mission Control!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MissionControlActivity.class));
            finish();
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            startActivity(new Intent(this, NavigationActivity.class));
            finish();
        });

        // Bottom nav
        findViewById(R.id.navSimulator).setOnClickListener(v -> { startActivity(new Intent(this, MainActivity.class)); finish(); });
        findViewById(R.id.navMission).setOnClickListener(v   -> { startActivity(new Intent(this, MissionControlActivity.class)); finish(); });
        findViewById(R.id.navHospital).setOnClickListener(v  -> { startActivity(new Intent(this, HospitalActivity.class)); finish(); });
        findViewById(R.id.navStats).setOnClickListener(v     -> { startActivity(new Intent(this, StatisticsActivity.class)); finish(); });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tvCoins != null) tvCoins.setText(String.valueOf(GameData.coins));
        buildCrewList();
    }

    // ── Build the dynamic crew list ───────────────────────────────

    private void buildCrewList() {
        crewListContainer.removeAllViews();

        // Always show all crew members created in Quarters
        List<CrewMember> allCrew = GameData.crewList;

        if (allCrew.isEmpty()) {
            tvNoCrewInQuarters.setVisibility(View.VISIBLE);
            crewListScroll.setVisibility(View.GONE);
            return;
        }

        tvNoCrewInQuarters.setVisibility(View.GONE);
        crewListScroll.setVisibility(View.VISIBLE);

        for (CrewMember m : allCrew) {
            boolean isSelected = selectedCrew.contains(m);

            // ── Card ──────────────────────────────────────────────
            CardView card = new CardView(this);
            CardView.LayoutParams cardParams = new CardView.LayoutParams(
                    CardView.LayoutParams.MATCH_PARENT,
                    CardView.LayoutParams.WRAP_CONTENT);
            cardParams.setMargins(0, 0, 0, 12);
            card.setLayoutParams(cardParams);
            card.setRadius(24f);
            card.setCardElevation(8f);
            card.setCardBackgroundColor(isSelected ? 0xFF2E4A2E : 0xCC1A1A2E);

            // ── Inner layout ──────────────────────────────────────
            LinearLayout inner = new LinearLayout(this);
            inner.setOrientation(LinearLayout.HORIZONTAL);
            inner.setPadding(32, 24, 32, 24);
            inner.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            // Role icon column
            TextView tvIcon = new TextView(this);
            tvIcon.setTextSize(28f);
            tvIcon.setText(roleIcon(m.role));
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            iconParams.setMarginEnd(20);
            tvIcon.setLayoutParams(iconParams);

            // Info column
            LinearLayout info = new LinearLayout(this);
            info.setOrientation(LinearLayout.VERTICAL);
            info.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            TextView tvName = new TextView(this);
            tvName.setText(m.name);
            tvName.setTextColor(0xFFFFFFFF);
            tvName.setTextSize(16f);
            tvName.setTypeface(null, Typeface.BOLD);

            TextView tvRole = new TextView(this);
            tvRole.setText(m.role + " (" + m.location + ")");
            tvRole.setTextColor(0xFFAADDFF);
            tvRole.setTextSize(13f);

            TextView tvStats = new TextView(this);
            tvStats.setText("Skill: " + m.getSkill() + "   XP: " + m.experience);
            tvStats.setTextColor(0xFFAAAAAA);
            tvStats.setTextSize(12f);

            info.addView(tvName);
            info.addView(tvRole);
            info.addView(tvStats);

            // Selected badge
            TextView tvBadge = new TextView(this);
            tvBadge.setText(isSelected ? "✓" : "");
            tvBadge.setTextColor(0xFF90EE90);
            tvBadge.setTextSize(20f);
            tvBadge.setTypeface(null, Typeface.BOLD);

            inner.addView(tvIcon);
            inner.addView(info);
            inner.addView(tvBadge);
            card.addView(inner);

            // Toggle selection on tap
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
            case "Pilot":     return "✈️";
            case "Engineer":  return "⚙️";
            case "Medic":     return "⚕️";
            case "Scientist": return "🔬";
            case "Soldier":   return "🛡️";
            default:          return "👤";
        }
    }
}
