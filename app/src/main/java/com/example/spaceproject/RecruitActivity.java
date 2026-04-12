package com.example.spaceproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RecruitActivity extends AppCompatActivity {

    // ── Fields ──────────────────────────────────────────────────────────
    // These variables are declared here so every method in this
    // class can access them without passing them around.

    // Tracks which specialization the user currently has selected.
    // Starts as "Pilot" because that card is highlighted by default.
    private String selectedSpecialization = "Pilot";

    // UI references — filled in onCreate using findViewById
    private TextView tvSelectedClass;
    private TextView tvSkillValue;
    private TextView tvResilienceValue;
    private TextView tvEnergyValue;
    private TextView tvAbilityValue;

    private LinearLayout cardPilot;
    private LinearLayout cardMedic;
    private LinearLayout cardScientist;
    private LinearLayout cardEngineer;
    private LinearLayout cardSoldier;

    // ── onCreate ─────────────────────────────────────────────────────────
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Links this Activity to activity_recruit.xml
        setContentView(R.layout.activity_recruit);

        // ── Connect XML views to Java variables ─────────────────────────
        tvSelectedClass    = findViewById(R.id.tvSelectedClass);
        tvSkillValue       = findViewById(R.id.tvSkillValue);
        tvResilienceValue  = findViewById(R.id.tvResilienceValue);
        tvEnergyValue      = findViewById(R.id.tvEnergyValue);
        tvAbilityValue     = findViewById(R.id.tvAbilityValue);

        cardPilot      = findViewById(R.id.cardPilot);
        cardMedic      = findViewById(R.id.cardMedic);
        cardScientist  = findViewById(R.id.cardScientist);
        cardEngineer   = findViewById(R.id.cardEngineer);
        cardSoldier    = findViewById(R.id.cardSoldier);

        // ── Back button ─────────────────────────────────────────────────
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        // finish() closes this Activity and goes back to NavigationActivity

        // ── Specialization card click listeners ─────────────────────────
        // Each card calls selectSpecialization() with its class name.
        // That method updates the stats panel and highlights the card.

        cardPilot.setOnClickListener(v     -> selectSpecialization("Pilot"));
        cardMedic.setOnClickListener(v     -> selectSpecialization("Medic"));
        cardScientist.setOnClickListener(v -> selectSpecialization("Scientist"));
        cardEngineer.setOnClickListener(v  -> selectSpecialization("Engineer"));
        cardSoldier.setOnClickListener(v   -> selectSpecialization("Soldier"));

        // Show Pilot stats by default on first load
        selectSpecialization("Pilot");

        // ── Recruit button ───────────────────────────────────────────────
        Button btnConfirmRecruit = findViewById(R.id.btnConfirmRecruit);
        btnConfirmRecruit.setOnClickListener(v -> recruitCrewMember());
    }

    // ── selectSpecialization() ───────────────────────────────────────────
    // Called whenever a class card is tapped.
    // 1. Saves the selected class name
    // 2. Updates the stats panel text
    // 3. Highlights the selected card, dims all others
    private void selectSpecialization(String specialization) {
        selectedSpecialization = specialization;

        // Update the banner showing selected class name
        tvSelectedClass.setText("— " + specialization.toUpperCase() + " —");

        // ── Stat values per specialization ───────────────────────────────
        // Each specialization has unique Skill and Resilience values
        // based on their role. Energy is always 100. Ability is unique.
        // These numbers match the game design from your project plan PDF.
        switch (specialization) {

            case "Pilot":
                tvSkillValue.setText("7");
                tvResilienceValue.setText("6");
                tvEnergyValue.setText("100");
                tvAbilityValue.setText("Evade");
                break;

            case "Medic":
                tvSkillValue.setText("4");
                tvResilienceValue.setText("8");
                tvEnergyValue.setText("100");
                tvAbilityValue.setText("Heal Teammate");
                break;

            case "Scientist":
                tvSkillValue.setText("6");
                tvResilienceValue.setText("5");
                tvEnergyValue.setText("100");
                tvAbilityValue.setText("Boost Attack");
                break;

            case "Engineer":
                tvSkillValue.setText("5");
                tvResilienceValue.setText("9");
                tvEnergyValue.setText("100");
                tvAbilityValue.setText("Repair");
                break;

            case "Soldier":
                tvSkillValue.setText("9");
                tvResilienceValue.setText("7");
                tvEnergyValue.setText("100");
                tvAbilityValue.setText("Heavy Attack");
                break;
        }

        // ── Highlight selected card, dim all others ───────────────────────
        // Reset ALL cards to dark/unselected first
        resetAllCards();

        // Then highlight only the chosen one in bright purple
        switch (specialization) {
            case "Pilot":
                cardPilot.setBackgroundColor(0xBBCC2D82);
                break;
            case "Medic":
                cardMedic.setBackgroundColor(0xBBCC2D82);
                break;
            case "Scientist":
                cardScientist.setBackgroundColor(0xBBCC2D82);
                break;
            case "Engineer":
                cardEngineer.setBackgroundColor(0xBBCC2D82);
                break;
            case "Soldier":
                cardSoldier.setBackgroundColor(0xBBCC2D82);
                break;
        }
    }

    // ── resetAllCards() ──────────────────────────────────────────────────
    // Sets every class card back to the dark/unselected colour.
    // Called before highlighting the newly selected card.
    private void resetAllCards() {
        int darkColor = 0x331A3A5C;
        cardPilot.setBackgroundColor(darkColor);
        cardMedic.setBackgroundColor(darkColor);
        cardScientist.setBackgroundColor(darkColor);
        cardEngineer.setBackgroundColor(darkColor);
        cardSoldier.setBackgroundColor(darkColor);
    }

    // ── recruitCrewMember() ──────────────────────────────────────────────
    // Called when the RECRUIT CREW MEMBER button is pressed.
    // Validates the name field, then adds the crew member.
    private void recruitCrewMember() {

        EditText etCrewName = findViewById(R.id.etCrewName);
        String name = etCrewName.getText().toString().trim();

        // Validation 1 — name must not be empty
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter a crew member name.", Toast.LENGTH_SHORT).show();
            return; // Stop here — do not proceed
        }

        // Validation 2 — crew must not already be full
        // (double-check in case user somehow bypasses NavigationActivity check)
        if (GameData.crewCount >= 2) {
            Toast.makeText(
                    this,
                    "Max limit reached. You cannot add more than 2 crew members at the start.",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        // ── All checks passed — add the crew member ───────────────────────
        // Increment the shared crew counter in GameData
        GameData.crewCount++;

        // Success message shown as a Toast popup
        Toast.makeText(
                this,
                "✓ " + name + " (" + selectedSpecialization + ") added successfully!",
                Toast.LENGTH_LONG
        ).show();

        // Go back to NavigationActivity automatically after recruiting
        finish();
    }
}
