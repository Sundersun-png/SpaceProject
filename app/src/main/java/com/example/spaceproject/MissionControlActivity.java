package com.example.spaceproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.OnBackPressedCallback;
import java.util.ArrayList;
import java.util.List;

// ═══════════════════════════════════════════════════════════════
// MissionControlActivity.java
// Linked to: activity_mission_control.xml
// - Crew A: spinner showing only TRAINED crew member names
// - Crew B: spinner showing 5 fixed opponents
// - Shows portrait image for each selection
// - Mission type: 5 options from project plan
// - Launch Mission button
// ═══════════════════════════════════════════════════════════════

public class MissionControlActivity extends AppCompatActivity {

    private TextView tvCoins;

    // Crew A (player's trained crew)
    private Spinner spinnerCrewA;
    private ImageView imgCrewA;
    private TextView tvCrewAStats;

    // Crew B (fixed opponents)
    private Spinner spinnerCrewB;
    private ImageView imgCrewB;

    // Mission type
    private Spinner spinnerMission;

    private Button btnLaunch;

    // Current selections
    private String selectedCrewAName = "";
    private String selectedCrewBName = "";
    private String selectedMission   = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_control);

        // ── Back button ───────────────────────────────────────────
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() { finish(); }
        });

        // ── Bind views ────────────────────────────────────────────
        tvCoins        = findViewById(R.id.tvCoins);
        spinnerCrewA   = findViewById(R.id.spinnerCrewA);
        imgCrewA       = findViewById(R.id.imgCrewA);
        tvCrewAStats   = findViewById(R.id.tvCrewAStats);
        spinnerCrewB   = findViewById(R.id.spinnerCrewB);
        imgCrewB       = findViewById(R.id.imgCrewB);
        spinnerMission = findViewById(R.id.spinnerMission);
        btnLaunch      = findViewById(R.id.btnLaunchMission);

        setupCrewASpinner();
        setupCrewBSpinner();
        setupMissionSpinner();

        // ── Launch Mission button ─────────────────────────────────
        btnLaunch.setOnClickListener(v -> launchMission());
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvCoins.setText("🪙 " + GameData.coins);
        setupCrewASpinner(); // refresh in case crew was just trained
    }

    // ── setupCrewASpinner() ───────────────────────────────────────
    // Only shows crew members who are TRAINED (isTrained == true)
    private void setupCrewASpinner() {
        List<String> trainedNames = new ArrayList<>();
        trainedNames.add("SELECT...");

        for (CrewMember c : GameData.crewList) {
            if (c.isTrained) {
                trainedNames.add(c.name + " (" + c.role + ")");
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, trainedNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCrewA.setAdapter(adapter);

        spinnerCrewA.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos == 0) {
                    selectedCrewAName = "";
                    imgCrewA.setVisibility(View.GONE);
                    tvCrewAStats.setText("");
                    return;
                }
                // Find matching crew member
                String label = trainedNames.get(pos);
                for (CrewMember c : GameData.crewList) {
                    if (label.startsWith(c.name)) {
                        selectedCrewAName = c.name;
                        // Show portrait based on role
                        showPortrait(imgCrewA, c.getPortraitDrawable());
                        tvCrewAStats.setText(
                                "Skill: " + c.getSkill() +
                                        "  |  Resilience: " + c.resilience +
                                        "  |  Energy: " + c.energy +
                                        "  |  XP: " + c.experience
                        );
                        break;
                    }
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // ── setupCrewBSpinner() ───────────────────────────────────────
    // Fixed 5 opponents from GameData.CREW_B_NAMES
    private void setupCrewBSpinner() {
        List<String> names = new ArrayList<>();
        names.add("SELECT...");
        for (String n : GameData.CREW_B_NAMES) names.add(n);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCrewB.setAdapter(adapter);

        spinnerCrewB.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos == 0) {
                    selectedCrewBName = "";
                    imgCrewB.setVisibility(View.GONE);
                    return;
                }
                selectedCrewBName = GameData.CREW_B_NAMES[pos - 1];
                showPortrait(imgCrewB, GameData.getCrewBPortrait(selectedCrewBName));
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // ── setupMissionSpinner() ─────────────────────────────────────
    private void setupMissionSpinner() {
        List<String> missions = new ArrayList<>();
        missions.add("SELECT MISSION...");
        for (String m : GameData.MISSION_TYPES) missions.add(m);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, missions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMission.setAdapter(adapter);

        spinnerMission.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selectedMission = pos == 0 ? "" : GameData.MISSION_TYPES[pos - 1];
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // ── showPortrait() ────────────────────────────────────────────
    // Loads drawable by name string. Falls back to placeholder if missing.
    private void showPortrait(ImageView imageView, String drawableName) {
        int resId = getResources().getIdentifier(drawableName, "drawable", getPackageName());
        if (resId != 0) {
            imageView.setImageResource(resId);
        } else {
            // Placeholder color block if image not added yet
            imageView.setImageResource(android.R.drawable.ic_menu_gallery);
        }
        imageView.setVisibility(View.VISIBLE);
    }

    // ── launchMission() ───────────────────────────────────────────
    private void launchMission() {
        if (selectedCrewAName.isEmpty()) {
            Toast.makeText(this, "Select a trained crew member for Crew A.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedCrewBName.isEmpty()) {
            Toast.makeText(this, "Select an opponent for Crew B.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedMission.isEmpty()) {
            Toast.makeText(this, "Select a mission type.", Toast.LENGTH_SHORT).show();
            return;
        }

        // All selected — launch
        // Pass selections to the next screen via Intent extras
        Intent intent = new Intent(this, MissionActivity.class);
        intent.putExtra("crewAName", selectedCrewAName);
        intent.putExtra("crewBName", selectedCrewBName);
        intent.putExtra("mission",   selectedMission);
        startActivity(intent);
    }
}
