package com.example.spaceproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnQuarter     = findViewById(R.id.btnQuarter);
        Button btnSimulator   = findViewById(R.id.btnSimulator);
        Button btnMission     = findViewById(R.id.btnMissionControl);
        Button btnStatistics  = findViewById(R.id.btnStatistics);
        Button btnHospital    = findViewById(R.id.btnHospital);
        Button btnAddCrew     = findViewById(R.id.btnAddCrew);

        btnQuarter.setOnClickListener(v ->
                startActivity(new Intent(this, QuartersActivity.class)));

        btnSimulator.setOnClickListener(v ->
                startActivity(new Intent(this, SimulatorActivity.class)));

        btnMission.setOnClickListener(v ->
                startActivity(new Intent(this, MissionControlActivity.class)));

        btnStatistics.setOnClickListener(v ->
                startActivity(new Intent(this, StatisticsActivity.class)));

        btnHospital.setOnClickListener(v ->
                startActivity(new Intent(this, HospitalActivity.class)));

        btnAddCrew.setOnClickListener(v ->
                startActivity(new Intent(this, RecruitActivity.class)));
    }
}