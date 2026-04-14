package com.example.spaceproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class EngineerMissionActivity extends AppCompatActivity {

    private boolean missionOver  = false;
    private boolean fromInventory = false;

    private TextView        tvCountdown, tvCoins;
    private ConnectDotsView connectDotsView;
    private CardView        cardResult;
    private TextView        tvResultIcon, tvResultTitle, tvResultMessage;

    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engineer_mission);

        fromInventory = getIntent().getBooleanExtra("fromInventory", false);

        tvCountdown     = findViewById(R.id.tvCountdown);
        tvCoins         = findViewById(R.id.tvCoins);
        connectDotsView = findViewById(R.id.connectDotsView);
        cardResult      = findViewById(R.id.cardResult);
        tvResultIcon    = findViewById(R.id.tvResultIcon);
        tvResultTitle   = findViewById(R.id.tvResultTitle);
        tvResultMessage = findViewById(R.id.tvResultMessage);

        tvCoins.setText(String.valueOf(GameData.coins));

        // Back button — cancel timer and return
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (countDownTimer != null) countDownTimer.cancel();
            if (fromInventory) {
                startActivity(new Intent(this, InventoryActivity.class));
            } else {
                startActivity(new Intent(this, MissionControlActivity.class));
            }
            finish();
        });

        // All dots connected → win
        connectDotsView.setOnAllConnectedListener(() -> endMission(true));

        // Continue from result card
        findViewById(R.id.btnResultContinue).setOnClickListener(v -> {
            startActivity(new Intent(this, InventoryActivity.class));
            finish();
        });

        startCountdown();
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvCoins.setText(String.valueOf(GameData.coins));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }

    // ── 60-second countdown ──────────────────────────────────────

    private void startCountdown() {
        countDownTimer = new CountDownTimer(60_000, 1_000) {
            @Override
            public void onTick(long msLeft) {
                int secs = (int) (msLeft / 1000);
                tvCountdown.setText(secs + "s");
                tvCountdown.setTextColor(secs <= 10 ? 0xFFFF2222 : 0xFFFF6600);
            }

            @Override
            public void onFinish() {
                if (!missionOver) endMission(false);
            }
        }.start();
    }

    // ── End mission ──────────────────────────────────────────────

    private void endMission(boolean won) {
        missionOver = true;
        if (countDownTimer != null) countDownTimer.cancel();
        connectDotsView.stopGame();

        StatisticsActivity.totalMissions++;

        if (won) {
            StatisticsActivity.missionsWon++;
            GameData.addCoins(GameData.MISSION_WIN_REWARD);

            // Go straight to inventory on win — no result card
            startActivity(new Intent(this, InventoryActivity.class));
            finish();
            return;
        }

        // Lost — show result card

        tvResultIcon.setText("💥");
        tvResultTitle.setText("REACTOR EXPLODED");
        tvResultTitle.setTextColor(0xFFFF4444);
        tvResultMessage.setText("Time ran out before the repairs were done.\nBetter luck next time.");

        cardResult.setVisibility(View.VISIBLE);
    }
}
