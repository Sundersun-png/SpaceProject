package com.example.spaceproject;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Iterator;
import java.util.Random;

public class JointMissionActivity extends AppCompatActivity {

    private CrewMember crewA;
    private CrewMember crewB;
    private String threatName;
    private int threatSkill;
    private int threatEnergy;
    private int threatMaxEnergy;
    private int threatResilience;
    
    private CrewMember currentAttacker;
    private TextView tvLog, tvCoins, tvStats;
    private ScrollView scrollView;
    private Button btnAttack, btnDefend, btnSpecial;
    private boolean missionOver = false;
    private int currentRound = 1;

    private ImageView ivCrewA, ivCrewB, ivThreat;
    private String difficultyLevel = "EASY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joint_mission);

        crewA = (CrewMember) getIntent().getSerializableExtra("crewA");
        crewB = (CrewMember) getIntent().getSerializableExtra("crewB");

        tvLog = findViewById(R.id.tvLog);
        tvCoins = findViewById(R.id.tvCoins);
        tvStats = findViewById(R.id.tvStatus); 
        scrollView = findViewById(R.id.scrollView);
        btnAttack = findViewById(R.id.btnAttack);
        btnDefend = findViewById(R.id.btnDefend);
        btnSpecial = findViewById(R.id.btnSpecial);
        
        ivCrewA = findViewById(R.id.ivCrewA);
        ivCrewB = findViewById(R.id.ivCrewB);
        ivThreat = findViewById(R.id.ivThreat);

        setupMission();
        currentAttacker = (crewA != null && crewA.currentEnergy > 0) ? crewA : crewB;
        
        updateUI();

