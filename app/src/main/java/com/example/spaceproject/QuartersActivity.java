package com.example.spaceproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class QuartersActivity extends AppCompatActivity {

    private boolean andreSelected = false;
    private int currentEnergy = 10; // starts below max to show restore works
    private final int maxEnergy = 25;

    private CardView cardAndre;
    private TextView tvAndreEnergy, tvAndreSelectedBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quarters);

        cardAndre           = findViewById(R.id.cardAndre);
        tvAndreEnergy       = findViewById(R.id.tvAndreEnergy);
        tvAndreSelectedBadge = findViewById(R.id.tvAndreSelectedBadge);

        Button btnRestoreEnergy       = findViewById(R.id.btnRestoreEnergy);
        Button btnMoveToSimulator     = findViewById(R.id.btnMoveToSimulator);
        Button btnMoveToMissionControl = findViewById(R.id.btnMoveToMissionControl);

        // Tap card to select/deselect Andre
        cardAndre.setOnClickListener(v -> {
            andreSelected = !andreSelected;
            refreshCrewCard();
        });

        // Restore energy — only works if a crew member is selected
        btnRestoreEnergy.setOnClickListener(v -> {
            if (!andreSelected) {
                Toast.makeText(this, "Select a crew member first!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (currentEnergy >= maxEnergy) {
                Toast.makeText(this, "Andre is already at full energy!", Toast.LENGTH_SHORT).show();
                return;
            }
            currentEnergy = maxEnergy;
            tvAndreEnergy.setText("Max Energy: " + currentEnergy);
            Toast.makeText(this, "Andre's energy restored to " + maxEnergy + "!", Toast.LENGTH_SHORT).show();
        });

        // Move to Simulator
        btnMoveToSimulator.setOnClickListener(v -> {
            if (!andreSelected) {
                Toast.makeText(this, "Select a crew member first!", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Andre moved to Simulator!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        // Move to Mission Control
        btnMoveToMissionControl.setOnClickListener(v -> {
            if (!andreSelected) {
                Toast.makeText(this, "Select a crew member first!", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Andre moved to Mission Control!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MissionControlActivity.class));
            finish();
        });

        // Bottom nav
        LinearLayout navSimulator = findViewById(R.id.navSimulator);
        LinearLayout navMission   = findViewById(R.id.navMission);

        navSimulator.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        navMission.setOnClickListener(v -> {
            startActivity(new Intent(this, MissionControlActivity.class));
            finish();
        });

        // navQuarters is current screen — no action needed
    }

    private void refreshCrewCard() {
        if (andreSelected) {
            cardAndre.setCardBackgroundColor(0xFF2E4A2E); // dark green tint
            tvAndreSelectedBadge.setText("✓ SELECTED");
            tvAndreSelectedBadge.setTextColor(0xFF90EE90);
        } else {
            cardAndre.setCardBackgroundColor(0xCC1A1A2E); // original dark blue
            tvAndreSelectedBadge.setText("TAP TO SELECT");
            tvAndreSelectedBadge.setTextColor(0xFF888888);
        }
    }
}
