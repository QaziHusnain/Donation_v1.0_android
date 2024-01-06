package com.example.donation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Retrieve the username passed from the login activity
        String username = getIntent().getStringExtra("username");

        // Display a welcome message with the username
        TextView welcomeTextView = findViewById(R.id.textViewWelcome);
        welcomeTextView.setText("Welcome, " + username + "!");

        // Initialize buttons
        Button buttonPersonal = findViewById(R.id.buttonPersonal);
        Button buttonHome = findViewById(R.id.buttonHome);
        Button buttonShop = findViewById(R.id.buttonShop);

        // Set click listeners for buttons
        buttonPersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start PersonalActivity when the Personal button is clicked
                Intent intent = new Intent(WelcomeActivity.this, personal_activity.class);
                startActivity(intent);
            }
        });

        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start HomeActivity when the Home button is clicked
                Intent intent = new Intent(WelcomeActivity.this, home_activity.class);
                startActivity(intent);
            }
        });

        buttonShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start ShopActivity when the Shop button is clicked
                Intent intent = new Intent(WelcomeActivity.this, shop_activity.class);
                startActivity(intent);
            }
        });
    }
}

