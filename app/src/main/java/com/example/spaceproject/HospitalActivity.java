package com.example.spaceproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class HospitalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital);

        List<CrewMember> crewList = CrewRepository.getCrewList();

        // Count crew who are injured (energy below max)
        int injured = 0;
        int recovering = 0;
        for (CrewMember crew : crewList) {
            if (crew.getEnergy() <= 0) {
                injured++;
            } else if (crew.getEnergy() < crew.getMaxEnergy()) {
                recovering++;
            }
        }

        TextView tvBeds       = findViewById(R.id.tvBedsOccupied);
        TextView tvRecoveries = findViewById(R.id.tvExpectedRecoveries);
        Button   btnBack      = findViewById(R.id.btnHospitalBack);

        tvBeds.setText(String.valueOf(injured));
        tvRecoveries.setText(String.valueOf(recovering));

        btnBack.setOnClickListener(v -> finish());
    }
}