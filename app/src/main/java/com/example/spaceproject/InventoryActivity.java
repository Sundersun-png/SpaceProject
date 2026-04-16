package com.example.spaceproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
        btnPurchaseTorpedo.setOnClickListener(v -> purchaseItem(() -> GameData.torpedoAdded = true));
        btnAddTorpedo.setOnClickListener(v -> addItem(5, () -> GameData.torpedoAdded = true));

        btnPurchaseGrenade.setOnClickListener(v -> purchaseItem(() -> GameData.grenadeAdded = true));
        btnAddGrenade.setOnClickListener(v -> addItem(3, () -> GameData.grenadeAdded = true));

        btnPurchaseGun.setOnClickListener(v -> purchaseItem(() -> GameData.gunAdded = true));
        btnAddGun.setOnClickListener(v -> addItem(4, () -> GameData.gunAdded = true));

        btnPurchaseRocketship.setOnClickListener(v -> purchaseItem(() -> GameData.rocketshipAdded = true));
        btnAddRocketship.setOnClickListener(v -> addItem(6, () -> GameData.rocketshipAdded = true));
    }

    private void purchaseItem(Runnable action) {
        if (GameData.coins >= 5) {
            GameData.coins -= 5;
            action.run();
            refreshUI();
            Toast.makeText(this, "Weapon Purchased with Coins!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Not enough coins! (Need 5🪙)", Toast.LENGTH_SHORT).show();
        }
    }

    private void addItem(int reqXP, Runnable action) {
        if (engineer == null) {
            Toast.makeText(this, "No Engineer in crew! Recruit one first.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (engineer.experience >= reqXP) {
            action.run();
            refreshUI();
            Toast.makeText(this, "Weapon Unlocked via XP!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Engineer XP too low! (Requires " + reqXP + ")", Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshUI() {
        tvCoins.setText(String.valueOf(GameData.coins));
        if (engineer != null) {
            tvEngineerInfo.setText("Engineer: " + engineer.name + " | XP: " + engineer.experience);
        } else {
            tvEngineerInfo.setText("No Engineer in crew!");
        }

        tvTorpedoStatus.setText(GameData.torpedoAdded ? "Added" : "Not Owned");
        tvGrenadeStatus.setText(GameData.grenadeAdded ? "Added" : "Not Owned");
        tvGunStatus.setText(GameData.gunAdded ? "Added" : "Not Owned");
        tvRocketshipStatus.setText(GameData.rocketshipAdded ? "Added" : "Not Owned");

        int green = 0xFF90EE90;
        int red = 0xFFFF6666;
        
        tvTorpedoStatus.setTextColor(GameData.torpedoAdded ? green : red);
        tvGrenadeStatus.setTextColor(GameData.grenadeAdded ? green : red);
        tvGunStatus.setTextColor(GameData.gunAdded ? green : red);
        tvRocketshipStatus.setTextColor(GameData.rocketshipAdded ? green : red);

        btnPurchaseTorpedo.setVisibility(GameData.torpedoAdded ? View.GONE : View.VISIBLE);
        btnAddTorpedo.setVisibility(GameData.torpedoAdded ? View.GONE : View.VISIBLE);
        
        btnPurchaseGrenade.setVisibility(GameData.grenadeAdded ? View.GONE : View.VISIBLE);
        btnAddGrenade.setVisibility(GameData.grenadeAdded ? View.GONE : View.VISIBLE);
        
        btnPurchaseGun.setVisibility(GameData.gunAdded ? View.GONE : View.VISIBLE);
        btnAddGun.setVisibility(GameData.gunAdded ? View.GONE : View.VISIBLE);
        
        btnPurchaseRocketship.setVisibility(GameData.rocketshipAdded ? View.GONE : View.VISIBLE);
        btnAddRocketship.setVisibility(GameData.rocketshipAdded ? View.GONE : View.VISIBLE);
    }
}
