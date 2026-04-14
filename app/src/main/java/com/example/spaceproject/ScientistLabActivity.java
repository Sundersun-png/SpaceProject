package com.example.spaceproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ScientistLabActivity extends AppCompatActivity {

    private TextView tvCoins, tvEnemyReduction, tvPowerBoost, tvScientistInfo;
    private CrewMember activeScientist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scientist_lab);

        // Find the first available Scientist in the crew
        for (CrewMember member : GameData.crewList) {
            if (member.isScientist()) {
                activeScientist = member;
                break;
            }
        }

        tvCoins = findViewById(R.id.tvCoins);
        tvEnemyReduction = findViewById(R.id.tvEnemyReduction);
        tvPowerBoost = findViewById(R.id.tvPowerBoost);
        tvScientistInfo = findViewById(R.id.tvScientistInfo);

        updateUI();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Flask 1: Extra Skill Potion (Costs 10 coins, gives +1 skill to scientist)
        findViewById(R.id.btnFlaskSkill).setOnClickListener(v -> {
            if (activeScientist == null) {
                Toast.makeText(this, "No scientist available!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (GameData.coins >= 10) {
                GameData.coins -= 10;
                activeScientist.train(0); // train(0) adds 1 + 0 = 1 experience
                Toast.makeText(this, "Scientist skill increased!", Toast.LENGTH_SHORT).show();
                updateUI();
            } else {
                Toast.makeText(this, "Not enough coins! (Need 10)", Toast.LENGTH_SHORT).show();
            }
        });

        // Flask 2: Gold Potion (gives 5 coin to user)
        // User asked for "one that gives 5 coin to user potion"
        // Let's make it a free brew (or costs 0 coins) but requires a scientist.
        findViewById(R.id.btnFlaskCoins).setOnClickListener(v -> {
            if (activeScientist == null) {
                Toast.makeText(this, "No scientist available to brew gold!", Toast.LENGTH_SHORT).show();
                return;
            }
            // Maybe it can only be done once per mission? Or just give 5 coins.
            GameData.addCoins(5);
            Toast.makeText(this, "Scientist transmuted some gold! (+5 Coins)", Toast.LENGTH_SHORT).show();
            updateUI();
        });

        // Flask 3: Reduce Enemy Skill Potion (Costs 10 coins)
        findViewById(R.id.btnFlaskReduce).setOnClickListener(v -> {
            if (GameData.coins >= 10) {
                GameData.coins -= 10;
                GameData.enemySkillReduction++;
                Toast.makeText(this, "Enemy skill reduced for next mission!", Toast.LENGTH_SHORT).show();
                updateUI();
            } else {
                Toast.makeText(this, "Not enough coins!", Toast.LENGTH_SHORT).show();
            }
        });

        // Flask 4: Power Boost Potion (Costs 10 coins)
        findViewById(R.id.btnFlaskBoost).setOnClickListener(v -> {
            if (GameData.coins >= 10) {
                GameData.coins -= 10;
                GameData.powerBoostLevel++;
                Toast.makeText(this, "Mission power boosted!", Toast.LENGTH_SHORT).show();
                updateUI();
            } else {
                Toast.makeText(this, "Not enough coins!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        tvCoins.setText(String.valueOf(GameData.coins));
        tvEnemyReduction.setText("Enemy Reduction: " + GameData.enemySkillReduction);
        tvPowerBoost.setText("Power Boost: " + GameData.powerBoostLevel);
        
        if (activeScientist != null) {
            tvScientistInfo.setText("🔬 Scientist: " + activeScientist.name + " (Skill: " + activeScientist.getSkill() + ")");
        } else {
            tvScientistInfo.setText("🔬 Scientist: NONE FOUND");
        }
    }
}
