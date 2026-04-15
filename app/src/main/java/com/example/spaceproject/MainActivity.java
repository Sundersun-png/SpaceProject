package com.example.spaceproject;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
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

public class MainActivity extends AppCompatActivity {

    private final Set<CrewMember> selectedCrew = new HashSet<>();
    private LinearLayout crewContainer, selectedCrewImages;
    private TextView tvTrainStatus, tvCoins;
    private Button btnTrain, btnInstantTrain, btnJointMission;

    private CountDownTimer countDownTimer;
    private boolean isTraining = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulator);

        crewContainer = findViewById(R.id.crewContainer);
        selectedCrewImages = findViewById(R.id.selectedCrewImages);
        tvTrainStatus = findViewById(R.id.tvTrainStatus);
        tvCoins = findViewById(R.id.tvCoins);
        btnTrain = findViewById(R.id.btnTrain);
        btnInstantTrain = findViewById(R.id.btnInstantTrain);

        // Dynamic Joint Mission Button creation
        btnJointMission = new Button(this);
        btnJointMission.setText("JOIN MISSION");
        btnJointMission.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF1DB954));
        btnJointMission.setTextColor(0xFFFFFFFF);
        // Calculate 56dp for height to match existing buttons
        int heightInPx = (int) (56 * getResources().getDisplayMetrics().density);
        btnJointMission.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 
                heightInPx));
        btnJointMission.setOnClickListener(v -> {
            boolean allHighSkill = true;
            for (CrewMember m : selectedCrew) {
                if (m.getSkill() < 5) {
                    allHighSkill = false;
                    break;
                }
            }
            if (allHighSkill) {
                List<CrewMember> list = new ArrayList<>(selectedCrew);
                Intent intent = new Intent(this, JointMissionActivity.class);
                intent.putExtra("name1", list.get(0).name);
                intent.putExtra("name2", list.get(1).name);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Both crew members must have skill level >= 5!", Toast.LENGTH_SHORT).show();
            }
        });

        refreshCoins();
        buildCrewCards();

        // ── Normal train: 30-second timer ────────────────────────
        btnTrain.setOnClickListener(v -> {
            if (isTraining) return;
            if (selectedCrew.isEmpty()) {
                Toast.makeText(this, "Select crew members to train!", Toast.LENGTH_SHORT).show();
                return;
            }
            startTraining();
        });

        // ── Instant train: costs 5 coins ─────────────────────────
        btnInstantTrain.setOnClickListener(v -> {
            if (selectedCrew.isEmpty()) {
                Toast.makeText(this, "Select crew members to train!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (GameData.coins < GameData.INSTANT_TRAIN_COST) {
                Toast.makeText(this,
                    "Not enough coins! Need " + GameData.INSTANT_TRAIN_COST + "🪙",
                    Toast.LENGTH_SHORT).show();
                return;
            }
            if (countDownTimer != null) countDownTimer.cancel();
            isTraining = false;
            GameData.coins -= GameData.INSTANT_TRAIN_COST;
            completeTraining();
        });

        // ── Bottom nav ────────────────────────────────────────────
        findViewById(R.id.navMission).setOnClickListener(v -> startActivity(new Intent(this, MissionControlActivity.class)));
        findViewById(R.id.navQuarters).setOnClickListener(v -> startActivity(new Intent(this, QuartersActivity.class)));
        findViewById(R.id.navHospital).setOnClickListener(v -> startActivity(new Intent(this, HospitalActivity.class)));
        findViewById(R.id.navStats).setOnClickListener(v -> startActivity(new Intent(this, StatisticsActivity.class)));

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            startActivity(new Intent(this, NavigationActivity.class));
            finish();
        });
    }

    private void buildCrewCards() {
        crewContainer.removeAllViews();
        if (GameData.crewList.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No crew members found. Recruitment is needed!");
            empty.setTextColor(0xFFFFFFFF);
            empty.setGravity(Gravity.CENTER);
            crewContainer.addView(empty);
            return;
        }

        for (CrewMember m : GameData.crewList) {
            boolean isSelected = selectedCrew.contains(m);
            CardView card = new CardView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 200);
            params.setMargins(0, 0, 0, 16);
            card.setLayoutParams(params);
            card.setRadius(24f);
            card.setCardBackgroundColor(isSelected ? 0xFF90EE90 : 0xCCFFFFFF);

            LinearLayout inner = new LinearLayout(this);
            inner.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
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
            tvStats.setText("XP: " + m.experience + "  Skill: " + m.getSkill());
            tvStats.setTextSize(12f);
            tvStats.setTextColor(0xFF444444);

            info.addView(tvName);
            info.addView(tvStats);
            inner.addView(tvIcon);
            inner.addView(info);
            card.addView(inner);

            card.setOnClickListener(v -> {
                if (isTraining) return;
                if (selectedCrew.contains(m)) selectedCrew.remove(m);
                else selectedCrew.add(m);
                buildCrewCards();
                updateTrainingImages();
            });

            crewContainer.addView(card);
        }
    }

    private void updateTrainingImages() {
        selectedCrewImages.removeAllViews();
        for (CrewMember m : selectedCrew) {
            android.widget.ImageView iv = new android.widget.ImageView(this);
            int resId = getCrewDrawable(m.role);
            iv.setImageResource(resId);
            
            // Layout params for the character image inside the circle
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, -1, 1f);
            iv.setLayoutParams(lp);
            iv.setScaleType(android.widget.ImageView.ScaleType.FIT_CENTER);
            
            selectedCrewImages.addView(iv);
        }

        // Toggle Joint Mission button visibility
        LinearLayout btnRow = findViewById(R.id.btnRow);
        if (selectedCrew.size() == 2) {
            btnTrain.setVisibility(View.GONE);
            btnInstantTrain.setVisibility(View.GONE);
            if (btnJointMission.getParent() == null) {
                btnRow.addView(btnJointMission);
            }
            btnJointMission.setVisibility(View.VISIBLE);
        } else {
            btnTrain.setVisibility(View.VISIBLE);
            btnInstantTrain.setVisibility(View.VISIBLE);
            btnJointMission.setVisibility(View.GONE);
        }
    }

    private int getCrewDrawable(String role) {
        switch (role) {
            case "Pilot":     return R.drawable.pilot;
            case "Engineer":  return R.drawable.engineer;
            case "Medic":     return R.drawable.medic;
            case "Scientist": return R.drawable.scientist;
            case "Soldier":   return R.drawable.soldier;
            default:          return R.drawable.ic_launcher_foreground;
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

    private void startTraining() {
        isTraining = true;
        btnTrain.setEnabled(false);
        btnInstantTrain.setEnabled(false);
        tvTrainStatus.setText("Training... 30s");

        countDownTimer = new CountDownTimer(30_000, 1_000) {
            @Override
            public void onTick(long msLeft) {
                tvTrainStatus.setText("Training... " + (msLeft / 1000) + "s");
            }
            @Override
            public void onFinish() {
                completeTraining();
            }
        }.start();
    }

    private void completeTraining() {
        isTraining = false;
        for (CrewMember m : selectedCrew) {
            m.train(0);
        }
        buildCrewCards();
        refreshCoins();
        tvTrainStatus.setText("✓ Training complete!");
        btnTrain.setEnabled(true);
        btnInstantTrain.setEnabled(true);
        Toast.makeText(this, "Training complete for selected crew!", Toast.LENGTH_SHORT).show();
    }

    private void refreshCoins() {
        tvCoins.setText(String.valueOf(GameData.coins));
    }
}