        btnAttack.setOnClickListener(v -> handleAction("ATTACK"));
        btnDefend.setOnClickListener(v -> handleAction("DEFEND"));
        btnSpecial.setOnClickListener(v -> handleAction("SPECIAL"));
    }

    private void setupMission() {
        int avgXP = 0;
        int countXP = 0;
        if (crewA != null) { avgXP += crewA.experience; countXP++; }
        if (crewB != null) { avgXP += crewB.experience; countXP++; }
        if (countXP > 0) avgXP /= countXP;

        if (avgXP <= 3) {
            difficultyLevel = "EASY";
            threatSkill = 4;
            threatResilience = 1;
            threatMaxEnergy = 20;
        } else if (avgXP <= 8) {
            difficultyLevel = "MEDIUM";
            threatSkill = 6;
            threatResilience = 2;
            threatMaxEnergy = 30;
        } else {
            difficultyLevel = "HARD";
            threatSkill = 9;
            threatResilience = 3;
            threatMaxEnergy = 45;
        }
        threatEnergy = threatMaxEnergy;

        Random rand = new Random();
        threatName = rand.nextBoolean() ? "Solar Flare" : "Pirate Raider";
        if (GameData.successfulMissionsCount == 0 && avgXP <= 3) threatName = "Asteroid Storm";
        
        if (ivCrewA != null && crewA != null) ivCrewA.setImageResource(getCrewDrawable(crewA.role));
        if (ivCrewB != null && crewB != null) ivCrewB.setImageResource(getCrewDrawable(crewB.role));
        if (ivThreat != null) ivThreat.setImageResource(R.drawable.ufo);

        appendLog("=== MISSION: " + threatName + " ===", Color.WHITE, true);
        appendLog("Threat: " + threatName + " (skill: " + threatSkill + ", resilience: " + threatResilience + ", energy: " + threatEnergy + "/" + threatMaxEnergy + ")", Color.LTGRAY, false);
        
        if (crewA != null) {
            appendLog("Crew Member A: " + crewA.role + "(" + crewA.name + ") skill: " + crewA.skillLevel + "; res: " + crewA.resilience + "; exp: " + crewA.experience + "; energy: " + crewA.currentEnergy + "/" + crewA.maxEnergy, Color.LTGRAY, false);
        }
        if (crewB != null) {
            appendLog("Crew Member B: " + crewB.role + "(" + crewB.name + ") skill: " + crewB.skillLevel + "; res: " + crewB.resilience + "; exp: " + crewB.experience + "; energy: " + crewB.currentEnergy + "/" + crewB.maxEnergy, Color.LTGRAY, false);
        }
        
        appendLog("--- Round " + currentRound + " ---", Color.BLUE, true);
        printStats();
        updateLowEnergyTint();
    }

    private void printStats() {
        StringBuilder sb = new StringBuilder();
        sb.append("Threat: ").append(threatEnergy).append("/").append(threatMaxEnergy).append(" NRG | ").append(threatSkill).append(" SKL\n");
        if (crewA != null) sb.append(crewA.name).append(": ").append(crewA.currentEnergy).append("/").append(crewA.maxEnergy).append(" NRG | ").append(crewA.skillLevel).append(" SKL\n");
        if (crewB != null) sb.append(crewB.name).append(": ").append(crewB.currentEnergy).append("/").append(crewB.maxEnergy).append(" NRG | ").append(crewB.skillLevel).append(" SKL");
        
        if (tvStats != null) tvStats.setText(sb.toString());
    }

    private int getCrewDrawable(String role) {
        if (role == null) return R.drawable.ic_launcher_foreground;
        if (role.equalsIgnoreCase("Pilot")) return R.drawable.pilot;
        if (role.equalsIgnoreCase("Engineer")) return R.drawable.engineer;
        if (role.equalsIgnoreCase("Medic")) return R.drawable.medic;
        if (role.equalsIgnoreCase("Scientist")) return R.drawable.scientist;
        if (role.equalsIgnoreCase("Soldier")) return R.drawable.soldier;
        return R.drawable.ic_launcher_foreground;
    }

    private void updateUI() {
        if (tvCoins != null) tvCoins.setText(String.valueOf(GameData.coins));
        
        int color = Color.GREEN;
        if (difficultyLevel.equals("MEDIUM")) color = Color.YELLOW;
        else if (difficultyLevel.equals("HARD")) color = Color.RED;
        
        SpannableString title = new SpannableString("Joint Mission - " + difficultyLevel);
        title.setSpan(new ForegroundColorSpan(color), 16, title.length(), 0);
        setTitle(title);

        if (missionOver) {
            btnAttack.setVisibility(View.GONE);
            btnDefend.setVisibility(View.GONE);
            btnSpecial.setVisibility(View.GONE);
            
            Button btnExit = new Button(this);
            btnExit.setText("RETURN TO MISSION CONTROL");
            btnExit.setOnClickListener(v -> finish());
            LinearLayout parent = (LinearLayout) btnAttack.getParent();
            if (parent != null && parent.getChildCount() < 4) {
                parent.addView(btnExit);
            }
            return;
        }
        String attackerName = (currentAttacker != null) ? currentAttacker.name : "N/A";
        btnAttack.setText("ATTACK (" + attackerName + ")");
        
        String specialAction = getSpecialButtonName();
        btnSpecial.setText(specialAction);
    }

    private String getSpecialButtonName() {
        boolean hasEngineer = (crewA != null && "Engineer".equalsIgnoreCase(crewA.role)) || 
                             (crewB != null && "Engineer".equalsIgnoreCase(crewB.role));
        boolean hasScientist = (crewA != null && "Scientist".equalsIgnoreCase(crewA.role)) || 
                              (crewB != null && "Scientist".equalsIgnoreCase(crewB.role));
        boolean hasMedic = (crewA != null && "Medic".equalsIgnoreCase(crewA.role)) || 
                          (crewB != null && "Medic".equalsIgnoreCase(crewB.role));

        if (hasEngineer && GameData.torpedoAdded) return "USE TORPEDO";
        if (hasScientist && GameData.weaknessPotionAdded) return "WEAK ENEMY";
        if (hasMedic && GameData.pillUnlocked) return "💊 USE PILL";
        
        return "CRITICAL ATTACK";
    }

    private void handleAction(String action) {
        if (missionOver || currentAttacker == null) return;
        
        Random rand = new Random();
        int damageToThreat = 0;
        boolean defended = false;
        
        if (action.equals("ATTACK") || action.equals("SPECIAL")) {
            animateAttack();
        }

        appendLog(currentAttacker.role + "(" + currentAttacker.name + ") acts against " + threatName, Color.YELLOW, false);

        if (action.equals("ATTACK")) {
            damageToThreat = Math.max(1, currentAttacker.skillLevel - threatResilience);
            appendLog("Damage dealt: " + currentAttacker.skillLevel + " - " + threatResilience + " = " + damageToThreat, Color.GREEN, false);
        } else if (action.equals("DEFEND")) {
            defended = true;
            appendLog(currentAttacker.name + " is defending!", Color.LTGRAY, false);
        } else if (action.equals("SPECIAL")) {
            String specialName = btnSpecial.getText().toString();
            if (specialName.contains("PILL")) {
                damageToThreat = 15;
                appendLog("💊 PILL POWER! 15 damage!", Color.GREEN, true);
            } else if (specialName.equals("USE TORPEDO")) {
                damageToThreat = 20;
                GameData.torpedoAdded = false;
                appendLog("🚀 TORPEDO! 20 damage!", Color.GREEN, true);
            } else if (specialName.equals("WEAK ENEMY")) {
                threatSkill -= 2;
                GameData.weaknessPotionAdded = false;
                appendLog("🧪 Weakness potion! Threat Skill reduced.", Color.LTGRAY, false);
            } else {
                damageToThreat = (currentAttacker.skillLevel * 2) - threatResilience;
                currentAttacker.currentEnergy -= 2;
                appendLog("CRITICAL! Damage: (" + currentAttacker.skillLevel + "x2) - " + threatResilience + " = " + damageToThreat + " (Cost: 2 NRG)", Color.GREEN, true);
            }
        }

        if (damageToThreat > 0) {
            animateThreatDamage();
        }

        threatEnergy -= damageToThreat;
        if (threatEnergy < 0) threatEnergy = 0;
        appendLog(threatName + " energy: " + threatEnergy + "/" + threatMaxEnergy, Color.LTGRAY, false);
        
        if (threatEnergy <= 0) {
            appendLog("The threat has been neutralized!", Color.GREEN, true);
            endMission(true);
            return;
        }

        // Retaliation
        appendLog(threatName + " retaliates against " + currentAttacker.role + "(" + currentAttacker.name + ")", Color.RED, false);
        int damageToCrew = Math.max(1, threatSkill - currentAttacker.resilience);
        if (defended) damageToCrew /= 2;
        
        currentAttacker.currentEnergy -= damageToCrew;
        if (currentAttacker.currentEnergy < 0) currentAttacker.currentEnergy = 0;
        animateCrewDamage(currentAttacker);
        appendLog("Damage dealt: " + threatSkill + " - " + currentAttacker.resilience + " = " + damageToCrew, Color.RED, false);
        appendLog(currentAttacker.role + "(" + currentAttacker.name + ") energy: " + currentAttacker.currentEnergy + "/" + currentAttacker.maxEnergy, Color.LTGRAY, false);

        updateLowEnergyTint();

        if (currentAttacker.currentEnergy <= 0) {
            appendLog(currentAttacker.name + " has fallen!", Color.RED, true);
            
            if (currentAttacker == crewA) {
                currentAttacker = (crewB != null && crewB.currentEnergy > 0) ? crewB : null;
            } else {
                currentAttacker = (crewA != null && crewA.currentEnergy > 0) ? crewA : null;
            }
            if (currentAttacker != null) {
                 currentRound++;
                 appendLog("--- Round " + currentRound + " ---", Color.BLUE, true);
            }
        } else {
            // Swap turn
            if (crewA != null && crewB != null && crewA.currentEnergy > 0 && crewB.currentEnergy > 0) {
                if (currentAttacker == crewB) {
                    currentRound++;
                    appendLog("--- Round " + currentRound + " ---", Color.BLUE, true);
                }
                currentAttacker = (currentAttacker == crewA) ? crewB : crewA;
            } else {
                 currentRound++;
                 appendLog("--- Round " + currentRound + " ---", Color.BLUE, true);
            }
        }

        if (currentAttacker == null) {
            appendLog("Mission failed. All crew members lost.", Color.RED, true);
            endMission(false);
        } else {
            printStats();
            updateUI();
        }
        scrollToBottom();
    }

    private void animateAttack() {
        if (currentAttacker == crewA) {
            TranslateAnimation anim = new TranslateAnimation(0, 20, 0, 0);
            anim.setDuration(150);
            anim.setRepeatMode(Animation.REVERSE);
            anim.setRepeatCount(1);
            ivCrewA.startAnimation(anim);
        } else {
            TranslateAnimation anim = new TranslateAnimation(0, -20, 0, 0);
            anim.setDuration(150);
            anim.setRepeatMode(Animation.REVERSE);
            anim.setRepeatCount(1);
            ivCrewB.startAnimation(anim);
        }
    }

    private void animateThreatDamage() {
        TranslateAnimation shake = new TranslateAnimation(0, -15, 0, 0);
        shake.setDuration(50);
        shake.setRepeatMode(Animation.REVERSE);
        shake.setRepeatCount(5);
        ivThreat.startAnimation(shake);
    }

    private void animateCrewDamage(CrewMember member) {
        ImageView targetIv = (member == crewA) ? ivCrewA : ivCrewB;
        TranslateAnimation shake = new TranslateAnimation(0, -10, 0, 0);
        shake.setDuration(50);
        shake.setRepeatMode(Animation.REVERSE);
        shake.setRepeatCount(3);
        targetIv.startAnimation(shake);
    }

    private void updateLowEnergyTint() {
        if (crewA != null && ivCrewA != null) {
            if ((float) crewA.currentEnergy / crewA.maxEnergy < 0.3f) {
                ivCrewA.setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
            } else {
                ivCrewA.clearColorFilter();
            }
        }
        if (crewB != null && ivCrewB != null) {
            if ((float) crewB.currentEnergy / crewB.maxEnergy < 0.3f) {
                ivCrewB.setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
            } else {
                ivCrewB.clearColorFilter();
            }
        }
    }

    private void appendLog(String text, int color, boolean bold) {
        SpannableString span = new SpannableString(text + "\n");
        span.setSpan(new ForegroundColorSpan(color), 0, span.length(), 0);
        if (bold) {
            span.setSpan(new StyleSpan(Typeface.BOLD), 0, span.length(), 0);
            if (text.contains("MISSION")) {
                span.setSpan(new RelativeSizeSpan(1.2f), 0, span.length(), 0);
            }
        }
        tvLog.append(span);
        scrollToBottom();
    }

    private void endMission(boolean success) {
        missionOver = true;
        if (success) {
            GameData.successfulMissionsCount++;
            GameData.addCoins(10);
            appendLog("MISSION COMPLETE", Color.WHITE, true);
            Toast.makeText(this, "Joint Mission Success!", Toast.LENGTH_SHORT).show();
            updateGameDataOnSuccess();
        } else {
            appendLog("MISSION FAILED", Color.WHITE, true);
            removeCrewFromGame(crewA);
            removeCrewFromGame(crewB);
        }
        updateUI();
    }

    private void updateGameDataOnSuccess() {
        for (CrewMember m : GameData.crewList) {
            if (crewA != null && m.name.equals(crewA.name) && crewA.currentEnergy > 0) {
                m.experience += 2;
                m.skillLevel += 1;
                m.currentEnergy = crewA.currentEnergy;
            }
            if (crewB != null && m.name.equals(crewB.name) && crewB.currentEnergy > 0) {
                m.experience += 2;
                m.skillLevel += 1;
                m.currentEnergy = crewB.currentEnergy;
            }
        }
    }

    private void removeCrewFromGame(CrewMember crew) {
        if (crew == null) return;
        Iterator<CrewMember> it = GameData.crewList.iterator();
        while (it.hasNext()) {
            if (it.next().name.equals(crew.name)) {
                it.remove();
                break;
            }
        }
    }

    private void log(String msg) {
        appendLog(msg, Color.LTGRAY, false);
    }

    private void scrollToBottom() {
        if (scrollView != null) {
            scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
        }
    }
}
