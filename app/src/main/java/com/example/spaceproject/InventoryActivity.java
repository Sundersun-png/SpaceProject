package com.example.spaceproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class InventoryActivity extends AppCompatActivity {

    private TextView tvCoins, tvEngineerInfo;
    private TextView tvTorpedoStatus, tvGrenadeStatus, tvGunStatus, tvRocketshipStatus;
    private Button btnPurchaseTorpedo, btnAddTorpedo;
    private Button btnPurchaseGrenade, btnAddGrenade;
    private Button btnPurchaseGun, btnAddGun;
    private Button btnPurchaseRocketship, btnAddRocketship;

    private CrewMember engineer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        tvCoins = findViewById(R.id.tvCoins);
        tvEngineerInfo = findViewById(R.id.tvEngineerInfo);
        
        tvTorpedoStatus = findViewById(R.id.tvTorpedoStatus);
        tvGrenadeStatus = findViewById(R.id.tvGrenadeStatus);
        tvGunStatus = findViewById(R.id.tvGunStatus);
        tvRocketshipStatus = findViewById(R.id.tvRocketshipStatus);

        btnPurchaseTorpedo = findViewById(R.id.btnPurchaseTorpedo);
        btnAddTorpedo = findViewById(R.id.btnAddTorpedo);
        btnPurchaseGrenade = findViewById(R.id.btnPurchaseGrenade);
        btnAddGrenade = findViewById(R.id.btnAddGrenade);
        btnPurchaseGun = findViewById(R.id.btnPurchaseGun);
        btnAddGun = findViewById(R.id.btnAddGun);
        btnPurchaseRocketship = findViewById(R.id.btnPurchaseRocketship);
        btnAddRocketship = findViewById(R.id.btnAddRocketship);

        findEngineer();
        setupListeners();
        refreshUI();

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            startActivity(new Intent(this, MissionControlActivity.class));
            finish();
        });
    }

    private void findEngineer() {
        for (CrewMember m : GameData.crewList) {
            if ("Engineer".equals(m.role)) {
                engineer = m;
                break;
            }
        }
    }

    private void setupListeners() {
        btnPurchaseTorpedo.setOnClickListener(v -> purchaseItem(() -> GameData.torpedoPurchased = true));
        btnAddTorpedo.setOnClickListener(v -> addItem(GameData.torpedoPurchased, 5, () -> GameData.torpedoAdded = true));

        btnPurchaseGrenade.setOnClickListener(v -> purchaseItem(() -> GameData.grenadePurchased = true));
        btnAddGrenade.setOnClickListener(v -> addItem(GameData.grenadePurchased, 3, () -> GameData.grenadeAdded = true));

        btnPurchaseGun.setOnClickListener(v -> purchaseItem(() -> GameData.gunPurchased = true));
        btnAddGun.setOnClickListener(v -> addItem(GameData.gunPurchased, 4, () -> GameData.gunAdded = true));

        btnPurchaseRocketship.setOnClickListener(v -> purchaseItem(() -> GameData.rocketshipPurchased = true));
        btnAddRocketship.setOnClickListener(v -> addItem(GameData.rocketshipPurchased, 6, () -> GameData.rocketshipAdded = true));
    }

    private void purchaseItem(Runnable action) {
        if (GameData.coins >= 5) {
            GameData.coins -= 5;
            action.run();
            refreshUI();
            Toast.makeText(this, "Weapon Purchased!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Not enough coins! (Need 5🪙)", Toast.LENGTH_SHORT).show();
        }
    }

    private void addItem(boolean purchased, int reqSkill, Runnable action) {
        if (!purchased) {
            Toast.makeText(this, "Purchase the weapon first!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (engineer == null) {
            Toast.makeText(this, "No Engineer in crew! Recruit one first.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (engineer.getSkill() >= reqSkill) {
            action.run();
            refreshUI();
            Toast.makeText(this, "Weapon Added by Engineer!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Engineer skill too low! (Requires " + reqSkill + ")", Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshUI() {
        tvCoins.setText(String.valueOf(GameData.coins));
        if (engineer != null) {
            tvEngineerInfo.setText("Engineer: " + engineer.name + " | Skill: " + engineer.getSkill());
        } else {
            tvEngineerInfo.setText("No Engineer in crew!");
        }

        tvTorpedoStatus.setText(GameData.torpedoAdded ? "Added" : (GameData.torpedoPurchased ? "Purchased" : "Not Owned"));
        tvGrenadeStatus.setText(GameData.grenadeAdded ? "Added" : (GameData.grenadePurchased ? "Purchased" : "Not Owned"));
        tvGunStatus.setText(GameData.gunAdded ? "Added" : (GameData.gunPurchased ? "Purchased" : "Not Owned"));
        tvRocketshipStatus.setText(GameData.rocketshipAdded ? "Added" : (GameData.rocketshipPurchased ? "Purchased" : "Not Owned"));

        int green = 0xFF90EE90;
        int yellow = 0xFFFFD700;
        int red = 0xFFFF6666;
        
        tvTorpedoStatus.setTextColor(GameData.torpedoAdded ? green : (GameData.torpedoPurchased ? yellow : red));
        tvGrenadeStatus.setTextColor(GameData.grenadeAdded ? green : (GameData.grenadePurchased ? yellow : red));
        tvGunStatus.setTextColor(GameData.gunAdded ? green : (GameData.gunPurchased ? yellow : red));
        tvRocketshipStatus.setTextColor(GameData.rocketshipAdded ? green : (GameData.rocketshipPurchased ? yellow : red));

        // Always keep buttons enabled unless already added/purchased
        // This ensures the user can see the feedback (Toast) for why it's not working
        btnPurchaseTorpedo.setEnabled(!GameData.torpedoPurchased);
        btnPurchaseGrenade.setEnabled(!GameData.grenadePurchased);
        btnPurchaseGun.setEnabled(!GameData.gunPurchased);
        btnPurchaseRocketship.setEnabled(!GameData.rocketshipPurchased);

        btnAddTorpedo.setEnabled(!GameData.torpedoAdded);
        btnAddGrenade.setEnabled(!GameData.grenadeAdded);
        btnAddGun.setEnabled(!GameData.gunAdded);
        btnAddRocketship.setEnabled(!GameData.rocketshipAdded);
    }
}
