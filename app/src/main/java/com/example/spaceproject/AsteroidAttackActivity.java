package com.example.spaceproject;

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
    private float ufoSpeedMultiplier = 1.0f;
    private boolean missionOver = false;

    private TextView tvCoins, tvEnergyLevel, tvDamage, tvPowerBoostCountdown, tvUfoEnergy;
    private ImageView ivUfo, ivRocketship;
    private ConstraintLayout gameContainer;
    private Button btnPowerBoost;
    private CrewMember pilot;

    private final List<ImageView> asteroids = new ArrayList<>();
    private final List<ImageView> meteors = new ArrayList<>();

    private final Handler gameHandler = new Handler();
    private final Random random = new Random();
    private Runnable meteorSpawner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asteroid_attack);

        gameContainer = findViewById(R.id.gameContainer);
        tvCoins = findViewById(R.id.tvCoins);
        tvEnergyLevel = findViewById(R.id.tvEnergyLevel);
        tvDamage = findViewById(R.id.tvDamage);
        tvUfoEnergy = findViewById(R.id.tvUfoEnergy);
        tvPowerBoostCountdown = findViewById(R.id.tvPowerBoostCountdown);
        ivUfo = findViewById(R.id.ivUfo);
        ivRocketship = findViewById(R.id.ivRocketship);

        btnPowerBoost = findViewById(R.id.btnPowerBoost);

        findViewById(R.id.btnAttack).setOnClickListener(v -> launchMeteor());
        findViewById(R.id.btnLeft).setOnClickListener(v -> moveRocket(-50));
        findViewById(R.id.btnRight).setOnClickListener(v -> moveRocket(50));
        
        btnPowerBoost.setOnClickListener(v -> activatePowerBoost());

        findPilot();
        
        if (pilot != null) {
            // Check if pilot is in hospital
            if ("Hospital".equalsIgnoreCase(pilot.location)) {
                Toast.makeText(this, "Pilot is in Hospital!", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            // Training mission - do not increment missionsParticipated here
        }

        updateUI();
        startGameLoop();
    }

    private void findPilot() {
        pilot = null;
        for (CrewMember m : GameData.crewList) {
            if ("Pilot".equalsIgnoreCase(m.role)) {
                pilot = m;
                break;
            }
        }
    }

    private void updateUI() {
        if (tvCoins != null) tvCoins.setText(String.valueOf(GameData.coins));
        if (tvEnergyLevel != null) tvEnergyLevel.setText("PILOT ENERGY: " + pilotEnergy);
        if (tvDamage != null) tvDamage.setText("HULL DAMAGE: " + damageTaken + "/5");
        if (tvUfoEnergy != null) tvUfoEnergy.setText("THREAT ENERGY: " + ufoEnergy);
        
        if (btnPowerBoost != null) {
            btnPowerBoost.setText(GameData.powerBoostAdded ? "Power Boost (FREE)" : "Power Boost (5🪙)");
        }
    }

    private void launchMeteor() {
        if (missionOver) return;
        ImageView meteor = new ImageView(this);
        meteor.setImageResource(R.drawable.meteor);
        meteor.setLayoutParams(new ViewGroup.LayoutParams(60, 60));
        meteor.setX(ivRocketship.getX() + (ivRocketship.getWidth() / 2f) - 30);
        meteor.setY(ivRocketship.getY() - 60);
        gameContainer.addView(meteor);
        meteors.add(meteor);
    }

    private void moveRocket(float deltaX) {
        if (missionOver) return;
        float newX = ivRocketship.getX() + deltaX;
        if (newX >= 0 && newX <= gameContainer.getWidth() - ivRocketship.getWidth()) {
            ivRocketship.setX(newX);
        }
    }

    private void activatePowerBoost() {
        if (isPowerBoostActive || missionOver) return;
        if (!GameData.powerBoostAdded) {
            if (GameData.coins < 5) return;
            GameData.coins -= 5;
        }
        isPowerBoostActive = true;
        updateUI();
        startAutoFire();

        new CountDownTimer(5000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvPowerBoostCountdown.setText("Boost: " + (millisUntilFinished / 1000.0) + "s");
            }
            @Override
            public void onFinish() {
                isPowerBoostActive = false;
                stopAutoFire();
                tvPowerBoostCountdown.setText("");
                updateUI();
            }
        }.start();
    }

    private void startAutoFire() {
        meteorSpawner = new Runnable() {
            @Override
            public void run() {
                if (isPowerBoostActive && !missionOver) {
                    launchMeteor();
                    gameHandler.postDelayed(this, 200);
                }
            }
        };
        gameHandler.post(meteorSpawner);
    }

    private void stopAutoFire() {
        if (meteorSpawner != null) gameHandler.removeCallbacks(meteorSpawner);
    }

    private void startGameLoop() {
        gameHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!missionOver) {
                    updateGame();
                    gameHandler.postDelayed(this, 30);
                }
            }
        }, 30);

        gameHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!missionOver) {
                    moveUfo();
                    dropAsteroid();
                    gameHandler.postDelayed(this, (long)(1500 / ufoSpeedMultiplier));
                }
            }
        }, 1500);
    }

    private void moveUfo() {
        int choice = random.nextInt(3);
        float screenWidth = gameContainer.getWidth();
        float targetX;
        if (choice == 0) targetX = 50; 
        else if (choice == 1) targetX = (screenWidth / 2f) - (ivUfo.getWidth() / 2f);
        else targetX = screenWidth - ivUfo.getWidth() - 50;
        ivUfo.animate().x(targetX).setDuration((long)(800 / ufoSpeedMultiplier)).start();
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
        Iterator<ImageView> mIter = meteors.iterator();
        while (mIter.hasNext()) {
            ImageView m = mIter.next();
            m.setY(m.getY() - speedMeteor);
            boolean hitAsteroid = false;
            Iterator<ImageView> aIterInner = asteroids.iterator();
            while (aIterInner.hasNext()) {
                ImageView a = aIterInner.next();
                if (checkCollision(m, a)) {
                    gameContainer.removeView(a);
                    aIterInner.remove();
                    hitAsteroid = true;
                    break;
                }
            }
            if (hitAsteroid) {
                gameContainer.removeView(m);
                mIter.remove();
                continue;
            }
            if (checkCollision(m, ivUfo)) {
                ufoEnergy -= 5;
                if (ufoEnergy < 0) ufoEnergy = 0;
                gameContainer.removeView(m);
                mIter.remove();
                updateUI();
                checkWin();
                continue;
            }
            if (m.getY() < -150) {
                gameContainer.removeView(m);
                mIter.remove();
            }
        }

        Iterator<ImageView> aIter = asteroids.iterator();
        while (aIter.hasNext()) {
            ImageView a = aIter.next();
            a.setY(a.getY() + speedAsteroid);
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
        if (v1 == null || v2 == null) return false;
        int[] loc1 = new int[2]; v1.getLocationOnScreen(loc1);
        int[] loc2 = new int[2]; v2.getLocationOnScreen(loc2);
        return (loc1[0] < loc2[0] + v2.getWidth() && loc1[0] + v1.getWidth() > loc2[0] &&
                loc1[1] < loc2[1] + v2.getHeight() && loc1[1] + v1.getHeight() > loc2[1]);
    }

    private void checkWin() {
        if (ufoEnergy <= 0 && !missionOver) {
            missionOver = true;
            gameHandler.removeCallbacksAndMessages(null);
            if (pilot != null) {
                pilot.experience += 1;
                pilot.skillLevel += 1;
                // Pilot training mission win updates trainingSessions
                pilot.setTrainingSessions(pilot.getTrainingSessions() + 1);
            }
            GameData.addCoins(20);
            Toast.makeText(this, "Training Successful! Pilot +1 XP, +1 Skill.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void checkGameOver() {
        if ((pilotEnergy <= 0 || damageTaken >= 5) && !missionOver) {
            missionOver = true;
            gameHandler.removeCallbacksAndMessages(null);
            Toast.makeText(this, "Training Failed! Try again later.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameHandler.removeCallbacksAndMessages(null);
    }
}
