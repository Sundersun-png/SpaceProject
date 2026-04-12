package com.example.spaceproject;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SimulatorActivity extends AppCompatActivity {

    ListView crewListView;
    Button trainButton;

    ArrayList<CrewMember> crewList;
    ArrayList<CrewMember> selectedCrew;

    ArrayAdapter<String> adapter;
    ArrayList<String> displayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulator);

        crewListView = findViewById(R.id.crewListView);
        trainButton = findViewById(R.id.btnTrainCrew);

        crewList = new ArrayList<>();
        selectedCrew = new ArrayList<>();
        displayList = new ArrayList<>();

        // Sample crew (you can expand this)
        crewList.add(new CrewMember("Alice", "Engineer", 5));
        crewList.add(new CrewMember("Bob", "Pilot", 4));
        crewList.add(new CrewMember("Clara", "Scientist", 6));
        crewList.add(new CrewMember("Dan", "Medic", 3));
        crewList.add(new CrewMember("Eve", "Soldier", 5));

        updateDisplay();

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_multiple_choice,
                displayList);

        crewListView.setAdapter(adapter);
        crewListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        // Selecting crew members
        crewListView.setOnItemClickListener((parent, view, position, id) -> {
            CrewMember selected = crewList.get(position);

            if (selectedCrew.contains(selected)) {
                selectedCrew.remove(selected);
            } else {
                if (selectedCrew.size() < 2) {
                    selectedCrew.add(selected);
                } else {
                    Toast.makeText(this, "Only 2 crew members allowed!", Toast.LENGTH_SHORT).show();
                    crewListView.setItemChecked(position, false);
                }
            }
        });

        // Train button logic
        trainButton.setOnClickListener(v -> trainCrew());
    }

    private void trainCrew() {
        if (selectedCrew.size() != 2) {
            Toast.makeText(this, "Select exactly 2 crew members!", Toast.LENGTH_SHORT).show();
            return;
        }

        int bonus = 0;

        // Check for Scientist bonus
        for (CrewMember member : selectedCrew) {
            if (member.isScientist()) {
                bonus = 1;
                break;
            }
        }

        // Apply training
        for (CrewMember member : selectedCrew) {
            member.train(bonus);
        }

        Toast.makeText(this, "Training Complete! +" + (1 + bonus) + " XP", Toast.LENGTH_SHORT).show();

        selectedCrew.clear();
        crewListView.clearChoices();

        updateDisplay();
        adapter.notifyDataSetChanged();
    }

    private void updateDisplay() {
        displayList.clear();
        for (CrewMember member : crewList) {
            displayList.add(member.toString());
        }
    }
}