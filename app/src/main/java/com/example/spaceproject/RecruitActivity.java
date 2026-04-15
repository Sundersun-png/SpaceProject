package com.example.spaceproject;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RecruitActivity extends AppCompatActivity {

    private String selectedSpecialization = "Pilot";

    private TextView tvSelectedClass, tvSkillValue, tvResilienceValue;
    private TextView tvEnergyValue, tvAbilityValue, tvCoins, tvCurrentCrewList;

    private LinearLayout cardPilot, cardMedic, cardScientist;
    private LinearLayout cardEngineer, cardSoldier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruit);

        // Bind views
        tvSelectedClass   = findViewById(R.id.tvSelectedClass);
        tvSkillValue      = findViewById(R.id.tvSkillValue);
        tvResilienceValue = findViewById(R.id.tvResilienceValue);
        tvEnergyValue     = findViewById(R.id.tvEnergyValue);
        tvAbilityValue    = findViewById(R.id.tvAbilityValue);
        tvCurrentCrewList = findViewById(R.id.tvCurrentCrewList);

        cardPilot     = findViewById(R.id.cardPilot);
        cardMedic     = findViewById(R.id.cardMedic);
        cardScientist = findViewById(R.id.cardScientist);
        cardEngineer  = findViewById(R.id.cardEngineer);
        cardSoldier   = findViewById(R.id.cardSoldier);

        tvCoins = findViewById(R.id.tvCoins);
        if (tvCoins != null) tvCoins.setText(String.valueOf(GameData.coins));

        // Back button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Class card selection
        cardPilot.setOnClickListener(v     -> selectSpecialization("Pilot"));
        cardMedic.setOnClickListener(v     -> selectSpecialization("Medic"));
        cardScientist.setOnClickListener(v -> selectSpecialization("Scientist"));
        cardEngineer.setOnClickListener(v  -> selectSpecialization("Engineer"));
        cardSoldier.setOnClickListener(v   -> selectSpecialization("Soldier"));

        selectSpecialization("Pilot"); 
        updateCurrentCrewDisplay();

        // Recruit button
        findViewById(R.id.btnConfirmRecruit).setOnClickListener(v -> recruitCrewMember());
    }

    private void updateCurrentCrewDisplay() {
        if (GameData.crewList.isEmpty()) {
            tvCurrentCrewList.setText("No crew members yet.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (CrewMember m : GameData.crewList) {
                sb.append("• ").append(m.name)
                  .append(" (").append(m.role).append(")")
                  .append(" - Skill: ").append(m.skillLevel)
                  .append(", XP: ").append(m.experience)
                  .append("\n");
            }
            tvCurrentCrewList.setText(sb.toString().trim());
        }
    }

    private void selectSpecialization(String spec) {
        selectedSpecialization = spec;
        tvSelectedClass.setText("— " + spec.toUpperCase() + " —");

        switch (spec) {
            case "Pilot":
                tvSkillValue.setText("5"); tvResilienceValue.setText("4"); tvEnergyValue.setText("20");
                tvAbilityValue.setText("Evade"); break;
            case "Medic":
                tvSkillValue.setText("7"); tvResilienceValue.setText("2"); tvEnergyValue.setText("18");
                tvAbilityValue.setText("Heal Teammate"); break;
            case "Scientist":
                tvSkillValue.setText("8"); tvResilienceValue.setText("1"); tvEnergyValue.setText("17");
                tvAbilityValue.setText("Boost Attack"); break;
            case "Engineer":
                tvSkillValue.setText("6"); tvResilienceValue.setText("3"); tvEnergyValue.setText("19");
                tvAbilityValue.setText("Repair"); break;
            case "Soldier":
                tvSkillValue.setText("9"); tvResilienceValue.setText("0"); tvEnergyValue.setText("16");
                tvAbilityValue.setText("Heavy Attack"); break;
        }
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
        if (GameData.crewList.size() >= 5) { // Increased limit for easier testing/play
            Toast.makeText(this, "Max crew limit reached.", Toast.LENGTH_LONG).show();
            return;
        }

        CrewMember newMember = GameData.createCrew(name, selectedSpecialization);
        newMember.location = "Quarters";
        GameData.crewList.add(newMember);

        Toast.makeText(this, "✓ " + name + " (" + selectedSpecialization + ") recruited!", Toast.LENGTH_LONG).show();
        updateCurrentCrewDisplay();
        etCrewName.setText("");
    }
}
