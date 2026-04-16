package com.example.spaceproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ScientistTrainingActivity extends AppCompatActivity {

    private TextView tvCountdown, tvStatus, tvCoins;
    private Button btnTrain, btnInstantTrain;
    private CrewMember activeScientist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scientist_training);

        tvCountdown = findViewById(R.id.tvCountdown);
        tvStatus = findViewById(R.id.tvStatus);
        tvCoins = findViewById(R.id.tvCoins);
        btnTrain = findViewById(R.id.btnTrain);
        btnInstantTrain = findViewById(R.id.btnInstantTrain);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Find the scientist
        for (CrewMember member : GameData.crewList) {
            if (member.isScientist()) {
                activeScientist = member;
                break;
            }
        }

        updateUI();

        btnTrain.setOnClickListener(v -> startTraining());
        
        btnInstantTrain.setOnClickListener(v -> {
            if (GameData.coins >= 5) {
                GameData.coins -= 5;
                completeTraining();
            } else {
                Toast.makeText(this, "Not enough coins!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        if (tvCoins != null) tvCoins.setText(String.valueOf(GameData.coins));
        if (activeScientist == null) {
            tvStatus.setText("No scientist found in crew!");
            btnTrain.setEnabled(false);
            btnInstantTrain.setEnabled(false);
        } else {
            tvStatus.setText("Training: " + activeScientist.name + " (Skill: " + activeScientist.getSkill() + ")");
        }
    }

    private void startTraining() {
        btnTrain.setVisibility(View.GONE);
        btnInstantTrain.setVisibility(View.GONE);
        tvCountdown.setVisibility(View.VISIBLE);
        tvStatus.setText("Training in progress...");

        new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvCountdown.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                completeTraining();
            }
        }.start();
    }

    private void completeTraining() {
        if (activeScientist != null) {
            activeScientist.train(0); // Use the train method to increment session count
            Toast.makeText(ScientistTrainingActivity.this, "Training Complete! +1 Skill & +1 XP", Toast.LENGTH_SHORT).show();
        }
        startActivity(new Intent(ScientistTrainingActivity.this, ScientistLabActivity.class));
        finish();
    }
}
