package com.example.tridots;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.tridots.main_screens.LoginMethodActivity;
import com.google.android.material.card.MaterialCardView;

public class SettingsActivity extends AppCompatActivity {

    private MaterialCardView logoutCard;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "user_session";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize SharedPreferences to manage the user session
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Setup the Toolbar with a back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Find the logout card and set a click listener
        logoutCard = findViewById(R.id.logoutCard);
        logoutCard.setOnClickListener(v -> showLogoutConfirmationDialog());
    }

    /**
     * Displays an AlertDialog to confirm if the user wants to log out.
     */
    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Log Out", (dialog, which) -> performLogout()) // Lambda for positive button
                .setNegativeButton("Cancel", null) // Does nothing on click, just dismisses
                .show();
    }

    /**
     * Clears the user session data and navigates to the LoginActivity.
     */
    private void performLogout() {
        // Clear all data from the SharedPreferences session
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Create an intent to go to the LoginActivity
        Intent intent = new Intent(SettingsActivity.this, LoginMethodActivity.class);

        // Set flags to clear the activity stack, so the user can't press "back" to return
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        // Finish the current activity
        finish();
    }

    /**
     * Handles clicks on the toolbar items, specifically the back arrow.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle the toolbar's back arrow click
        if (item.getItemId() == android.R.id.home) {
            finish(); // Close this activity and return to the previous one (ProfileActivity)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Overrides the default back press to include a transition animation.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
