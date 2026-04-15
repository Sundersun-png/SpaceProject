package com.example.spaceproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class SoldierMissionActivity extends AppCompatActivity {

    private int currentEnergy = 100;
    private int threatSkill = 50;
    private int threatResilience = 80;
    private int totalDamage = 0;
    private boolean isBoosted = false;

    private TextView tvEnergy, tvThreatSkill, tvThreatResilience, tvDamage, tvCoins, tvCrewStats;
    private CrewMember soldier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soldier_mission);

        tvEnergy = findViewById(R.id.tvEnergy);
        tvThreatSkill = findViewById(R.id.tvThreatSkill);
        tvThreatResilience = findViewById(R.id.tvThreatResilience);
        tvDamage = findViewById(R.id.tvDamage);
        tvCoins = findViewById(R.id.tvCoins);
        tvCrewStats = findViewById(R.id.tvCrewStats);

        for (CrewMember m : GameData.crewList) {
            if ("Soldier".equals(m.role)) {
                soldier = m;
                break;
            }
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        Button btnTorpedo = findViewById(R.id.btnTorpedo);
        Button btnPowerBoost = findViewById(R.id.btnPowerBoost);
        Button btnReduceSkill = findViewById(R.id.btnReduceSkill);

        btnTorpedo.setOnClickListener(v -> {
            if (currentEnergy >= 15) {
                currentEnergy -= 15;
                int baseDmg = new Random().nextInt(20) + 20;
                int finalDmg = isBoosted ? baseDmg * 2 : baseDmg;
                totalDamage += finalDmg;
                threatResilience -= finalDmg;
                isBoosted = false;
                updateUI();
                checkMissionStatus();
            } else {
                Toast.makeText(this, "Not enough energy!", Toast.LENGTH_SHORT).show();
            }
        });

        btnPowerBoost.setOnClickListener(v -> {
            if (currentEnergy >= 10) {
                currentEnergy -= 10;
                isBoosted = true;
                Toast.makeText(this, "Boosted!", Toast.LENGTH_SHORT).show();
                updateUI();
            }
        });

        btnReduceSkill.setOnClickListener(v -> {
            if (currentEnergy >= 10) {
                currentEnergy -= 10;
                threatSkill = Math.max(0, threatSkill - 15);
                threatResilience -= 5;
                updateUI();
                checkMissionStatus();
            }
        });

        updateUI();
    }

    private void updateUI() {
        if (tvCoins != null) tvCoins.setText(String.valueOf(GameData.coins));
        tvEnergy.setText("Energy: " + currentEnergy + "%");
        tvThreatSkill.setText("Threat Skill: " + threatSkill);
        tvThreatResilience.setText("Threat Resilience: " + Math.max(0, threatResilience));
        tvDamage.setText("Damage: " + totalDamage);
        
        if (soldier != null && tvCrewStats != null) {
            tvCrewStats.setText("Skill: " + soldier.skillLevel + " | XP: " + soldier.experience);
        }
    }

    private void checkMissionStatus() {
        if (threatResilience <= 0) {
            if (soldier != null) {
                soldier.experience += 1;
                soldier.skillLevel += 1;
            }
            GameData.addCoins(10);
            Toast.makeText(this, "Mission Successful! Soldier +1 XP, +1 Skill.", Toast.LENGTH_LONG).show();
            finish();
        } else if (currentEnergy <= 0) {
            Toast.makeText(this, "Mission Failed!", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
