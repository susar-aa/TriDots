package com.example.tridots;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import androidx.annotation.Nullable;

public class ProfileDisplayActivity extends AppCompatActivity {

    private ImageView imageViewProfile;
    private TextView textViewUsername;
    private Button buttonContinue;
    private ProgressBar progressBar;
    private String username;
    private String profilePicturePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_display);

        imageViewProfile = findViewById(R.id.imageViewProfile);
        textViewUsername = findViewById(R.id.textViewUsername);
        buttonContinue = findViewById(R.id.buttonContinue);
        progressBar = findViewById(R.id.progressBar); // Initialize ProgressBar

        // Get user data from the intent
        username = getIntent().getStringExtra("username");
        profilePicturePath = getIntent().getStringExtra("profile_picture_path");
        Log.d("ProfilePicturePath", "Received path: " + profilePicturePath);

        // Display the username
        textViewUsername.setText("Welcome, " + username);

        // Load the profile picture using Glide with a listener
        if (profilePicturePath != null && !profilePicturePath.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE); // Show ProgressBar before loading
            Glide.with(this)
                    .load("https://lionsgoldencircle.com/Tridots/images/ProfilePictures/" + profilePicturePath)
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // Optional: Caching strategy
                    .circleCrop()
                    .placeholder(R.drawable.profile_placeholder)
                    .error(R.drawable.profile_placeholder)
                    .listener(new RequestListener<android.graphics.drawable.Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE); // Hide ProgressBar on failure
                            Log.e("Glide", "Image load failed", e);
                            return false; // Allow Glide's error handling to show the error placeholder
                        }

                        @Override
                        public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, Target<android.graphics.drawable.Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE); // Hide ProgressBar on success
                            return false; // Allow Glide to set the resource in the ImageView
                        }
                    })
                    .into(imageViewProfile);
        } else {
            // If no profile picture path, show a default placeholder without ProgressBar
            Glide.with(this)
                    .load(R.drawable.profile_placeholder)
                    .circleCrop()
                    .into(imageViewProfile);
        }

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileDisplayActivity.this, TutorialActivity.class);
                startActivity(intent);
                finish(); // Optional: Close this activity
            }
        });
    }
}