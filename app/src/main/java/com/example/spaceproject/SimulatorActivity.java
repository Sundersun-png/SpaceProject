package com.example.spaceproject;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.OnBackPressedCallback;

// ═══════════════════════════════════════════════════════════════
// SimulatorActivity.java
// Linked to: activity_simulator.xml
// - Tap a crew card to select → their portrait appears at top
// - 30s countdown displayed beside progress bar
// - Instant train costs 30 coins
// - On complete: +2 XP, isTrained = true
// ═══════════════════════════════════════════════════════════════

public class SimulatorActivity extends AppCompatActivity {

    // ── UI ────────────────────────────────────────────────────────
    private TextView tvCoins;
    private ImageView imgSelectedCrew;

    private LinearLayout cardCrewA;
    private TextView tvCrewAName, tvCrewARole, tvCrewAStats, tvCrewAStatus;

    private LinearLayout cardCrewB;
    private TextView tvCrewBName, tvCrewBRole, tvCrewBStats, tvCrewBStatus;

    private LinearLayout layoutTrainingPanel;
    private TextView tvTrainingName, tvCountdown;
    private ProgressBar progressTraining;
    private Button btnStartTraining, btnInstantTrain;

    // ── State ─────────────────────────────────────────────────────
    private int selectedCrewIndex = -1;
    private CountDownTimer countDownTimer;
    private boolean trainingInProgress = false;

