package com.example.spaceproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MedicLabActivity extends AppCompatActivity {

    private TextView tvCountdown, tvStatus, tvPillLabel, tvHealLabel, tvCoins;
    private Button btnTrain, btnInstantTrain, btnBuyPill, btnUnlockPill, btnBuyHeal, btnUnlockHeal;
    private View layoutPillButtons, layoutHealButtons;
    private CrewMember activeMedic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medic_lab);

        tvCountdown = findViewById(R.id.tvCountdown);
        tvStatus = findViewById(R.id.tvStatus);
        btnTrain = findViewById(R.id.btnTrain);
        btnInstantTrain = findViewById(R.id.btnInstantTrain);
        
        btnBuyPill = findViewById(R.id.btnBuyPill);
        btnUnlockPill = findViewById(R.id.btnUnlockPill);
        btnBuyHeal = findViewById(R.id.btnBuyHeal);
        btnUnlockHeal = findViewById(R.id.btnUnlockHeal);
        
        layoutPillButtons = findViewById(R.id.layoutPillButtons);
        layoutHealButtons = findViewById(R.id.layoutHealButtons);

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
        
        // PILL POWER
        btnBuyPill.setOnClickListener(v -> buyFeature(5, "Pill Power", () -> GameData.pillUnlocked = true));
        btnUnlockPill.setOnClickListener(v -> unlockFeature(9, "Pill Power", () -> GameData.pillUnlocked = true));
        
        // HEAL FEATURE
        btnBuyHeal.setOnClickListener(v -> buyFeature(5, "Heal Feature", () -> GameData.healPowerUnlocked = true));
        btnUnlockHeal.setOnClickListener(v -> unlockFeature(12, "Heal Feature", () -> GameData.healPowerUnlocked = true));
    }

    private void buyFeature(int cost, String name, Runnable action) {
        if (GameData.coins >= cost) {
            GameData.coins -= cost;
            action.run();
            Toast.makeText(this, name + " bought with coins!", Toast.LENGTH_SHORT).show();
            updateUI();
        } else {
            Toast.makeText(this, "Not enough coins! (Need " + cost + ")", Toast.LENGTH_SHORT).show();
        }
    }

    private void unlockFeature(int reqXP, String name, Runnable action) {
        if (activeMedic == null) {
            Toast.makeText(this, "No Medic in crew!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (activeMedic.experience >= reqXP) {
            action.run();
            Toast.makeText(this, name + " unlocked with XP!", Toast.LENGTH_SHORT).show();
            updateUI();
        } else {
            Toast.makeText(this, name + " requires XP: " + reqXP, Toast.LENGTH_SHORT).show();
        }
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
        } else {
            tvStatus.setText("Medic: " + activeMedic.name + " | XP: " + activeMedic.experience);
        }

        // Pill logic
        if (GameData.pillUnlocked) {
            tvPillLabel.setText("💊 Pill Power: UNLOCKED");
            layoutPillButtons.setVisibility(View.GONE);
        } else {
            tvPillLabel.setText("💊 Pill Power");
            layoutPillButtons.setVisibility(View.VISIBLE);
        }

        // Heal logic
        if (GameData.healPowerUnlocked) {
            tvHealLabel.setText("💖 Heal Feature: UNLOCKED");
            layoutHealButtons.setVisibility(View.GONE);
        } else {
            tvHealLabel.setText("💖 Heal Feature");
            layoutHealButtons.setVisibility(View.VISIBLE);
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
