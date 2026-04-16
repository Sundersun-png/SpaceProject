package com.example.spaceproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class StatisticsActivity extends AppCompatActivity {

    // ── Shared counters — increment these from other activities ──────────────
    public static int totalMissions    = 0;
    public static int totalCrew        = 0;
    public static int missionsWon      = 0;
    public static int crewLost         = 0;

    // ── Views ────────────────────────────────────────────────────────────────
    private TextView tvTotalMissions, tvTotalCrew, tvMissionsWon, tvCrewLost;
    private TextView tvWinRate, tvCoins;
    private View     winRateBar;
    private RecyclerView crewStatsRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        tvTotalMissions = findViewById(R.id.tvTotalMissions);
        tvTotalCrew     = findViewById(R.id.tvTotalCrew);
        tvMissionsWon   = findViewById(R.id.tvMissionsWon);
        tvCrewLost      = findViewById(R.id.tvCrewLost);
        tvWinRate       = findViewById(R.id.tvWinRate);
        winRateBar      = findViewById(R.id.winRateBar);
        tvCoins         = findViewById(R.id.tvCoins);
        crewStatsRecycler = findViewById(R.id.crewStatsRecycler);

        // Setup RecyclerView
        crewStatsRecycler.setLayoutManager(new LinearLayoutManager(this));
        crewStatsRecycler.setAdapter(new CrewStatsAdapter(GameData.crewList));

        // Bottom nav
        LinearLayout navQuarters  = findViewById(R.id.navQuarters);
        LinearLayout navSimulator = findViewById(R.id.navSimulator);
        LinearLayout navMission   = findViewById(R.id.navMission);
        LinearLayout navHospital  = findViewById(R.id.navHospital);

        navQuarters.setOnClickListener(v -> {
            startActivity(new Intent(this, QuartersActivity.class));
            finish();
        });
        navSimulator.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
        navMission.setOnClickListener(v -> {
            startActivity(new Intent(this, MissionControlActivity.class));
            finish();
        });
        navHospital.setOnClickListener(v -> {
            startActivity(new Intent(this, HospitalActivity.class));
            finish();
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            startActivity(new Intent(this, NavigationActivity.class));
            finish();
        });

        refreshUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUI(); // keep numbers fresh when returning from other screens
    }

    private void refreshUI() {
        if (tvCoins != null) tvCoins.setText(String.valueOf(GameData.coins));
        tvTotalMissions.setText(String.valueOf(totalMissions));
        tvTotalCrew.setText(String.valueOf(totalCrew));
        tvMissionsWon.setText(String.valueOf(missionsWon));
        tvCrewLost.setText(String.valueOf(crewLost));

        // Win rate percentage
        int rate = totalMissions > 0 ? (int) ((missionsWon * 100f) / totalMissions) : 0;
        tvWinRate.setText(rate + "%");

        // Animate the win-rate bar width as a fraction of its parent
        winRateBar.post(() -> {
            ViewGroup parent = (ViewGroup) winRateBar.getParent();
            if (parent != null) {
                int fullWidth = parent.getWidth();
                ViewGroup.LayoutParams params = winRateBar.getLayoutParams();
                params.width = (int) (fullWidth * (rate / 100f));
                winRateBar.setLayoutParams(params);
            }
        });

        // Update recycler if adapter exists
        if (crewStatsRecycler.getAdapter() != null) {
            crewStatsRecycler.getAdapter().notifyDataSetChanged();
        }
    }
}