    private static final int TRAIN_DURATION_MS = 30_000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulator);

        // ── Back button ───────────────────────────────────────────
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() { finish(); }
        });

        // ── Bind views ────────────────────────────────────────────
        tvCoins          = findViewById(R.id.tvCoins);
        imgSelectedCrew  = findViewById(R.id.imgSelectedCrew);

        cardCrewA     = findViewById(R.id.cardCrewA);
        tvCrewAName   = findViewById(R.id.tvCrewAName);
        tvCrewARole   = findViewById(R.id.tvCrewARole);
        tvCrewAStats  = findViewById(R.id.tvCrewAStats);
        tvCrewAStatus = findViewById(R.id.tvCrewAStatus);

        cardCrewB     = findViewById(R.id.cardCrewB);
        tvCrewBName   = findViewById(R.id.tvCrewBName);
        tvCrewBRole   = findViewById(R.id.tvCrewBRole);
        tvCrewBStats  = findViewById(R.id.tvCrewBStats);
        tvCrewBStatus = findViewById(R.id.tvCrewBStatus);

        layoutTrainingPanel = findViewById(R.id.layoutTrainingPanel);
        tvTrainingName      = findViewById(R.id.tvTrainingName);
        tvCountdown         = findViewById(R.id.tvCountdown);
        progressTraining    = findViewById(R.id.progressTraining);
        btnStartTraining    = findViewById(R.id.btnStartTraining);
        btnInstantTrain     = findViewById(R.id.btnInstantTrain);

        layoutTrainingPanel.setVisibility(View.GONE);
        imgSelectedCrew.setVisibility(View.GONE);

        // ── Crew card taps ────────────────────────────────────────
        cardCrewA.setOnClickListener(v -> selectCrew(0));
        cardCrewB.setOnClickListener(v -> selectCrew(1));

        // ── Training buttons ──────────────────────────────────────
        btnStartTraining.setOnClickListener(v -> startTraining());
        btnInstantTrain.setOnClickListener(v  -> instantTrain());

        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    // ── updateUI() ────────────────────────────────────────────────
    private void updateUI() {
        tvCoins.setText("🪙 " + GameData.coins);

        if (GameData.crewList.size() >= 1) {
            cardCrewA.setVisibility(View.VISIBLE);
            CrewMember a = GameData.crewList.get(0);
            tvCrewAName.setText(a.name);
            tvCrewARole.setText(a.role);
            tvCrewAStats.setText("XP: " + a.experience + "  |  Skill: " + a.getSkill());
            tvCrewAStatus.setText(a.isTrained ? "✅ Trained" : "⏳ Needs Training");
        } else {
            cardCrewA.setVisibility(View.GONE);
        }

        if (GameData.crewList.size() >= 2) {
            cardCrewB.setVisibility(View.VISIBLE);
            CrewMember b = GameData.crewList.get(1);
            tvCrewBName.setText(b.name);
            tvCrewBRole.setText(b.role);
            tvCrewBStats.setText("XP: " + b.experience + "  |  Skill: " + b.getSkill());
            tvCrewBStatus.setText(b.isTrained ? "✅ Trained" : "⏳ Needs Training");
        } else {
            cardCrewB.setVisibility(View.GONE);
        }
    }

    // ── selectCrew() ──────────────────────────────────────────────
    private void selectCrew(int index) {
        if (index >= GameData.crewList.size()) return;
        if (trainingInProgress) {
            Toast.makeText(this, "Training already in progress!", Toast.LENGTH_SHORT).show();
            return;
        }

        selectedCrewIndex = index;
        CrewMember selected = GameData.crewList.get(index);

        // Highlight selected, dim the other
        cardCrewA.setAlpha(index == 0 ? 1.0f : 0.5f);
        cardCrewB.setAlpha(index == 1 ? 1.0f : 0.5f);

        // ── Show portrait image at top ────────────────────────────
        String drawableName = selected.getPortraitDrawable();
        int resId = getResources().getIdentifier(drawableName, "drawable", getPackageName());
        if (resId != 0) {
            imgSelectedCrew.setImageResource(resId);
        } else {
            imgSelectedCrew.setImageResource(android.R.drawable.ic_menu_gallery);
        }
        imgSelectedCrew.setVisibility(View.VISIBLE);

        // ── Show training panel ───────────────────────────────────
        layoutTrainingPanel.setVisibility(View.VISIBLE);
        tvTrainingName.setText("Training: " + selected.name + " (" + selected.role + ")");
        tvCountdown.setText("30s");
        progressTraining.setProgress(0);
        btnInstantTrain.setText("⚡ Instant  (🪙 " + GameData.INSTANT_TRAIN_COST + ")");
        btnStartTraining.setText(selected.isTrained ? "Train Again (+2 XP)" : "▶  Start Training");
        btnInstantTrain.setEnabled(true);
    }

    // ── startTraining() ───────────────────────────────────────────
    private void startTraining() {
        if (selectedCrewIndex < 0) {
            Toast.makeText(this, "Select a crew member first.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (trainingInProgress) return;

        trainingInProgress = true;
        btnStartTraining.setEnabled(false);
        btnInstantTrain.setEnabled(true);

        countDownTimer = new CountDownTimer(TRAIN_DURATION_MS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int secondsLeft = (int)(millisUntilFinished / 1000);
                tvCountdown.setText(secondsLeft + "s");
                int progress = (int)(((TRAIN_DURATION_MS - millisUntilFinished) / (float) TRAIN_DURATION_MS) * 100);
                progressTraining.setProgress(progress);
            }

            @Override
            public void onFinish() {
                progressTraining.setProgress(100);
                tvCountdown.setText("Done!");
                completeTraining();
            }
        }.start();
    }

    // ── instantTrain() ────────────────────────────────────────────
    private void instantTrain() {
        if (selectedCrewIndex < 0) {
            Toast.makeText(this, "Select a crew member first.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (GameData.coins < GameData.INSTANT_TRAIN_COST) {
            Toast.makeText(this,
                    "Not enough coins! Need " + GameData.INSTANT_TRAIN_COST + " coins.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (countDownTimer != null) countDownTimer.cancel();

        GameData.coins -= GameData.INSTANT_TRAIN_COST;
        tvCoins.setText("🪙 " + GameData.coins);

        progressTraining.setProgress(100);
        tvCountdown.setText("Done!");
        completeTraining();
    }

    // ── completeTraining() ────────────────────────────────────────
    private void completeTraining() {
        trainingInProgress = false;
        btnStartTraining.setEnabled(true);
        btnInstantTrain.setEnabled(false);

        CrewMember trained = GameData.crewList.get(selectedCrewIndex);
        trained.train(0); // This internally increments trainingSessions

        updateUI();

        Toast.makeText(this,
                "🎉 " + trained.name + " training complete! +2 XP  |  Skill: " + trained.getSkill(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}
