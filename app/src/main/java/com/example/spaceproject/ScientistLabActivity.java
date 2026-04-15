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

        for (CrewMember member : GameData.crewList) {
            if (member.role.equalsIgnoreCase("Scientist")) {
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

        // Flask 1: Skill Potion (Req XP: 4)
        findViewById(R.id.btnFlaskSkill).setOnClickListener(v -> brewPotion(4, "Skill Potion", () -> {
            activeScientist.experience += 1;
            activeScientist.skillLevel += 1;
        }));
        findViewById(R.id.btnBuySkill).setOnClickListener(v -> buyPotion(10, "Skill Potion", () -> {
            activeScientist.experience += 1;
            activeScientist.skillLevel += 1;
        }));

        // Flask 2: Gold Potion (Req XP: 6)
        findViewById(R.id.btnFlaskCoins).setOnClickListener(v -> brewPotion(6, "Gold Potion", () -> {
            GameData.addCoins(5);
        }));
        findViewById(R.id.btnBuyCoins).setOnClickListener(v -> buyPotion(15, "Gold Potion", () -> {
            GameData.addCoins(5);
        }));

        // Flask 3: Weakness (Req XP: 3)
        findViewById(R.id.btnFlaskReduce).setOnClickListener(v -> brewPotion(3, "Weakness Potion", () -> {
            GameData.weaknessPotionAdded = true;
        }));
        findViewById(R.id.btnBuyReduce).setOnClickListener(v -> buyPotion(8, "Weakness Potion", () -> {
            GameData.weaknessPotionAdded = true;
        }));

        // Flask 4: Power Boost (Req XP: 5)
        findViewById(R.id.btnFlaskBoost).setOnClickListener(v -> brewPotion(5, "Power Boost", () -> {
            GameData.powerBoostAdded = true;
        }));
        findViewById(R.id.btnBuyBoost).setOnClickListener(v -> buyPotion(12, "Power Boost", () -> {
            GameData.powerBoostAdded = true;
        }));
    }

    private interface PotionAction {
        void execute();
    }

    private void brewPotion(int reqXP, String name, PotionAction action) {
        if (activeScientist == null) {
            Toast.makeText(this, "No scientist available!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (activeScientist.experience >= reqXP) {
            action.execute();
            Toast.makeText(this, name + " brewed successfully!", Toast.LENGTH_SHORT).show();
            updateUI();
        } else {
            Toast.makeText(this, "Scientist XP too low! (Need " + reqXP + ")", Toast.LENGTH_SHORT).show();
        }
    }

    private void buyPotion(int cost, String name, PotionAction action) {
        if (GameData.coins >= cost) {
            GameData.coins -= cost;
            action.execute();
            Toast.makeText(this, name + " purchased for " + cost + " coins!", Toast.LENGTH_SHORT).show();
            updateUI();
        } else {
            Toast.makeText(this, "Not enough coins!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI() {
        if (tvCoins != null) tvCoins.setText(String.valueOf(GameData.coins));
        tvEnemyReduction.setText("Weak Enemy: " + (GameData.weaknessPotionAdded ? "READY" : "OFF"));
        tvPowerBoost.setText("Power Boost: " + (GameData.powerBoostAdded ? "READY" : "OFF"));
        
        if (activeScientist != null) {
            tvScientistInfo.setText("🔬 Scientist: " + activeScientist.name + " (XP: " + activeScientist.experience + ")");
        } else {
            tvScientistInfo.setText("🔬 Scientist: NONE FOUND");
        }
    }
}
