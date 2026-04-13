
package com.example.spaceproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

// ═══════════════════════════════════════════════════════════════
// MainActivity.java — Home / Start Screen
// Linked to: activity_main.xml
// Only job: show home screen, go to NavigationActivity on START GAME
// ═══════════════════════════════════════════════════════════════

public class MainScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        Button btnStartGame = findViewById(R.id.btnStartGame);
        btnStartGame.setOnClickListener(v ->
                startActivity(new Intent(MainScreenActivity.this, NavigationActivity.class))
        );
    }
}