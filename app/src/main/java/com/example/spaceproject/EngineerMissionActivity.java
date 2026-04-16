package com.example.spaceproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class EngineerMissionActivity extends AppCompatActivity {

    private boolean missionOver  = false;
    private int puzzleCount = 0;
    private final int MAX_PUZZLES = 5;
    private int puzzlesSolved = 0;

    private TextView tvCountdown, tvCoins, tvProgress, tvCrewStats;
    private ConnectDotsView connectDotsView;
    private CardView cardResult;
    private TextView tvResultIcon, tvResultTitle, tvResultMessage;
    private CountDownTimer countDownTimer;
    private CrewMember engineer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engineer_mission);

        tvCountdown     = findViewById(R.id.tvCountdown);
        tvCoins         = findViewById(R.id.tvCoins);
        tvProgress      = findViewById(R.id.tvProgress);
        tvCrewStats     = findViewById(R.id.tvCrewStats);
        connectDotsView = findViewById(R.id.connectDotsView);
        cardResult      = findViewById(R.id.cardResult);
        tvResultIcon    = findViewById(R.id.tvResultIcon);
        tvResultTitle   = findViewById(R.id.tvResultTitle);
        tvResultMessage = findViewById(R.id.tvResultMessage);

        if (tvCoins != null) tvCoins.setText(String.valueOf(GameData.coins));
        
        for (CrewMember m : GameData.crewList) {
            if ("Engineer".equals(m.role)) {
                engineer = m;
                break;
            }
        }
        
        if (engineer != null) {
            // Check if engineer is in hospital
            if ("Hospital".equalsIgnoreCase(engineer.location)) {
                Toast.makeText(this, "Engineer is in Hospital!", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            engineer.setMissionsParticipated(engineer.getMissionsParticipated() + 1);
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (countDownTimer != null) countDownTimer.cancel();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        connectDotsView.setOnAllConnectedListener(() -> {
            puzzlesSolved++;
            nextPuzzle();
        });

        findViewById(R.id.btnResultContinue).setOnClickListener(v -> {
            startActivity(new Intent(this, InventoryActivity.class));
            finish();
        });

        startMission();
        updateCrewStats();
    }

    private void updateCrewStats() {
        if (engineer != null && tvCrewStats != null) {
            tvCrewStats.setText("Skill: " + engineer.skillLevel + " | XP: " + engineer.experience);
        }
    }

    private void startMission() {
        puzzleCount = 0;
        puzzlesSolved = 0;
        nextPuzzle();
        startCountdown();
    }

    private void updateProgress() {
        if (tvProgress != null) {
            tvProgress.setText("Puzzle: " + puzzleCount + "/" + MAX_PUZZLES);
        }
    }

    private void nextPuzzle() {
        if (puzzleCount < MAX_PUZZLES) {
            puzzleCount++;
            updateProgress();
            connectDotsView.generateRandomPuzzle();
        } else {
            endMission(true);
        }
    }

    private void startCountdown() {
        if (countDownTimer != null) countDownTimer.cancel();
        countDownTimer = new CountDownTimer(60_000, 1_000) {
            @Override
            public void onTick(long msLeft) {
                int secs = (int) (msLeft / 1000);
                tvCountdown.setText(secs + "s");
            }
            @Override
            public void onFinish() {
                if (!missionOver) endMission(false);
            }
        }.start();
    }

    private void endMission(boolean completed) {
        missionOver = true;
        if (countDownTimer != null) countDownTimer.cancel();
        connectDotsView.stopGame();

        if (puzzlesSolved >= 3) {
            if (engineer != null) {
                engineer.experience += 1;
                engineer.skillLevel += 1;
                // Profession-specific mission wins count as training sessions
                engineer.setTrainingSessions(engineer.getTrainingSessions() + 1);
                Toast.makeText(this, "Mission Successful! +1 XP, +1 Skill.", Toast.LENGTH_LONG).show();
            }
            GameData.addCoins(10);
            tvResultIcon.setText("⚙️");
            tvResultTitle.setText("REACTOR STABILIZED");
            tvResultMessage.setText("You solved " + puzzlesSolved + " puzzles! Accessing inventory...");
            cardResult.setVisibility(View.VISIBLE);
        } else {
            tvResultIcon.setText("💥");
            tvResultTitle.setText("REACTOR FAILURE");
            tvResultMessage.setText("Only solved " + puzzlesSolved + ". Ship takes damage!");
            cardResult.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}
