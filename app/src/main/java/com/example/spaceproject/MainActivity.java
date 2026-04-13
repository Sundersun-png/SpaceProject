package com.example.spaceproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {

    private CrewMember crewA;
    private CrewMember crewB;

    private CardView cardCrewA, cardCrewB;
    private TextView tvCrewAName, tvCrewARole, tvCrewAStats;
    private TextView tvCrewBName, tvCrewBRole, tvCrewBStats;
    private TextView tvTrainStatus, tvCoins;
    private Button   btnTrain, btnInstantTrain;

    private CountDownTimer countDownTimer;
    private boolean isTraining = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulator);

        // Find views
        cardCrewA      = findViewById(R.id.cardCrewA);
        cardCrewB      = findViewById(R.id.cardCrewB);
        tvCrewAName    = findViewById(R.id.tvCrewAName);
        tvCrewARole    = findViewById(R.id.tvCrewARole);
        tvCrewAStats   = findViewById(R.id.tvCrewAStats);
        tvCrewBName    = findViewById(R.id.tvCrewBName);
        tvCrewBRole    = findViewById(R.id.tvCrewBRole);
        tvCrewBStats   = findViewById(R.id.tvCrewBStats);
        tvTrainStatus  = findViewById(R.id.tvTrainStatus);
        tvCoins        = findViewById(R.id.tvCoins);
        btnTrain       = findViewById(R.id.btnTrain);
        btnInstantTrain= findViewById(R.id.btnInstantTrain);

        setupCrew();
        updateCrewUI();
        refreshCoins();

        // ── Normal train: 30-second timer ────────────────────────
        btnTrain.setOnClickListener(v -> {
            if (isTraining) return;
            if (crewA == null && crewB == null) {
                Toast.makeText(this, "No crew assigned to Simulator!", Toast.LENGTH_SHORT).show();
                return;
            }
            startTraining();
        });

        // ── Instant train: costs 5 coins ─────────────────────────
        btnInstantTrain.setOnClickListener(v -> {
            if (crewA == null && crewB == null) {
                Toast.makeText(this, "No crew assigned to Simulator!", Toast.LENGTH_SHORT).show();
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
        findViewById(R.id.navMission).setOnClickListener(v  -> { startActivity(new Intent(this, MissionControlActivity.class)); finish(); });
        findViewById(R.id.navQuarters).setOnClickListener(v -> { startActivity(new Intent(this, QuartersActivity.class)); finish(); });
        findViewById(R.id.navHospital).setOnClickListener(v -> { startActivity(new Intent(this, HospitalActivity.class)); finish(); });
        findViewById(R.id.navStats).setOnClickListener(v    -> { startActivity(new Intent(this, StatisticsActivity.class)); finish(); });

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            startActivity(new Intent(this, NavigationActivity.class));
            finish();
        });
    }

    private void setupCrew() {
        // Only show crew whose location is "Simulator"
        List<CrewMember> inSim = new ArrayList<>();
        for (CrewMember m : GameData.crewList) {
            if ("Simulator".equals(m.location)) inSim.add(m);
        }

        crewA = inSim.size() > 0 ? inSim.get(0) : null;
        crewB = inSim.size() > 1 ? inSim.get(1) : null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCoins();
        setupCrew();
        updateCrewUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }

    // ── Training logic ────────────────────────────────────────────

    private void startTraining() {
        isTraining = true;
        btnTrain.setEnabled(false);
        btnTrain.setText("TRAINING...");
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
        if (crewA != null) crewA.train(0);
        if (crewB != null) crewB.train(0);
        updateCrewUI();
        refreshCoins();
        tvTrainStatus.setText("✓ Training complete!");
        btnTrain.setEnabled(true);
        btnTrain.setText("TRAIN  (30s)");
        Toast.makeText(this, "Training complete!", Toast.LENGTH_SHORT).show();
    }

    // ── UI helpers ────────────────────────────────────────────────

    private void updateCrewUI() {
        if (crewA != null) {
            cardCrewA.setVisibility(View.VISIBLE);
            tvCrewAName.setText(crewA.name);
            tvCrewARole.setText(crewA.role);
            tvCrewAStats.setText("XP: " + crewA.experience + "    Skill: " + crewA.getSkill());
        } else {
            cardCrewA.setVisibility(View.GONE);
        }

        if (crewB != null) {
            cardCrewB.setVisibility(View.VISIBLE);
            tvCrewBName.setText(crewB.name);
            tvCrewBRole.setText(crewB.role);
            tvCrewBStats.setText("XP: " + crewB.experience + "    Skill: " + crewB.getSkill());
        } else {
            cardCrewB.setVisibility(View.GONE);
        }

        if (crewA == null && crewB == null) {
            tvTrainStatus.setText("No crew assigned to Simulator. Go to Quarters to assign crew.");
        } else if (!isTraining) {
            tvTrainStatus.setText("Ready to train");
        }
    }

    private void refreshCoins() {
        tvCoins.setText(String.valueOf(GameData.coins));
    }
}
