package com.example.spaceproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class InventoryActivity extends AppCompatActivity {

    // Items: 0=Spaceship, 1=Gun, 2=Grenade, 3=Missile
    private static final String[] ITEM_EMOJI = {"🛸", "🔫", "💣", "🚀"};
    private static final int[]    ITEM_SKILL = {2,    6,    4,    8};   // required engineer skill

    private final int[] statusIds  = {
            R.id.tvStatusSpaceship, R.id.tvStatusGun,
            R.id.tvStatusGrenade,   R.id.tvStatusMissile
    };
    private final int[] skillReqIds = {
            R.id.tvSkillReqSpaceship, R.id.tvSkillReqGun,
            R.id.tvSkillReqGrenade,   R.id.tvSkillReqMissile
    };
    private final int[] repairIds  = {
            R.id.btnRepairSpaceship, R.id.btnRepairGun,
            R.id.btnRepairGrenade,   R.id.btnRepairMissile
    };
    private final int[] sendIds    = {
            R.id.btnSendSpaceship, R.id.btnSendGun,
            R.id.btnSendGrenade,   R.id.btnSendMissile
    };

    private TextView   tvEngineerInfo;
    private TextView[] tvStatus   = new TextView[4];
    private TextView[] tvSkillReq = new TextView[4];
    private Button[]   btnRepair  = new Button[4];
    private Button[]   btnSend    = new Button[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        tvEngineerInfo = findViewById(R.id.tvEngineerInfo);

        for (int i = 0; i < 4; i++) {
            tvStatus[i]   = findViewById(statusIds[i]);
            tvSkillReq[i] = findViewById(skillReqIds[i]);
            btnRepair[i]  = findViewById(repairIds[i]);
            btnSend[i]    = findViewById(sendIds[i]);

            final int idx = i;
            btnRepair[i].setOnClickListener(v -> attemptRepair(idx));
            btnSend[i].setOnClickListener(v -> sendItem(idx));
        }

        findViewById(R.id.btnBackInventory).setOnClickListener(v -> {
            startActivity(new Intent(this, MissionControlActivity.class));
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUI();
    }

    // ── Helpers ───────────────────────────────────────────────────

    private CrewMember findEngineer() {
        for (CrewMember m : GameData.crewList)
            if ("Engineer".equalsIgnoreCase(m.role)) return m;
        return null;
    }

    private void refreshUI() {
        CrewMember eng   = findEngineer();
        int        skill = (eng != null) ? eng.getSkill() : 0;

        tvEngineerInfo.setText(eng != null
                ? "⚙️ " + eng.name + "  |  Skill: " + skill
                : "⚠️ No Engineer in crew");

        for (int i = 0; i < 4; i++) {
            boolean canRepair = skill >= ITEM_SKILL[i];
            int     state     = GameData.inventoryItemState[i];

            // Skill requirement label — green when met, red when not
            tvSkillReq[i].setText("Required Skill: " + ITEM_SKILL[i]);
            tvSkillReq[i].setTextColor(canRepair ? 0xFF44DD44 : 0xFFFF4444);

            switch (state) {
                case GameData.ITEM_BROKEN:
                    if (canRepair) {
                        tvStatus[i].setText("🔧 Ready to Repair");
                        tvStatus[i].setTextColor(0xFFFF8800);
                    } else {
                        tvStatus[i].setText("⛔ Not enough skill");
                        tvStatus[i].setTextColor(0xFFFF3333);
                    }
                    btnRepair[i].setVisibility(View.VISIBLE);
                    btnRepair[i].setAlpha(canRepair ? 1.0f : 0.45f);
                    btnSend[i].setVisibility(View.GONE);
                    break;

                case GameData.ITEM_REPAIRED:
                    tvStatus[i].setText("✅ Repaired");
                    tvStatus[i].setTextColor(0xFF44DD44);
                    btnRepair[i].setVisibility(View.GONE);
                    btnSend[i].setVisibility(View.VISIBLE);
                    break;

                case GameData.ITEM_SENT:
                    tvStatus[i].setText("📤 Sent to Mission");
                    tvStatus[i].setTextColor(0xFF3399FF);
                    btnRepair[i].setVisibility(View.GONE);
                    btnSend[i].setVisibility(View.GONE);
                    break;
            }
        }
    }

    private void attemptRepair(int idx) {
        CrewMember eng   = findEngineer();
        int        skill = (eng != null) ? eng.getSkill() : 0;
        if (skill < ITEM_SKILL[idx]) {
            Toast.makeText(this, "Not enough skill to fix this weapon", Toast.LENGTH_SHORT).show();
            return;
        }
        GameData.inventoryItemState[idx] = GameData.ITEM_REPAIRED;
        refreshUI();
        Toast.makeText(this, ITEM_EMOJI[idx] + " Weapon repaired!", Toast.LENGTH_SHORT).show();
    }

    private void sendItem(int idx) {
        GameData.inventoryItemState[idx] = GameData.ITEM_SENT;
        refreshUI();
        Toast.makeText(this, ITEM_EMOJI[idx] + " Sent to mission!", Toast.LENGTH_SHORT).show();
    }
}
