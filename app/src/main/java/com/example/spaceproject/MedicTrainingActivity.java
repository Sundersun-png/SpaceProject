package com.example.spaceproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MedicTrainingActivity extends AppCompatActivity {

    private TextView tvCountdown, tvStatus, tvPillLabel, tvHealLabel;
    private Button btnTrain, btnInstantTrain, btnUnlockPill, btnUnlockHeal;
    private CrewMember activeMedic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medic_training);

        tvCountdown = findViewById(R.id.tvCountdown);
        tvStatus = findViewById(R.id.tvStatus);
        btnTrain = findViewById(R.id.btnTrain);
        btnInstantTrain = findViewById(R.id.btnInstantTrain);
        btnUnlockPill = findViewById(R.id.btnUnlockPill);
        btnUnlockHeal = findViewById(R.id.btnUnlockHeal);
        tvPillLabel = findViewById(R.id.tvPillLabel);
        tvHealLabel = findViewById(R.id.tvHealLabel);

        // Find the medic
        for (CrewMember member : GameData.crewList) {
            if (member.role.equalsIgnoreCase("Medic")) {
                activeMedic = member;
                break;
            }
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        updateUI();

        btnTrain.setOnClickListener(v -> startTraining());
        btnInstantTrain.setOnClickListener(v -> instantTrain());
        btnUnlockPill.setOnClickListener(v -> unlockPill());
        btnUnlockHeal.setOnClickListener(v -> unlockHeal());
    }

    private void updateUI() {
        if (activeMedic == null) {
            tvStatus.setText("No medic found in crew!");
            btnTrain.setEnabled(false);
            btnInstantTrain.setEnabled(false);
            btnUnlockPill.setVisibility(View.GONE);
            btnUnlockHeal.setVisibility(View.GONE);
        } else {
            int skill = activeMedic.getSkill();
            tvStatus.setText("Medic skill: " + skill);
            
            // Pill unlock (Skill >= 9)
            if (skill >= 9) {
                tvPillLabel.setVisibility(View.VISIBLE);
                btnUnlockPill.setVisibility(GameData.pillUnlocked ? View.GONE : View.VISIBLE);
                if (GameData.pillUnlocked) tvPillLabel.setText("💊 Pill Power: UNLOCKED");
            } else {
                tvPillLabel.setVisibility(View.VISIBLE);
                tvPillLabel.setText("💊 Pill Power (Req Skill: 9)");
                btnUnlockPill.setVisibility(View.GONE);
            }

            // Heal unlock (Skill >= 12)
            if (skill >= 12) {
                tvHealLabel.setVisibility(View.VISIBLE);
                btnUnlockHeal.setVisibility(GameData.healPowerUnlocked ? View.GONE : View.VISIBLE);
                if (GameData.healPowerUnlocked) tvHealLabel.setText("💖 Heal Feature: UNLOCKED");
            } else {
                tvHealLabel.setVisibility(View.VISIBLE);
                tvHealLabel.setText("💖 Heal Feature (Req Skill: 12)");
                btnUnlockHeal.setVisibility(View.GONE);
            }
        }
    }

    private void startTraining() {
        btnTrain.setVisibility(View.GONE);
        btnInstantTrain.setVisibility(View.GONE);
        tvCountdown.setVisibility(View.VISIBLE);

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

    private void instantTrain() {
        if (GameData.coins >= 5) {
            GameData.coins -= 5;
            completeTraining();
        } else {
            Toast.makeText(this, "Not enough coins!", Toast.LENGTH_SHORT).show();
        }
    }

    private void completeTraining() {
        if (activeMedic != null) {
            activeMedic.train(0); // Using the train method to increment session count
            Toast.makeText(this, "Medic training complete! Skill +1", Toast.LENGTH_SHORT).show();
        }
        tvCountdown.setVisibility(View.GONE);
        btnTrain.setVisibility(View.VISIBLE);
        btnInstantTrain.setVisibility(View.VISIBLE);
        updateUI();
    }

    private void unlockPill() {
        GameData.pillUnlocked = true;
        Toast.makeText(this, "Pill power unlocked for joint missions!", Toast.LENGTH_SHORT).show();
        updateUI();
    }

    private void unlockHeal() {
        GameData.healPowerUnlocked = true;
        Toast.makeText(this, "Heal feature unlocked for Hospital!", Toast.LENGTH_SHORT).show();
        updateUI();
    }
}
