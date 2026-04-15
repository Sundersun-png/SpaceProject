package com.example.spaceproject;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JointMissionActivity extends AppCompatActivity {

    private static int missionCounter = 0;
    private static final Random random = new Random();

    private CrewMember crew1, crew2;
    private int crew1Energy = 100, crew2Energy = 100;
    private int threatEnergy, threatSkill, threatResilience;
    private String threatName;
    private boolean missionOver = false;

    private TextView tvMissionType, tvCrew1Name, tvCrew1Stats, tvCrew2Name, tvCrew2Stats, tvMissionLog;
    private Button btnNextRound;
    private ScrollView logScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joint_mission);

        // UI Initialization
        tvMissionType = findViewById(R.id.tvMissionType);
        tvCrew1Name = findViewById(R.id.tvCrew1Name);
        tvCrew1Stats = findViewById(R.id.tvCrew1Stats);
        tvCrew2Name = findViewById(R.id.tvCrew2Name);
        tvCrew2Stats = findViewById(R.id.tvCrew2Stats);
        tvMissionLog = findViewById(R.id.tvMissionLog);
        btnNextRound = findViewById(R.id.btnNextRound);
        logScrollView = findViewById(R.id.logScrollView);

        // Load Crew Members (finding them in GameData)
        String name1 = getIntent().getStringExtra("name1");
        String name2 = getIntent().getStringExtra("name2");
        for (CrewMember m : GameData.crewList) {
            if (m.name.equals(name1)) crew1 = m;
            if (m.name.equals(name2)) crew2 = m;
        }

        setupMission();
        updateUI();

        btnNextRound.setOnClickListener(v -> {
            if (missionOver) {
                finish();
            } else {
                runRound();
            }
        });
    }

    private void setupMission() {
        String[] types = {"Solar Flare Defense", "Pirate Raid"};
        String type = types[random.nextInt(2)];
        tvMissionType.setText(type);
        threatName = (type.equals("Solar Flare Defense")) ? "Solar Flare" : "Space Pirate";

        threatSkill = 4 + missionCounter;
        threatResilience = 2;
        threatEnergy = 20 + (missionCounter * 3);

        log("MISSION STARTED: " + type);
        log("Threat: " + threatName + " (HP: " + threatEnergy + ", Skill: " + threatSkill + ")");
        log("--------------------------------");
    }

    private void runRound() {
        btnNextRound.setText("NEXT ROUND");

        // Crew A Attacks
        if (crew1Energy > 0) {
            attack(crew1, threatName);
            if (threatEnergy <= 0) {
                endMission(true);
                return;
            }
            // Threat Retaliates on Crew A
            retaliate(threatName, crew1);
        }

        // Crew B Attacks
        if (crew2Energy > 0) {
            attack(crew2, threatName);
            if (threatEnergy <= 0) {
                endMission(true);
                return;
            }
            // Threat Retaliates on Crew B
            retaliate(threatName, crew2);
        }

        if (crew1Energy <= 0 && crew2Energy <= 0) {
            endMission(false);
        }

        updateUI();
    }

    private void attack(CrewMember attacker, String target) {
        int damage = Math.max(1, attacker.getSkill() - threatResilience + random.nextInt(5));
        threatEnergy -= damage;
        log(attacker.name + " attacks " + target + " for " + damage + " DMG!");
    }

    private void retaliate(String attackerName, CrewMember target) {
        int damage = Math.max(1, threatSkill + random.nextInt(5));
        if (target == crew1) crew1Energy -= damage;
        else crew2Energy -= damage;
        log(attackerName + " retaliates against " + target.name + " for " + damage + " DMG!");
    }

    private void endMission(boolean success) {
        missionOver = true;
        btnNextRound.setText("RETURN TO BASE");
        log("--------------------------------");
        if (success) {
            log("MISSION SUCCESS!");
            missionCounter++;
            GameData.addCoins(10 + (missionCounter * 2));
            if (crew1Energy > 0) crew1.train(0);
            if (crew2Energy > 0) crew2.train(0);
        } else {
            log("MISSION FAILED: Both crew members incapacitated.");
        }
        updateUI();
    }

    private void updateUI() {
        tvCrew1Name.setText(crew1.name);
        tvCrew1Stats.setText("HP: " + Math.max(0, crew1Energy) + " | Skill: " + crew1.getSkill());
        tvCrew2Name.setText(crew2.name);
        tvCrew2Stats.setText("HP: " + Math.max(0, crew2Energy) + " | Skill: " + crew2.getSkill());
        
        if (threatEnergy <= 0) {
            tvMissionType.setText("THREAT ELIMINATED");
        }
    }

    private void log(String message) {
        tvMissionLog.append(message + "\n");
        logScrollView.post(() -> logScrollView.fullScroll(View.FOCUS_DOWN));
    }
}
