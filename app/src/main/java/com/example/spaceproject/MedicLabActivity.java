package com.example.spaceproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MedicLabActivity extends AppCompatActivity {

    private TextView tvCountdown, tvStatus, tvPillLabel, tvHealLabel, tvCoins;
    private Button btnTrain, btnInstantTrain, btnUnlockPill, btnUnlockHeal;
    private CrewMember activeMedic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medic_lab);

        tvCountdown = findViewById(R.id.tvCountdown);
        tvStatus = findViewById(R.id.tvStatus);
        btnTrain = findViewById(R.id.btnTrain);
        btnInstantTrain = findViewById(R.id.btnInstantTrain);
        btnUnlockPill = findViewById(R.id.btnUnlockPill);
        btnUnlockHeal = findViewById(R.id.btnUnlockHeal);
        tvPillLabel = findViewById(R.id.tvPillLabel);
        tvHealLabel = findViewById(R.id.tvHealLabel);
        tvCoins = findViewById(R.id.tvCoins);

        findMedic();

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        btnTrain.setOnClickListener(v -> startTraining());
        btnInstantTrain.setOnClickListener(v -> instantTrain());
        
        // UNLOCK PILL (Req XP: 9, Cost: 10 coins)
        btnUnlockPill.setOnClickListener(v -> unlockFeature(9, 10, "Pill Power", () -> GameData.pillUnlocked = true));
        
        // UNLOCK HEAL (Req XP: 12, Cost: 15 coins)
        btnUnlockHeal.setOnClickListener(v -> unlockFeature(12, 15, "Heal Feature", () -> GameData.healPowerUnlocked = true));
    }

    private void unlockFeature(int reqXP, int cost, String name, Runnable action) {
        if (activeMedic == null) return;
        if (activeMedic.experience < reqXP) {
            Toast.makeText(this, name + " requires XP: " + reqXP, Toast.LENGTH_SHORT).show();
            return;
        }
        if (GameData.coins < cost) {
            Toast.makeText(this, "Not enough coins! (Need " + cost + ")", Toast.LENGTH_SHORT).show();
            return;
        }
        GameData.coins -= cost;
        action.run();
        Toast.makeText(this, name + " unlocked!", Toast.LENGTH_SHORT).show();
        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        findMedic();
        updateUI();
    }

    private void findMedic() {
        activeMedic = null;
        for (CrewMember member : GameData.crewList) {
            if (member.role.equalsIgnoreCase("Medic")) {
                activeMedic = member;
                break;
            }
        }
    }

    private void updateUI() {
        if (tvCoins != null) tvCoins.setText(String.valueOf(GameData.coins));
        
        if (activeMedic == null) {
            tvStatus.setText("No medic found in crew!");
            btnTrain.setEnabled(false);
            btnInstantTrain.setEnabled(false);
            btnUnlockPill.setVisibility(View.GONE);
            btnUnlockHeal.setVisibility(View.GONE);
        } else {
            tvStatus.setText("Medic: " + activeMedic.name + " | XP: " + activeMedic.experience);
            
            // Pill logic (Req XP: 9)
            if (GameData.pillUnlocked) {
                tvPillLabel.setText("💊 Pill Power: UNLOCKED");
                btnUnlockPill.setVisibility(View.GONE);
            } else {
                tvPillLabel.setText("💊 Pill Power (Req XP: 9, Cost: 10)");
                btnUnlockPill.setVisibility(activeMedic.experience >= 9 ? View.VISIBLE : View.GONE);
            }

            // Heal logic (Req XP: 12)
            if (GameData.healPowerUnlocked) {
                tvHealLabel.setText("💖 Heal Feature: UNLOCKED");
                btnUnlockHeal.setVisibility(View.GONE);
            } else {
                tvHealLabel.setText("💖 Heal Feature (Req XP: 12, Cost: 15)");
                btnUnlockHeal.setVisibility(activeMedic.experience >= 12 ? View.VISIBLE : View.GONE);
            }
        }
    }

    private void startTraining() {
        btnTrain.setVisibility(View.GONE);
        btnInstantTrain.setVisibility(View.GONE);
        tvCountdown.setVisibility(View.VISIBLE);
        new CountDownTimer(30000, 1000) {
            @Override public void onTick(long l) { tvCountdown.setText(String.valueOf(l / 1000)); }
            @Override public void onFinish() { completeTraining(); }
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
            activeMedic.experience += 1;
            activeMedic.skillLevel += 1;
        }
        tvCountdown.setVisibility(View.GONE);
        btnTrain.setVisibility(View.VISIBLE);
        btnInstantTrain.setVisibility(View.VISIBLE);
        updateUI();
    }
}
