package com.example.tridots.main_screens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tridots.R;

public class MainActivity extends AppCompatActivity {

    private Button continueButton;
    private static final String PREF_NAME = "user_session";
    private static final String KEY_LOGGED_IN = "is_logged_in";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PROFILE_PICTURE = "profile_picture_path";

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Check if the user is already logged in
        if (sharedPreferences.getBoolean(KEY_LOGGED_IN, false)) {
            // User is logged in, navigate directly to HomeActivity
            String savedUsername = sharedPreferences.getString(KEY_USERNAME, "");
            String savedProfilePicture = sharedPreferences.getString(KEY_PROFILE_PICTURE, "");
            navigateToHome(savedUsername, savedProfilePicture);
            finish(); // Prevent going back to MainActivity
            return;
        }

        setContentView(R.layout.activity_main);

        // Hide the action bar if it's showing
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize the continue button
        continueButton = findViewById(R.id.continueButton);

        // Set click listener for the continue button
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to LoginMethodActivity
                Intent intent = new Intent(MainActivity.this, LoginMethodActivity.class);
                startActivity(intent);

                // Optional: Add transition animation
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                // Optional: finish this activity if you don't want users to come back to it when pressing back
                finish();
            }
        });
    }

    private void navigateToHome(String username, String profilePicturePath) {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class); // Ensure this is HomeActivity
        intent.putExtra("username", username);
        intent.putExtra("profile_picture_path", profilePicturePath);
        startActivity(intent);
        finish(); // Prevent going back to MainActivity after navigating to Home
    }
}