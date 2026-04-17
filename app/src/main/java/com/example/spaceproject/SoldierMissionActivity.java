package com.example.spaceproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class SoldierMissionActivity extends AppCompatActivity {

    private int soldierHealth = 100;
    private int soldierMaxHealth = 100;
    private int enemyHealth;
    private int enemyMaxHealth;
    private int enemySkill;

    private int grenadeCooldown = 0;
    private boolean isDodging = false;
    private boolean isPlayerTurn = true;

    private TextView tvEnergy, tvThreatSkill, tvThreatResilience, tvDamage, tvCoins, tvCrewStats, tvCombatLog;
    private ProgressBar soldierHealthBar, enemyHealthBar;
    private Button btnShoot, btnDodge, btnGrenade;
    private ImageView imgSoldier, imgAlien, projectile;

    private CrewMember soldier;
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soldier_mission);

        // Bind views
        tvEnergy = findViewById(R.id.tvEnergy);
        tvThreatSkill = findViewById(R.id.tvThreatSkill);
        tvThreatResilience = findViewById(R.id.tvThreatResilience);
        tvDamage = findViewById(R.id.tvDamage);
        tvCoins = findViewById(R.id.tvCoins);
        tvCrewStats = findViewById(R.id.tvCrewStats);
        tvCombatLog = findViewById(R.id.tvCombatLog);

        soldierHealthBar = findViewById(R.id.soldierHealthBar);
        enemyHealthBar = findViewById(R.id.enemyHealthBar);

        btnShoot = findViewById(R.id.btnShoot);
        btnDodge = findViewById(R.id.btnDodge);
        btnGrenade = findViewById(R.id.btnGrenade);

        imgSoldier = findViewById(R.id.imgSoldier);
        imgAlien = findViewById(R.id.imgAlien);
        projectile = findViewById(R.id.projectile);

        tvCombatLog.setMovementMethod(new ScrollingMovementMethod());

        // Find soldier
        for (CrewMember m : GameData.crewList) {
            if ("Soldier".equals(m.role)) {
                soldier = m;
                break;
            }
        }

        if (soldier != null) {
            // Check if soldier is in hospital
            if ("Hospital".equalsIgnoreCase(soldier.location)) {
                Toast.makeText(this, "Soldier is in Hospital!", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            // Training missions do not count as 'missions' in Statistics page
        }

        // Initialize Enemy scaling
        initEnemy();

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        btnShoot.setOnClickListener(v -> performAction("SHOOT"));
        btnDodge.setOnClickListener(v -> performAction("DODGE"));
        btnGrenade.setOnClickListener(v -> performAction("GRENADE"));

        updateUI();
        addToLog("Mission Start! Tyranide sighted.");
    }

    private void initEnemy() {
        int xp = (soldier != null) ? soldier.experience : 0;
        if (xp <= 3) {
            enemySkill = 12; // Increased from 4
            enemyMaxHealth = 100; // Increased from 20
        } else if (xp <= 8) {
            enemySkill = 15; // Increased from 6
            enemyMaxHealth = 150; // Increased from 35
        } else {
            enemySkill = 20; // Increased from 9
            enemyMaxHealth = 200; // Increased from 50
        }
        enemyHealth = enemyMaxHealth;
        enemyHealthBar.setMax(enemyMaxHealth);
        enemyHealthBar.setProgress(enemyHealth);
    }

    private void performAction(String action) {
        if (!isPlayerTurn) return;
        isPlayerTurn = false;

        switch (action) {
            case "SHOOT":
                animateProjectile(R.drawable.bullet, 300, false);
                break;
            case "GRENADE":
                if (grenadeCooldown > 0) {
                    isPlayerTurn = true;
                    return;
                }
                animateProjectile(R.drawable.grenade, 500, true);
                grenadeCooldown = 3;
                break;
            case "DODGE":
                isDodging = true;
                addToLog("Soldier is dodging!");
                new Handler().postDelayed(this::enemyRetaliate, 500);
                break;
        }
    }

    private void animateProjectile(int drawableRes, int duration, boolean isGrenade) {
        projectile.setImageResource(drawableRes);
        projectile.setVisibility(View.VISIBLE);

        // Calculate positions
        float startX = imgSoldier.getX() + imgSoldier.getWidth() / 2;
        float startY = imgSoldier.getY() + imgSoldier.getHeight() / 3;
        float endX = imgAlien.getX() + imgAlien.getWidth() / 4;
        float endY = imgAlien.getY() + imgAlien.getHeight() / 3;

        projectile.setX(startX);
        projectile.setY(startY);

        TranslateAnimation anim = new TranslateAnimation(0, endX - startX, 0, endY - startY);
        anim.setDuration(duration);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation) {
                projectile.setVisibility(View.INVISIBLE);
                shakeEnemy();
                applyPlayerDamage(isGrenade);
                new Handler().postDelayed(() -> enemyRetaliate(), 500);
            }
        });
        projectile.startAnimation(anim);
    }

    private void applyPlayerDamage(boolean isGrenade) {
        int damage = random.nextInt(5) + 5;
        if (isGrenade) damage *= 3; // Buffed grenade slightly but enemy has more health

        enemyHealth -= damage;
        addToLog("Soldier deals " + damage + " damage!");
        updateUI();
        checkMissionStatus();
    }

    private void shakeEnemy() {
        TranslateAnimation shake = new TranslateAnimation(0, 10, 0, 0);
        shake.setDuration(50);
        shake.setRepeatCount(5);
        shake.setRepeatMode(Animation.REVERSE);
        imgAlien.startAnimation(shake);
    }

    private void enemyRetaliate() {
        if (enemyHealth <= 0) return;

        // Animate enemy move
        TranslateAnimation move = new TranslateAnimation(0, -20, 0, 0);
        move.setDuration(200);
        move.setRepeatCount(1);
        move.setRepeatMode(Animation.REVERSE);
        imgAlien.startAnimation(move);

        new Handler().postDelayed(() -> animateEnemyProjectile(R.drawable.laser_beam, 300), 200);
    }

    private void animateEnemyProjectile(int drawableRes, int duration) {
        projectile.setImageResource(drawableRes);
        projectile.setVisibility(View.VISIBLE);

        float startX = imgAlien.getX() + imgAlien.getWidth() / 4;
        float startY = imgAlien.getY() + imgAlien.getHeight() / 3;
        float endX = imgSoldier.getX() + imgSoldier.getWidth() / 2;
        float endY = imgSoldier.getY() + imgSoldier.getHeight() / 3;

        projectile.setX(startX);
        projectile.setY(startY);

        TranslateAnimation anim = new TranslateAnimation(0, endX - startX, 0, endY - startY);
        anim.setDuration(duration);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation) {
                projectile.setVisibility(View.INVISIBLE);
                applyEnemyDamage();
            }
        });
        projectile.startAnimation(anim);
    }

    private void applyEnemyDamage() {
        int damage = random.nextInt(enemySkill) + 5;
        if (isDodging) {
            damage /= 2;
            isDodging = false;
        }
        soldierHealth -= damage;
        addToLog("Tyranide hits for " + damage + " damage!");

        if (grenadeCooldown > 0) grenadeCooldown--;

        isPlayerTurn = true;
        updateUI();
        checkMissionStatus();
    }

    private void addToLog(String msg) {
        String currentText = tvCombatLog.getText().toString();
        String[] lines = currentText.split("\n");
        StringBuilder newLog = new StringBuilder();

        int start = Math.max(0, lines.length - 3);
        for (int i = start; i < lines.length; i++) {
            if (!lines[i].isEmpty()) {
                newLog.append(lines[i]).append("\n");
            }
        }
        newLog.append("> ").append(msg);
        tvCombatLog.setText(newLog.toString());
    }

    private void updateUI() {
        if (tvCoins != null) tvCoins.setText(String.valueOf(GameData.coins));
        tvEnergy.setText("Energy: " + soldierHealth + "%");
        tvThreatSkill.setText("Threat Skill: " + enemySkill);
        tvThreatResilience.setText("Threat Resilience: " + Math.max(0, enemyHealth));

        soldierHealthBar.setProgress(Math.max(0, soldierHealth));
        enemyHealthBar.setProgress(Math.max(0, enemyHealth));

        if (soldier != null && tvCrewStats != null) {
            tvCrewStats.setText("Skill: " + soldier.skillLevel + " | XP: " + soldier.experience);
        }

        btnGrenade.setEnabled(grenadeCooldown == 0);
        if (grenadeCooldown > 0) {
            btnGrenade.setText("GRENADE (" + grenadeCooldown + ")");
        } else {
            btnGrenade.setText("GRENADE");
        }
    }

    private void checkMissionStatus() {
        if (enemyHealth <= 0) {
            if (soldier != null) {
                soldier.experience += 1;
                soldier.skillLevel += 1;
                // Training sessions updated for successful training mission
                soldier.setTrainingSessions(soldier.getTrainingSessions() + 1);
                Toast.makeText(this, "Training Successful! Soldier +1 XP, +1 Skill.", Toast.LENGTH_LONG).show();
            }
            GameData.addCoins(10);
            finish();
        } else if (soldierHealth <= 0) {
            Toast.makeText(this, "Training Failed! Try again later.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
