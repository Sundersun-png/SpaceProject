package com.example.spaceproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private CrewMember crewA;
    private CrewMember crewB;

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

        // Use crew assigned to Simulator, fall back to any available crew, then defaults
        List<CrewMember> inSim = new ArrayList<>();
        for (CrewMember m : GameData.crewList) {
            if ("Simulator".equals(m.location)) inSim.add(m);
        }
        if (inSim.size() >= 2) {
            crewA = inSim.get(0);
            crewB = inSim.get(1);
        } else if (inSim.size() == 1) {
            crewA = inSim.get(0);
            crewB = GameData.crewList.size() >= 2 ? GameData.crewList.get(1) : new CrewMember("Blake", "Pilot", 7);
        } else if (GameData.crewList.size() >= 2) {
            crewA = GameData.crewList.get(0);
            crewB = GameData.crewList.get(1);
        } else {
            crewA = new CrewMember("Alex", "Engineer", 8);
            crewB = new CrewMember("Blake", "Pilot", 7);
        }

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

        updateCrewUI();
        refreshCoins();

        // ── Normal train: 30-second timer ────────────────────────
        btnTrain.setOnClickListener(v -> {
            if (isTraining) return;
            startTraining();
        });

        // ── Instant train: costs 5 coins ─────────────────────────
        btnInstantTrain.setOnClickListener(v -> {
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
        LinearLayout navMission  = findViewById(R.id.navMission);
        LinearLayout navQuarters = findViewById(R.id.navQuarters);
        LinearLayout navHospital = findViewById(R.id.navHospital);
        LinearLayout navStats    = findViewById(R.id.navStats);

        navMission.setOnClickListener(v  -> startActivity(new Intent(this, MissionControlActivity.class)));
        navQuarters.setOnClickListener(v -> startActivity(new Intent(this, QuartersActivity.class)));
        navHospital.setOnClickListener(v -> startActivity(new Intent(this, HospitalActivity.class)));
        navStats.setOnClickListener(v    -> startActivity(new Intent(this, StatisticsActivity.class)));

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            startActivity(new Intent(this, NavigationActivity.class));
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCoins();
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
        crewA.train(0);
        crewB.train(0);
        updateCrewUI();
        refreshCoins();
        tvTrainStatus.setText("✓ Training complete!");
        btnTrain.setEnabled(true);
        btnTrain.setText("TRAIN  (30s)");
        Toast.makeText(this, "Training complete!", Toast.LENGTH_SHORT).show();
    }

    // ── UI helpers ────────────────────────────────────────────────

    private void updateCrewUI() {
        tvCrewAName.setText(crewA.name);
        tvCrewARole.setText(crewA.role);
        tvCrewAStats.setText("XP: " + crewA.experience + "    Skill: " + crewA.getSkill());
        tvCrewBName.setText(crewB.name);
        tvCrewBRole.setText(crewB.role);
        tvCrewBStats.setText("XP: " + crewB.experience + "    Skill: " + crewB.getSkill());
    }

    private void refreshCoins() {
        tvCoins.setText(String.valueOf(GameData.coins));
    }
}
