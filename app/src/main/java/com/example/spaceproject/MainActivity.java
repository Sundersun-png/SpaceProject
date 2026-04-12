package com.example.spaceproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This links MainActivity to activity_main.xml
        // Whatever is in activity_main.xml will show on screen
        setContentView(R.layout.activity_main);

        // Find the START GAME button from activity_main.xml using its ID
        Button btnStartGame = findViewById(R.id.btnStartGame);

        // When START GAME is clicked, open NavigationActivity
        btnStartGame.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NavigationActivity.class);
            startActivity(intent);
        });
    }
}