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
    private TextView tvCountdown, tvCoins;
    private ConnectDotsView connectDotsView;
    private CardView cardResult;
    private TextView tvResultIcon, tvResultTitle, tvResultMessage;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engineer_mission);

        tvCountdown     = findViewById(R.id.tvCountdown);
        tvCoins         = findViewById(R.id.tvCoins);
        connectDotsView = findViewById(R.id.connectDotsView);
        cardResult      = findViewById(R.id.cardResult);
        tvResultIcon    = findViewById(R.id.tvResultIcon);
        tvResultTitle   = findViewById(R.id.tvResultTitle);
        tvResultMessage = findViewById(R.id.tvResultMessage);

        tvCoins.setText(String.valueOf(GameData.coins));
        
        // Randomly generate the puzzle each time
        connectDotsView.generateRandomPuzzle();

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (countDownTimer != null) countDownTimer.cancel();
            startActivity(new Intent(this, MissionControlActivity.class));
            finish();
        });

        connectDotsView.setOnAllConnectedListener(() -> endMission(true));

        findViewById(R.id.btnResultContinue).setOnClickListener(v -> {
            startActivity(new Intent(this, InventoryActivity.class));
            finish();
        });

        startCountdown();
    }

    private void startCountdown() {
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

    private void endMission(boolean won) {
        missionOver = true;
        if (countDownTimer != null) countDownTimer.cancel();
        connectDotsView.stopGame();

        if (won) {
            // Give +1 XP to the Engineer
            for (CrewMember m : GameData.crewList) {
                if ("Engineer".equals(m.role)) {
                    m.experience += 1;
                    break;
                }
            }

            // Successfully solved -> Inventory
            Toast.makeText(this, "Reactor Stabilized! Engineer gained +1 XP", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, InventoryActivity.class));
            finish();
            return;
        }

        tvResultIcon.setText("💥");
        tvResultTitle.setText("REACTOR EXPLODED");
        tvResultMessage.setText("Too slow! The ship is damaged.");
        cardResult.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}
