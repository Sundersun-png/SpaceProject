package com.example.spaceproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class InventoryActivity extends AppCompatActivity {

    // Panel IDs in layout order: 0=Spaceship, 1=Gun, 2=Grenade, 3=Missile
    private final int[] statusIds = {
            R.id.tvStatusSpaceship, R.id.tvStatusGun,
            R.id.tvStatusGrenade,   R.id.tvStatusMissile
    };
    private final int[] repairIds = {
            R.id.btnRepairSpaceship, R.id.btnRepairGun,
            R.id.btnRepairGrenade,   R.id.btnRepairMissile
    };
    private final int[] sendIds = {
            R.id.btnSendSpaceship, R.id.btnSendGun,
            R.id.btnSendGrenade,   R.id.btnSendMissile
    };

    private TextView[] tvStatus = new TextView[4];
    private Button[]   btnRepair = new Button[4];
    private Button[]   btnSend   = new Button[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        for (int i = 0; i < 4; i++) {
            tvStatus[i]  = findViewById(statusIds[i]);
            btnRepair[i] = findViewById(repairIds[i]);
            btnSend[i]   = findViewById(sendIds[i]);

            final int idx = i;
            btnRepair[i].setOnClickListener(v -> launchRepair(idx));
            btnSend[i].setOnClickListener(v  -> sendItem(idx));
        }

        findViewById(R.id.btnBackInventory).setOnClickListener(v -> {
            startActivity(new Intent(this, MissionControlActivity.class));
            finish();
        });

        refreshUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUI();
    }

    private void refreshUI() {
        for (int i = 0; i < 4; i++) {
            int state = GameData.inventoryItemState[i];
            switch (state) {
                case GameData.ITEM_BROKEN:
                    tvStatus[i].setText("🔧 Needs Repair");
                    tvStatus[i].setTextColor(0xFFFF6600);
                    btnRepair[i].setVisibility(View.VISIBLE);
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

    private void launchRepair(int idx) {
        GameData.pendingRepairIndex = idx;
        Intent intent = new Intent(this, EngineerMissionActivity.class);
        intent.putExtra("fromInventory", true);
        startActivity(intent);
        finish();
    }

    private void sendItem(int idx) {
        GameData.inventoryItemState[idx] = GameData.ITEM_SENT;
        refreshUI();
    }
}
