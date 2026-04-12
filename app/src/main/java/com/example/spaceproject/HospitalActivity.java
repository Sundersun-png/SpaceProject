package com.example.spaceproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class HospitalActivity extends AppCompatActivity {

    // ── Patient model ────────────────────────────────────────────────────────

    public enum PatientStatus { CRITICAL, RECOVERING, HEALED }

    public static class Patient {
        String name;
        PatientStatus status;
        long statusStartTime; // ms when they entered current status

        Patient(String name, PatientStatus status) {
            this.name            = name;
            this.status          = status;
            this.statusStartTime = System.currentTimeMillis();
        }

        /** Seconds elapsed in the current status. */
        long secondsElapsed() {
            return (System.currentTimeMillis() - statusStartTime) / 1000;
        }

        /** Seconds left until next status change (0 when already progressing). */
        int secondsRemaining() {
            if (status == PatientStatus.CRITICAL)   return Math.max(0, 60 - (int) secondsElapsed());
            if (status == PatientStatus.RECOVERING) return Math.max(0, 30 - (int) secondsElapsed());
            return 0;
        }

        /** Advance to the next status and reset the timer. */
        void advance() {
            if (status == PatientStatus.CRITICAL) {
                status = PatientStatus.RECOVERING;
                statusStartTime = System.currentTimeMillis();
            } else if (status == PatientStatus.RECOVERING) {
                status = PatientStatus.HEALED;
                statusStartTime = System.currentTimeMillis();
            }
        }
    }

    // ── Shared patient list (other screens can admit patients here) ───────────
    public static final List<Patient> patients = new ArrayList<>();

    // ── Views ────────────────────────────────────────────────────────────────
    private TextView     tvBedsOccupied, tvExpectedRecoveries;
    private TextView     tvCritical, tvRecovering, tvHealed;
    private TextView     tvNoPatients;
    private LinearLayout patientList;
    private View         patientListScroll;

    // ── 1-second ticker ──────────────────────────────────────────────────────
    private final Handler  timerHandler  = new Handler();
    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            tickPatients();
            refreshUI();
            timerHandler.postDelayed(this, 1000);
        }
    };

    // ── Lifecycle ────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital);

        tvBedsOccupied       = findViewById(R.id.tvBedsOccupied);
        tvExpectedRecoveries = findViewById(R.id.tvExpectedRecoveries);
        tvCritical           = findViewById(R.id.tvCritical);
        tvRecovering         = findViewById(R.id.tvRecovering);
        tvHealed             = findViewById(R.id.tvHealed);
        tvNoPatients         = findViewById(R.id.tvNoPatients);
        patientList          = findViewById(R.id.patientList);
        patientListScroll    = findViewById(R.id.patientListScroll);

        // Demo patient — remove once real crew admission is wired up
        if (patients.isEmpty()) {
            patients.add(new Patient("Andre", PatientStatus.CRITICAL));
        }

        // Back arrow
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Bottom nav
        LinearLayout navQuarters  = findViewById(R.id.navQuarters);
        LinearLayout navSimulator = findViewById(R.id.navSimulator);
        LinearLayout navMission   = findViewById(R.id.navMission);

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

        LinearLayout navStats = findViewById(R.id.navStats);
        navStats.setOnClickListener(v -> {
            startActivity(new Intent(this, StatisticsActivity.class));
            finish();
        });

        refreshUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        timerHandler.post(timerRunnable);   // start ticking
    }

    @Override
    protected void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable); // stop ticking off-screen
    }

    // ── Timer logic ───────────────────────────────────────────────────────────

    /** Check every patient and advance their status when time is up. */
    private void tickPatients() {
        for (Patient p : patients) {
            if (p.status == PatientStatus.CRITICAL   && p.secondsElapsed() >= 60) p.advance();
            if (p.status == PatientStatus.RECOVERING && p.secondsElapsed() >= 30) p.advance();
        }
    }

    // ── UI refresh ────────────────────────────────────────────────────────────

    private void refreshUI() {
        int critical = 0, recovering = 0, healed = 0;
        for (Patient p : patients) {
            if      (p.status == PatientStatus.CRITICAL)   critical++;
            else if (p.status == PatientStatus.RECOVERING) recovering++;
            else if (p.status == PatientStatus.HEALED)     healed++;
        }

        tvBedsOccupied.setText(String.valueOf(patients.size()));
        tvExpectedRecoveries.setText(String.valueOf(recovering + healed));
        tvCritical.setText(String.valueOf(critical));
        tvRecovering.setText(String.valueOf(recovering));
        tvHealed.setText(String.valueOf(healed));

        if (patients.isEmpty()) {
            tvNoPatients.setVisibility(View.VISIBLE);
            patientListScroll.setVisibility(View.GONE);
        } else {
            tvNoPatients.setVisibility(View.GONE);
            patientListScroll.setVisibility(View.VISIBLE);
            buildPatientRows();
        }
    }

    /** Inflate item_patient.xml for each patient and bind their data. */
    private void buildPatientRows() {
        patientList.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (Patient p : patients) {
            View row = inflater.inflate(R.layout.item_patient, patientList, false);

            TextView tvName       = row.findViewById(R.id.tvPatientName);
            View circleCritical   = row.findViewById(R.id.circleCritical);
            View circleRecovering = row.findViewById(R.id.circleRecovering);
            View circleHealed     = row.findViewById(R.id.circleHealed);
            TextView tvCountdown  = row.findViewById(R.id.tvCountdown);
            Button btnSend        = row.findViewById(R.id.btnSendToMission);

            tvName.setText(p.name);

            // Fill only the current status circle; others stay as empty outlines
            circleCritical.setBackgroundResource(
                p.status == PatientStatus.CRITICAL   ? R.drawable.circle_filled_red    : R.drawable.circle_empty);
            circleRecovering.setBackgroundResource(
                p.status == PatientStatus.RECOVERING ? R.drawable.circle_filled_yellow : R.drawable.circle_empty);
            circleHealed.setBackgroundResource(
                p.status == PatientStatus.HEALED     ? R.drawable.circle_filled_green  : R.drawable.circle_empty);

            // Countdown label + Send button visibility
            if (p.status == PatientStatus.HEALED) {
                tvCountdown.setText("Done");
                tvCountdown.setTextColor(0xFF90EE90);
                btnSend.setVisibility(View.VISIBLE);
                btnSend.setOnClickListener(v -> {
                    patients.remove(p);
                    startActivity(new Intent(this, MissionControlActivity.class));
                    finish();
                });
            } else {
                tvCountdown.setText(p.secondsRemaining() + "s");
                tvCountdown.setTextColor(0xFFAADDFF);
                btnSend.setVisibility(View.GONE);
            }

            // Gap between rows
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 8);
            row.setLayoutParams(params);

            patientList.addView(row);
        }
    }
}
