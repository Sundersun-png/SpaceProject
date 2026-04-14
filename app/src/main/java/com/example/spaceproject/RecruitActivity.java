package com.example.spaceproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class RecruitActivity extends AppCompatActivity {

    private EditText etName;
    private RadioGroup rgSpecialization;
    private TextView tvSkillPreview, tvResiliencePreview, tvEnergyPreview, tvAbilityPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruit);

        etName             = findViewById(R.id.etCrewName);
        rgSpecialization   = findViewById(R.id.rgSpecialization);
        tvSkillPreview     = findViewById(R.id.tvSkillPreview);
        tvResiliencePreview = findViewById(R.id.tvResiliencePreview);
        tvEnergyPreview    = findViewById(R.id.tvEnergyPreview);
        tvAbilityPreview   = findViewById(R.id.tvAbilityPreview);

        // Update preview when specialization changes
        rgSpecialization.setOnCheckedChangeListener((group, checkedId) -> updatePreview(checkedId));

        // Confirm recruitment
        Button btnConfirm = findViewById(R.id.btnConfirmRecruit);
        btnConfirm.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Enter a crew member name", Toast.LENGTH_SHORT).show();
                return;
            }

            int checkedId = rgSpecialization.getCheckedRadioButtonId();
            if (checkedId == -1) {
                Toast.makeText(this, "Select a specialization", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if recruitment is unlocked (1 crew with 5+ missions)
            List<CrewMember> crewList = CrewRepository.getCrewList();
            boolean canRecruit = false;
            for (CrewMember crew : crewList) {
                if (crew.getExperience() >= 5) { canRecruit = true; break; }
            }
            // Allow recruitment at start if fewer than 2 members
            if (crewList.size() < 2) canRecruit = true;

            if (!canRecruit) {
                Toast.makeText(this,
                        "A crew member needs 5+ missions before recruiting",
                        Toast.LENGTH_LONG).show();
                return;
            }

            RadioButton selected = findViewById(checkedId);
            String spec = selected.getText().toString();

            CrewMember newMember = new CrewMember(name, spec, 0, 10, 5);
            CrewRepository.getCrewList().add(newMember);

            Toast.makeText(this,
                    name + " (" + spec + ") joined the crew!",
                    Toast.LENGTH_LONG).show();
            finish();
        });

        Button btnBack = findViewById(R.id.btnRecruitBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void updatePreview(int checkedId) {
        String skill = "5", resilience = "8", energy = "100", ability = "...";

        if (checkedId == R.id.rbPilot) {
            skill = "5"; resilience = "8"; energy = "100"; ability = "Evade";
        } else if (checkedId == R.id.rbMedic) {
            skill = "5"; resilience = "6"; energy = "100"; ability = "Heal Teammate";
        } else if (checkedId == R.id.rbScientist) {
            skill = "6"; resilience = "5"; energy = "100"; ability = "Boost Attack";
        } else if (checkedId == R.id.rbEngineer) {
            skill = "5"; resilience = "7"; energy = "100"; ability = "Repair";
        } else if (checkedId == R.id.rbSoldier) {
            skill = "8"; resilience = "6"; energy = "100"; ability = "Heavy Attack";
        }

        tvSkillPreview.setText(skill);
        tvResiliencePreview.setText(resilience);
        tvEnergyPreview.setText(energy);
        tvAbilityPreview.setText(ability);
    }
}