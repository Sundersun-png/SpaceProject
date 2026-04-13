package com.example.spaceproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class AsteroidAttackActivity extends AppCompatActivity {

    private int pilotEnergy = 100;
    private int damageTaken = 0;
    private int ufoEnergy = 100;
    private boolean isPowerBoostActive = false;

    private TextView tvCoins, tvEnergyLevel, tvDamage, tvPowerBoostCountdown;
    private ImageView ivUfo, ivRocketship;
    private ConstraintLayout gameContainer;
    private CrewMember pilot;

    private final List<ImageView> asteroids = new ArrayList<>();
    private final List<ImageView> meteors = new ArrayList<>();

    private final Handler gameHandler = new Handler();
    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asteroid_attack);

        gameContainer = findViewById(R.id.gameContainer);
        tvCoins = findViewById(R.id.tvCoins);
        tvEnergyLevel = findViewById(R.id.tvEnergyLevel);
        tvDamage = findViewById(R.id.tvDamage);
        tvPowerBoostCountdown = findViewById(R.id.tvPowerBoostCountdown);
        ivUfo = findViewById(R.id.ivUfo);
        ivRocketship = findViewById(R.id.ivRocketship);

        // Find pilot
        for (CrewMember m : GameData.crewList) {
            if ("Pilot".equals(m.role)) {
                pilot = m;
                break;
            }
        }
        if (pilot == null) pilot = new Pilot("Default Pilot");

        updateUI();

        findViewById(R.id.btnAttack).setOnClickListener(v -> launchMeteor());
        findViewById(R.id.btnLeft).setOnClickListener(v -> moveRocket(-50));
        findViewById(R.id.btnRight).setOnClickListener(v -> moveRocket(50));
        findViewById(R.id.btnPowerBoost).setOnClickListener(v -> activatePowerBoost());

        startGameLoop();
    }

    private void moveRocket(float deltaX) {
        float newX = ivRocketship.getX() + deltaX;
        if (newX >= 0 && newX <= gameContainer.getWidth() - ivRocketship.getWidth()) {
            ivRocketship.setX(newX);
        }
    }

    private void launchMeteor() {
        ImageView meteor = new ImageView(this);
        meteor.setImageResource(R.drawable.meteor);
        meteor.setLayoutParams(new ViewGroup.LayoutParams(60, 60));
        meteor.setX(ivRocketship.getX() + (ivRocketship.getWidth() / 2f) - 30);
        meteor.setY(ivRocketship.getY() - 60);
        gameContainer.addView(meteor);
        meteors.add(meteor);
    }

    private void activatePowerBoost() {
        if (isPowerBoostActive) return;
        if (GameData.coins < 5) {
            Toast.makeText(this, "Not enough coins!", Toast.LENGTH_SHORT).show();
            return;
        }

        GameData.coins -= 5;
        isPowerBoostActive = true;
        updateUI();

        new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvPowerBoostCountdown.setText("Power Boost: " + (millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                isPowerBoostActive = false;
                tvPowerBoostCountdown.setText("");
            }
        }.start();
    }

    private void startGameLoop() {
        gameHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateGame();
                gameHandler.postDelayed(this, 30);
            }
        }, 30);

        // UFO Movement & Asteroid Drop
        gameHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                moveUfo();
                dropAsteroid();
                gameHandler.postDelayed(this, 1500);
            }
        }, 1500);
    }

    private void moveUfo() {
        int choice = random.nextInt(3);
        float screenWidth = gameContainer.getWidth();
        float targetX;
        if (choice == 0) targetX = 50; // Left
        else if (choice == 1) targetX = (screenWidth / 2f) - (ivUfo.getWidth() / 2f); // Center
        else targetX = screenWidth - ivUfo.getWidth() - 50; // Right

        ivUfo.animate().x(targetX).setDuration(800).start();
    }

    private void dropAsteroid() {
        ImageView asteroid = new ImageView(this);
        asteroid.setImageResource(R.drawable.asteroid);
        asteroid.setLayoutParams(new ViewGroup.LayoutParams(80, 80));
        asteroid.setX(ivUfo.getX() + (ivUfo.getWidth() / 2f) - 40);
        asteroid.setY(ivUfo.getY() + ivUfo.getHeight());
        gameContainer.addView(asteroid);
        asteroids.add(asteroid);
    }

    private void updateGame() {
        float speedMeteor = isPowerBoostActive ? 25f : 15f;
        float speedAsteroid = 10f;

        // Move Meteors
        Iterator<ImageView> mIter = meteors.iterator();
        while (mIter.hasNext()) {
            ImageView m = mIter.next();
            m.setY(m.getY() - speedMeteor);
            
            // Check collision with UFO
            if (checkCollision(m, ivUfo)) {
                ufoEnergy -= 10;
                gameContainer.removeView(m);
                mIter.remove();
                checkWin();
                continue;
            }

            if (m.getY() < -100) {
                gameContainer.removeView(m);
                mIter.remove();
            }
        }

        // Move Asteroids
        Iterator<ImageView> aIter = asteroids.iterator();
        while (aIter.hasNext()) {
            ImageView a = aIter.next();
            a.setY(a.getY() + speedAsteroid);

            // Collision with Meteors
            Iterator<ImageView> mIter2 = meteors.iterator();
            boolean destroyed = false;
            while (mIter2.hasNext()) {
                ImageView m = mIter2.next();
                if (checkCollision(a, m)) {
                    gameContainer.removeView(a);
                    gameContainer.removeView(m);
                    mIter2.remove();
                    destroyed = true;
                    break;
                }
            }

            if (destroyed) {
                aIter.remove();
                continue;
            }

            // Collision with Rocket
            if (checkCollision(a, ivRocketship)) {
                damageTaken++;
                pilotEnergy -= 10;
                gameContainer.removeView(a);
                aIter.remove();
                updateUI();
                checkGameOver();
            } else if (a.getY() > gameContainer.getHeight()) {
                gameContainer.removeView(a);
                aIter.remove();
            }
        }
    }

    private boolean checkCollision(View v1, View v2) {
        int[] loc1 = new int[2];
        v1.getLocationOnScreen(loc1);
        int[] loc2 = new int[2];
        v2.getLocationOnScreen(loc2);

        return (loc1[0] < loc2[0] + v2.getWidth() &&
                loc1[0] + v1.getWidth() > loc2[0] &&
                loc1[1] < loc2[1] + v2.getHeight() &&
                loc1[1] + v1.getHeight() > loc2[1]);
    }

    private void checkWin() {
        if (ufoEnergy <= 0) {
            gameHandler.removeCallbacksAndMessages(null);
            pilot.experience += 2;
            GameData.addCoins(20);
            Toast.makeText(this, "UFO Defeated! Pilot XP +2!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void checkGameOver() {
        if (pilotEnergy <= 0 || damageTaken >= 5) {
            gameHandler.removeCallbacksAndMessages(null);
            pilot.location = "Hospital";
            // Admit pilot to the hospital system
            HospitalActivity.patients.add(new HospitalActivity.Patient(pilot, HospitalActivity.PatientStatus.CRITICAL));

            Toast.makeText(this, "Pilot injured! Sent to Hospital.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void updateUI() {
        tvCoins.setText(String.valueOf(GameData.coins));
        tvEnergyLevel.setText("Energy: " + pilotEnergy);
        tvDamage.setText("Damage: " + damageTaken + "/5");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameHandler.removeCallbacksAndMessages(null);
    }
}
