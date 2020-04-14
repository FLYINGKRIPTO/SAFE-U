package com.womensafety.shajt3ch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class StartScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start_screen);

        Button button = findViewById(R.id.navigate_to_main);
        button.setOnClickListener(v -> {
            Intent mainActivityIntet = new Intent(this, MainActivity.class);
            startActivity(mainActivityIntet);
        });

    }
}
