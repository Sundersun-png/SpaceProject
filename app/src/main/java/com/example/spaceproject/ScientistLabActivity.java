package com.example.spaceproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ScientistLabActivity extends AppCompatActivity {

    private TextView tvCoins, tvScientistInfo;
    private TextView tvWeaknessStatus, tvPowerBoostStatus, tvSkillBoostStatus;
    private Button btnPurchaseWeakness, btnAddWeakness;
    private Button btnPurchasePower, btnAddPower;
    private Button btnPurchaseCoins, btnAddCoins;
    private Button btnPurchaseSkill, btnAddSkill;

    private CrewMember scientist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scientist_lab);

        tvCoins = findViewById(R.id.tvCoins);
        tvScientistInfo = findViewById(R.id.tvScientistInfo);
        
        tvWeaknessStatus = findViewById(R.id.tvWeaknessStatus);
        tvPowerBoostStatus = findViewById(R.id.tvPowerBoostStatus);
        tvSkillBoostStatus = findViewById(R.id.tvSkillBoostStatus);

        btnPurchaseWeakness = findViewById(R.id.btnPurchaseWeakness);
        btnAddWeakness = findViewById(R.id.btnAddWeakness);
        btnPurchasePower = findViewById(R.id.btnPurchasePower);
        btnAddPower = findViewById(R.id.btnAddPower);
        btnPurchaseCoins = findViewById(R.id.btnPurchaseCoins);
        btnAddCoins = findViewById(R.id.btnAddCoins);
        btnPurchaseSkill = findViewById(R.id.btnPurchaseSkill);
        btnAddSkill = findViewById(R.id.btnAddSkill);

        findScientist();
        setupListeners();
        refreshUI();

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            startActivity(new Intent(this, MissionControlActivity.class));
            finish();
        });
    }

    private void findScientist() {
        for (CrewMember m : GameData.crewList) {
            if ("Scientist".equals(m.role)) {
                scientist = m;
                break;
            }
        }
    }

    private void setupListeners() {
        btnPurchaseWeakness.setOnClickListener(v -> purchasePotion(() -> GameData.weaknessPotionsPurchased++));
        btnAddWeakness.setOnClickListener(v -> addPotion(8, () -> GameData.weaknessPotionAdded = true));

        btnPurchasePower.setOnClickListener(v -> purchasePotion(() -> GameData.powerBoostPurchased = true));
        btnAddPower.setOnClickListener(v -> addPotion(5, () -> GameData.powerBoostAdded = true));

        btnPurchaseCoins.setOnClickListener(v -> purchasePotion(() -> GameData.addCoins(5)));
        btnAddCoins.setOnClickListener(v -> addPotion(10, () -> GameData.addCoins(5)));

        btnPurchaseSkill.setOnClickListener(v -> purchasePotion(this::applySkillBoost));
        btnAddSkill.setOnClickListener(v -> addPotion(9, this::applySkillBoost));
    }

    private void purchasePotion(Runnable action) {
        if (GameData.coins >= 5) {
            GameData.coins -= 5;
            action.run();
            refreshUI();
            Toast.makeText(this, "Purchased!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Not enough coins!", Toast.LENGTH_SHORT).show();
        }
    }

    private void addPotion(int reqSkill, Runnable action) {
        if (scientist == null) {
            Toast.makeText(this, "No Scientist in crew!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (scientist.getSkill() >= reqSkill) {
            action.run();
            refreshUI();
            Toast.makeText(this, "Added by Scientist!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Skill too low! (Requires " + reqSkill + ")", Toast.LENGTH_SHORT).show();
        }
    }

    private void applySkillBoost() {
        if (GameData.allCrewSkillBoosted) return;
        for (CrewMember m : GameData.crewList) {
            if (!"Scientist".equals(m.role)) {
                m.experience += 20; // +2 Skill approximately
            }
        }
        GameData.allCrewSkillBoosted = true;
    }

    private void refreshUI() {
        tvCoins.setText(String.valueOf(GameData.coins));
        if (scientist != null) {
            tvScientistInfo.setText("Scientist: " + scientist.name + " | Skill: " + scientist.getSkill());
        } else {
            tvScientistInfo.setText("No Scientist assigned");
        }

        tvWeaknessStatus.setText("Owned: " + GameData.weaknessPotionsPurchased + (GameData.weaknessPotionAdded ? " (Scientist Applied)" : ""));
        tvPowerBoostStatus.setText(GameData.powerBoostAdded ? "Added (Free)" : (GameData.powerBoostPurchased ? "Purchased" : "Not Added"));
        tvSkillBoostStatus.setText(GameData.allCrewSkillBoosted ? "Applied (+2 Skill)" : "Not Applied");
    }
}
