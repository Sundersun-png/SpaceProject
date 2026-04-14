package com.example.spaceproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class QuartersActivity extends AppCompatActivity {

    private List<CrewMember> crewList;
    private CrewMember currentCrew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quarters);

        Button btnRestoreEnergy = findViewById(R.id.btnRestoreEnergy);
        Button btnMoveToSimulator = findViewById(R.id.btnMoveToSimulator);
        Button btnMoveToMission = findViewById(R.id.btnMoveToMission);

        TextView tvCrewName = findViewById(R.id.tvCrewName);
        TextView tvEnergy = findViewById(R.id.tvEnergy);
        TextView tvSkill = findViewById(R.id.tvSkill);
        TextView tvExp = findViewById(R.id.tvExp);
        TextView tvBonus = findViewById(R.id.tvBonus);

        crewList = CrewRepository.getCrewList();

        if (!crewList.isEmpty()) {
            currentCrew = crewList.get(0);
        }

        if (currentCrew != null) {
            tvCrewName.setText(currentCrew.getName());
            tvEnergy.setText("Max Energy: " + currentCrew.getMaxEnergy());
            tvSkill.setText("Skill: " + currentCrew.getSkill());
            tvExp.setText("Exp: " + currentCrew.getExperience());
            tvBonus.setText("Bonus: 0");
        }

        btnRestoreEnergy.setOnClickListener(v -> {
            if (currentCrew != null) {
                currentCrew.setEnergy(currentCrew.getMaxEnergy());
                Toast.makeText(this, "Energy Restored", Toast.LENGTH_SHORT).show();
            }
        });

        btnMoveToSimulator.setOnClickListener(v ->
                startActivity(new Intent(this, SimulatorActivity.class)));

        btnMoveToMission.setOnClickListener(v ->
                startActivity(new Intent(this, MissionControlActivity.class)));
    }
}