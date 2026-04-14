package com.example.spaceproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SimulatorActivity extends AppCompatActivity {

    private CrewAdapter crewAdapter;
    private List<CrewMember> crewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulator);

        crewList = CrewRepository.getCrewList();

        crewAdapter = new CrewAdapter(crewList);
        RecyclerView rvCrew = findViewById(R.id.rvSimulatorCrew);
        rvCrew.setLayoutManager(new LinearLayoutManager(this));
        rvCrew.setAdapter(crewAdapter);

        Button btnTrain = findViewById(R.id.btnTrainCrew);
        btnTrain.setOnClickListener(v -> {
            int count = 0;
            for (CrewMember crew : crewList) {
                if (crew.isSelected()) {
                    crew.gainExp(1);
                    count++;
                }
            }
            if (count == 0) {
                Toast.makeText(this, "Select crew members to train", Toast.LENGTH_SHORT).show();
            } else {
                crewAdapter.notifyDataSetChanged();
                Toast.makeText(this,
                        count + " crew member(s) trained! Skill increased.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}