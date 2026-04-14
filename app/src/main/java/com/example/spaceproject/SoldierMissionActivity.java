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

    private TextView tvEnergy, tvThreatSkill, tvThreatResilience, tvDamage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soldier_mission);

        tvEnergy = findViewById(R.id.tvEnergy);
        tvThreatSkill = findViewById(R.id.tvThreatSkill);
        tvThreatResilience = findViewById(R.id.tvThreatResilience);
        tvDamage = findViewById(R.id.tvDamage);

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            startActivity(new Intent(this, MissionControlActivity.class));
            finish();
        });

        Button btnTorpedo = findViewById(R.id.btnTorpedo);
        Button btnPowerBoost = findViewById(R.id.btnPowerBoost);
        Button btnReduceSkill = findViewById(R.id.btnReduceSkill);

        btnTorpedo.setOnClickListener(v -> {
            if (currentEnergy >= 15) {
                currentEnergy -= 15;
                int baseDmg = new Random().nextInt(20) + 20; // 20-40 damage
                int finalDmg = isBoosted ? baseDmg * 2 : baseDmg;
                
                totalDamage += finalDmg;
                threatResilience -= finalDmg;
                isBoosted = false; // Boost used up
                
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
                Toast.makeText(this, "Next Torpedo Strike will deal DOUBLE damage!", Toast.LENGTH_SHORT).show();
                updateUI();
            } else {
                Toast.makeText(this, "Not enough energy!", Toast.LENGTH_SHORT).show();
            }
        });

        btnReduceSkill.setOnClickListener(v -> {
            if (currentEnergy >= 10) {
                currentEnergy -= 10;
                threatSkill = Math.max(0, threatSkill - 15);
                // Also deal a small amount of "structural" damage
                threatResilience -= 5;
                Toast.makeText(this, "Alien weakened!", Toast.LENGTH_SHORT).show();
                updateUI();
                checkMissionStatus();
            } else {
                Toast.makeText(this, "Not enough energy!", Toast.LENGTH_SHORT).show();
            }
        });

        updateUI();
    }

    private void updateUI() {
        tvEnergy.setText("Energy: " + currentEnergy + "%");
        tvThreatSkill.setText("Threat Skill: " + threatSkill);
        tvThreatResilience.setText("Threat Resilience: " + Math.max(0, threatResilience));
        tvDamage.setText("Damage: " + totalDamage);
    }

    private void checkMissionStatus() {
        if (threatResilience <= 0) {
            Toast.makeText(this, "Mission Successful! Alien defeated!", Toast.LENGTH_LONG).show();
            GameData.addCoins(10);
            finish();
        } else if (currentEnergy <= 0) {
            Toast.makeText(this, "Mission Failed! Out of energy!", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
