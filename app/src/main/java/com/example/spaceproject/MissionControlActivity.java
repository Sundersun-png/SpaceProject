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
 * MISSION CONTROL activity. (Swapped from Simulator)
 * Handles Joint Missions.
 */
public class MissionControlActivity extends AppCompatActivity {

    private final Set<CrewMember> selectedCrew = new HashSet<>();
    private LinearLayout crewContainer, selectedCrewImages;
    private TextView tvStatus, tvCoins;
    private Button btnLaunchJointMission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulator);

        crewContainer = findViewById(R.id.crewContainer);
        selectedCrewImages = findViewById(R.id.selectedCrewImages);
        tvStatus = findViewById(R.id.tvStatus);
        tvCoins = findViewById(R.id.tvCoins);
        btnLaunchJointMission = findViewById(R.id.btnLaunchJointMission);

        refreshCoins();
        buildCrewCards();

        setupListeners();

        // ── Bottom nav (Swapped mapping) ──
        findViewById(R.id.navMission).setOnClickListener(v -> {
            // Mission nav button now goes to MissionControlActivity (itself)
        });
        findViewById(R.id.navSimulator).setOnClickListener(v -> {
            // Simulator nav button now goes to MainActivity (Single Missions)
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
        findViewById(R.id.navQuarters).setOnClickListener(v -> {
            startActivity(new Intent(this, QuartersActivity.class));
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

    private void setupListeners() {
        btnLaunchJointMission.setOnClickListener(v -> {
            if (selectedCrew.size() != 2) {
                Toast.makeText(this, "Select exactly 2 crew members for a Joint Mission!", Toast.LENGTH_SHORT).show();
                return;
            }
            List<CrewMember> list = new ArrayList<>(selectedCrew);
            Intent intent = new Intent(this, JointMissionActivity.class);
            intent.putExtra("crewA", list.get(0));
            intent.putExtra("crewB", list.get(1));
            startActivity(intent);
        });
    }

    private void buildCrewCards() {
        crewContainer.removeAllViews();
        List<CrewMember> availableCrew = new ArrayList<>();
        for (CrewMember m : GameData.crewList) {
            // Now checks for "MissionControl" location
            if ("MissionControl".equals(m.location)) availableCrew.add(m);
        }

        if (availableCrew.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No crew in Mission Control. Move them from Quarters.");
            empty.setTextColor(0xFFFFFFFF);
            empty.setGravity(Gravity.CENTER);
            crewContainer.addView(empty);
            return;
        }

        for (CrewMember m : availableCrew) {
            boolean isSelected = selectedCrew.contains(m);
            CardView card = new CardView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, 200);
            params.setMargins(0, 0, 0, 16);
            card.setLayoutParams(params);
            card.setRadius(24f);
            card.setCardBackgroundColor(isSelected ? 0xFF90EE90 : 0xCCFFFFFF);

            LinearLayout inner = new LinearLayout(this);
            inner.setPadding(30, 0, 30, 0);
            inner.setGravity(Gravity.CENTER_VERTICAL);
            
            TextView tvIcon = new TextView(this);
            tvIcon.setText(roleIcon(m.role));
            tvIcon.setTextSize(30f);
            
            LinearLayout info = new LinearLayout(this);
            info.setOrientation(LinearLayout.VERTICAL);
            info.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1f));
            info.setPadding(30, 0, 0, 0);

            TextView tvName = new TextView(this);
            tvName.setText(m.name);
            tvName.setTextSize(14f);
            tvName.setTypeface(null, Typeface.BOLD);
            tvName.setTextColor(0xFF000000);

            TextView tvStats = new TextView(this);
            tvStats.setText("XP: " + m.experience + " | Skill: " + m.getSkill());
            tvStats.setTextColor(0xFF444444);

            info.addView(tvName);
            info.addView(tvStats);
            inner.addView(tvIcon);
            inner.addView(info);
            card.addView(inner);

            card.setOnClickListener(v -> {
                if (selectedCrew.contains(m)) {
                    selectedCrew.remove(m);
                } else if (selectedCrew.size() < 2) {
                    selectedCrew.add(m);
                } else {
                    Toast.makeText(this, "Maximum 2 crew members for joint mission.", Toast.LENGTH_SHORT).show();
                }
                buildCrewCards();
                updateTrainingImages();
                updateStatus();
            });
            crewContainer.addView(card);
        }
    }

    private void updateStatus() {
        if (selectedCrew.size() == 2) {
            tvStatus.setText("✓ 2 crew members ready for mission");
        } else {
            tvStatus.setText("Select exactly 2 crew members (" + selectedCrew.size() + "/2)");
        }
    }

    private void updateTrainingImages() {
        selectedCrewImages.removeAllViews();
        for (CrewMember m : selectedCrew) {
            android.widget.ImageView iv = new android.widget.ImageView(this);
            iv.setImageResource(getCrewDrawable(m.role));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, -1, 1f);
            iv.setLayoutParams(lp);
            iv.setScaleType(android.widget.ImageView.ScaleType.FIT_CENTER);
            selectedCrewImages.addView(iv);
        }
    }

    private int getCrewDrawable(String role) {
        if (role == null) return R.drawable.ic_launcher_foreground;
        switch (role) {
            case "Pilot": return R.drawable.pilot;
            case "Engineer": return R.drawable.engineer;
            case "Medic": return R.drawable.medic;
            case "Scientist": return R.drawable.scientist;
            case "Soldier": return R.drawable.soldier;
            default: return R.drawable.ic_launcher_foreground;
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

    private void refreshCoins() {
        if (tvCoins != null) tvCoins.setText(String.valueOf(GameData.coins));
    }
}
