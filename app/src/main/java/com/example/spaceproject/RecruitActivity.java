package com.example.spaceproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// ═══════════════════════════════════════════════════════════════
// RecruitActivity.java — Add Crew Member Screen
// Linked to: activity_recruit.xml
// Creates proper subclass objects (Pilot, Medic, etc.)
// Adds them to GameData.crewList and sends them to Quarters.
// ═══════════════════════════════════════════════════════════════

public class RecruitActivity extends AppCompatActivity {

    private String selectedSpecialization = "Pilot";

    private TextView tvSelectedClass, tvSkillValue, tvResilienceValue;
    private TextView tvEnergyValue, tvAbilityValue, tvCoins;

    private LinearLayout cardPilot, cardMedic, cardScientist;
    private LinearLayout cardEngineer, cardSoldier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruit);

        // Bind views
        tvCoins           = findViewById(R.id.tvCoins);
        tvSelectedClass   = findViewById(R.id.tvSelectedClass);
        tvSkillValue      = findViewById(R.id.tvSkillValue);
        tvResilienceValue = findViewById(R.id.tvResilienceValue);
        tvEnergyValue     = findViewById(R.id.tvEnergyValue);
        tvAbilityValue    = findViewById(R.id.tvAbilityValue);

        cardPilot     = findViewById(R.id.cardPilot);
        cardMedic     = findViewById(R.id.cardMedic);
        cardScientist = findViewById(R.id.cardScientist);
        cardEngineer  = findViewById(R.id.cardEngineer);
        cardSoldier   = findViewById(R.id.cardSoldier);

        // Back button → NavigationActivity
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Class card selection
        cardPilot.setOnClickListener(v     -> selectSpecialization("Pilot"));
        cardMedic.setOnClickListener(v     -> selectSpecialization("Medic"));
        cardScientist.setOnClickListener(v -> selectSpecialization("Scientist"));
        cardEngineer.setOnClickListener(v  -> selectSpecialization("Engineer"));
        cardSoldier.setOnClickListener(v   -> selectSpecialization("Soldier"));

        selectSpecialization("Pilot"); // default

        // Recruit button
        findViewById(R.id.btnConfirmRecruit).setOnClickListener(v -> recruitCrewMember());
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvCoins.setText("🪙 " + GameData.coins);
    }

    private void selectSpecialization(String spec) {
        selectedSpecialization = spec;
        tvSelectedClass.setText("— " + spec.toUpperCase() + " —");

        switch (spec) {
            case "Pilot":
                tvSkillValue.setText("7"); tvResilienceValue.setText("6");
                tvAbilityValue.setText("Evade"); break;
            case "Medic":
                tvSkillValue.setText("4"); tvResilienceValue.setText("8");
                tvAbilityValue.setText("Heal Teammate"); break;
            case "Scientist":
                tvSkillValue.setText("6"); tvResilienceValue.setText("5");
                tvAbilityValue.setText("Boost Attack"); break;
            case "Engineer":
                tvSkillValue.setText("5"); tvResilienceValue.setText("9");
                tvAbilityValue.setText("Repair"); break;
            case "Soldier":
                tvSkillValue.setText("9"); tvResilienceValue.setText("7");
                tvAbilityValue.setText("Heavy Attack"); break;
        }
        tvEnergyValue.setText("100");
        resetAllCards();
        highlightCard(spec);
    }

    private void highlightCard(String spec) {
        switch (spec) {
            case "Pilot":     cardPilot.setBackgroundColor(0xBBCC2D82);     break;
            case "Medic":     cardMedic.setBackgroundColor(0xBBCC2D82);     break;
            case "Scientist": cardScientist.setBackgroundColor(0xBBCC2D82); break;
            case "Engineer":  cardEngineer.setBackgroundColor(0xBBCC2D82);  break;
            case "Soldier":   cardSoldier.setBackgroundColor(0xBBCC2D82);   break;
        }
    }

    private void resetAllCards() {
        int dark = 0x331A3A5C;
        cardPilot.setBackgroundColor(dark);
        cardMedic.setBackgroundColor(dark);
        cardScientist.setBackgroundColor(dark);
        cardEngineer.setBackgroundColor(dark);
        cardSoldier.setBackgroundColor(dark);
    }

    private void recruitCrewMember() {
        EditText etCrewName = findViewById(R.id.etCrewName);
        String name = etCrewName.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter a crew member name.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (GameData.crewList.size() >= 2) {
            Toast.makeText(this,
                    "Max limit reached. Complete 5 missions to recruit more.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Create the correct subclass and add to GameData
        CrewMember newMember = GameData.createCrew(name, selectedSpecialization);
        newMember.location = "Quarters";
        GameData.crewList.add(newMember);

        Toast.makeText(this,
                "✓ " + name + " (" + selectedSpecialization + ") added! They are in Quarters.",
                Toast.LENGTH_LONG).show();

        finish(); // back to NavigationActivity
    }
}