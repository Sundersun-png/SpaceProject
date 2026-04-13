package com.example.spaceproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

// ═══════════════════════════════════════════════════════════════
// QuartersActivity.java
// Linked to: activity_quarters.xml
// Shows crew cards with live energy bar + recharge button
// ═══════════════════════════════════════════════════════════════

public class QuartersActivity extends AppCompatActivity {

    private TextView tvCoins;

    // Crew 1 views
    private LinearLayout cardCrew1;
    private TextView tvCrew1Name, tvCrew1Role, tvCrew1Stats, tvCrew1Trained, tvCrew1Energy;
    private View barEnergy1;
    private Button btnRestoreEnergy1, btnSendSimulator1, btnSendMission1;

    // Crew 2 views
    private LinearLayout cardCrew2;
    private TextView tvCrew2Name, tvCrew2Role, tvCrew2Stats, tvCrew2Trained, tvCrew2Energy;
    private View barEnergy2;
    private Button btnRestoreEnergy2, btnSendSimulator2, btnSendMission2;

    private TextView tvEmptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quarters);

        // ── Back button ───────────────────────────────────────────
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() { finish(); }
        });

        // ── Bind all views ────────────────────────────────────────
        tvCoins      = findViewById(R.id.tvCoins);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        cardCrew1         = findViewById(R.id.cardCrew1);
        tvCrew1Name       = findViewById(R.id.tvCrew1Name);
        tvCrew1Role       = findViewById(R.id.tvCrew1Role);
        tvCrew1Stats      = findViewById(R.id.tvCrew1Stats);
        tvCrew1Trained    = findViewById(R.id.tvCrew1Trained);
        tvCrew1Energy     = findViewById(R.id.tvCrew1Energy);
        barEnergy1        = findViewById(R.id.barEnergy1);
        btnRestoreEnergy1 = findViewById(R.id.btnRestoreEnergy1);
        btnSendSimulator1 = findViewById(R.id.btnSendSimulator1);
        btnSendMission1   = findViewById(R.id.btnSendMission1);

        cardCrew2         = findViewById(R.id.cardCrew2);
        tvCrew2Name       = findViewById(R.id.tvCrew2Name);
        tvCrew2Role       = findViewById(R.id.tvCrew2Role);
        tvCrew2Stats      = findViewById(R.id.tvCrew2Stats);
        tvCrew2Trained    = findViewById(R.id.tvCrew2Trained);
        tvCrew2Energy     = findViewById(R.id.tvCrew2Energy);
        barEnergy2        = findViewById(R.id.barEnergy2);
        btnRestoreEnergy2 = findViewById(R.id.btnRestoreEnergy2);
        btnSendSimulator2 = findViewById(R.id.btnSendSimulator2);
        btnSendMission2   = findViewById(R.id.btnSendMission2);

        // ── Button listeners ──────────────────────────────────────
        btnRestoreEnergy1.setOnClickListener(v -> restoreEnergy(0));
        btnSendSimulator1.setOnClickListener(v -> goToSimulator());
        btnSendMission1.setOnClickListener(v   -> goToMission(0));

        btnRestoreEnergy2.setOnClickListener(v -> restoreEnergy(1));
        btnSendSimulator2.setOnClickListener(v -> goToSimulator());
        btnSendMission2.setOnClickListener(v   -> goToMission(1));
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvCoins.setText("🪙 " + GameData.coins);
        updateUI();
    }

    // ── updateUI() ────────────────────────────────────────────────
    private void updateUI() {
        if (GameData.crewList.isEmpty()) {
            cardCrew1.setVisibility(View.GONE);
            cardCrew2.setVisibility(View.GONE);
            tvEmptyState.setVisibility(View.VISIBLE);
            return;
        }

        tvEmptyState.setVisibility(View.GONE);

        if (GameData.crewList.size() >= 1) {
            cardCrew1.setVisibility(View.VISIBLE);
            bindCrewCard(
                    GameData.crewList.get(0),
                    tvCrew1Name, tvCrew1Role, tvCrew1Stats, tvCrew1Trained,
                    tvCrew1Energy, barEnergy1, btnRestoreEnergy1, btnSendMission1
            );
        } else {
            cardCrew1.setVisibility(View.GONE);
        }

        if (GameData.crewList.size() >= 2) {
            cardCrew2.setVisibility(View.VISIBLE);
            bindCrewCard(
                    GameData.crewList.get(1),
                    tvCrew2Name, tvCrew2Role, tvCrew2Stats, tvCrew2Trained,
                    tvCrew2Energy, barEnergy2, btnRestoreEnergy2, btnSendMission2
            );
        } else {
            cardCrew2.setVisibility(View.GONE);
        }
    }

    // ── bindCrewCard() ────────────────────────────────────────────
    private void bindCrewCard(
            CrewMember crew,
            TextView nameView, TextView roleView, TextView statsView,
            TextView trainedView, TextView energyLabel, View energyBar,
            Button rechargeBtn, Button missionBtn) {

        nameView.setText(crew.name);
        roleView.setText(crew.role + "  |  Ability: " + crew.getAbility());
        statsView.setText("Skill: " + crew.getSkill() + "  |  XP: " + crew.experience);
        trainedView.setText(crew.isTrained ? "✅ Trained" : "⏳ Untrained");
        energyLabel.setText(crew.energy + " / " + crew.maxEnergy);

        // Energy bar — scale width as a fraction of its parent container
        energyBar.post(() -> {
            View parent = (View) energyBar.getParent();
            int fullWidth = parent.getWidth();
            float fraction = (float) crew.energy / (float) crew.maxEnergy;
            int barWidth = Math.max(0, (int) (fullWidth * fraction));

            android.view.ViewGroup.LayoutParams lp = energyBar.getLayoutParams();
            lp.width = barWidth;
            energyBar.setLayoutParams(lp);

            // Green → Yellow → Red depending on energy level
            if (fraction >= 1.0f) {
                energyBar.setBackgroundColor(0xFF2DCC82);
            } else if (fraction >= 0.4f) {
                energyBar.setBackgroundColor(0xFFFFD700);
            } else {
                energyBar.setBackgroundColor(0xFFCC3333);
            }
        });

        // Recharge button: disabled (dimmed) when already full
        boolean isFull = crew.energy >= crew.maxEnergy;
        rechargeBtn.setAlpha(isFull ? 0.4f : 1.0f);
        rechargeBtn.setEnabled(!isFull);

        // Mission button: only enabled when trained
        missionBtn.setAlpha(crew.isTrained ? 1.0f : 0.4f);
        missionBtn.setEnabled(crew.isTrained);
    }

    // ── restoreEnergy() ───────────────────────────────────────────
    private void restoreEnergy(int index) {
        if (index >= GameData.crewList.size()) return;
        CrewMember crew = GameData.crewList.get(index);

        if (crew.energy >= crew.maxEnergy) {
            Toast.makeText(this,
                    crew.name + " already has full energy!", Toast.LENGTH_SHORT).show();
            return;
        }

        crew.restoreEnergy();
        updateUI();
        Toast.makeText(this,
                "⚡ " + crew.name + "'s energy fully recharged!",
                Toast.LENGTH_SHORT).show();
    }

    // ── goToSimulator() ───────────────────────────────────────────
    private void goToSimulator() {
        startActivity(new Intent(this, SimulatorActivity.class));
    }

    // ── goToMission() ─────────────────────────────────────────────
    private void goToMission(int index) {
        if (index >= GameData.crewList.size()) return;
        CrewMember crew = GameData.crewList.get(index);

        if (!crew.isTrained) {
            Toast.makeText(this,
                    crew.name + " must complete training before going on a mission!",
                    Toast.LENGTH_LONG).show();
            return;
        }
        startActivity(new Intent(this, MissionControlActivity.class));
    }
}