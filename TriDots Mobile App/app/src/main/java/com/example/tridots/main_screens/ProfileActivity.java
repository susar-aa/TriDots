package com.example.tridots.main_screens;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tridots.EditProfileActivity;
import com.example.tridots.R;
import com.example.tridots.SettingsActivity;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    // UI Components
    private TextView textUsername, textUserType, textEmail, textContact, textNic, textAddress,
            textVerificationStatus, textIsActive, textLastUpdated;
    private CircleImageView profileImage;
    private ImageView btnEdit, btnSettings;
    private MaterialCardView profileHeaderCard, profileInfoCard, accountStatusCard;

    // --- Custom Bottom Navigation Components ---
    private LinearLayout navHome, navDashboard, navMarketplace, navInbox, navProfile;
    private View indicatorHome, indicatorDashboard, indicatorInbox, indicatorProfile;
    private ImageView iconHome, iconDashboard, iconInbox, iconProfile;
    private TextView labelHome, labelDashboard, labelInbox, labelProfile;

    // Data Management
    private SharedPreferences sharedPreferences;
    private RequestQueue requestQueue;
    private String currentUsername;

    // Constants
    private static final String PREF_NAME = "user_session";
    private static final String KEY_USERNAME = "username";
    private static final String GET_PROFILE_URL = "https://lionsgoldencircle.com/Tridots/Api/get_profile.php";
    private static final String BASE_IMAGE_URL = "https://lionsgoldencircle.com/Tridots/";
    private static final long ANIMATION_DURATION = 500;
    private static final long ANIMATION_DELAY = 100;

    // Activity Result Handler
    private ActivityResultLauncher<Intent> editProfileLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeComponents();
        setupUI();
        loadUserProfile();
    }

    private void initializeComponents() {
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        requestQueue = Volley.newRequestQueue(this);
        setupEditProfileLauncher();
        findViews();
    }

    private void findViews() {
        // Cards
        profileHeaderCard = findViewById(R.id.profileHeaderCard);
        profileInfoCard = findViewById(R.id.profileInfoCard);
        accountStatusCard = findViewById(R.id.accountStatusCard);

        // Toolbar items
        btnEdit = findViewById(R.id.btnEdit);
        btnSettings = findViewById(R.id.btnSettings);

        // Profile information views
        profileImage = findViewById(R.id.profileImage);
        textUsername = findViewById(R.id.textUsername);
        textUserType = findViewById(R.id.textUserType);
        textEmail = findViewById(R.id.textEmail);
        textContact = findViewById(R.id.textContact);
        textNic = findViewById(R.id.textNic);
        textAddress = findViewById(R.id.textAddress);
        textVerificationStatus = findViewById(R.id.textVerificationStatus);
        textIsActive = findViewById(R.id.textIsActive);
        textLastUpdated = findViewById(R.id.textLastUpdated);

        // --- Initialize Custom Navigation Bar Views ---
        navHome = findViewById(R.id.navHome);
        navDashboard = findViewById(R.id.navDashboard);
        navMarketplace = findViewById(R.id.navMarketplace);
        navInbox = findViewById(R.id.navInbox);
        navProfile = findViewById(R.id.navProfile);

        indicatorHome = findViewById(R.id.indicatorHome);
        indicatorDashboard = findViewById(R.id.indicatorDashboard);
        indicatorInbox = findViewById(R.id.indicatorInbox);
        indicatorProfile = findViewById(R.id.indicatorProfile);

        iconHome = findViewById(R.id.iconHome);
        iconDashboard = findViewById(R.id.iconDashboard);
        iconInbox = findViewById(R.id.iconInbox);
        iconProfile = findViewById(R.id.iconProfile);

        labelHome = findViewById(R.id.labelHome);
        labelDashboard = findViewById(R.id.labelDashboard);
        labelInbox = findViewById(R.id.labelInbox);
        labelProfile = findViewById(R.id.labelProfile);
    }

    private void setupUI() {
        setupToolbar();
        setupClickListeners();
        resetViewsForAnimation();
        // Set Profile as the default selected tab
        selectTab(4); // 0=Home, 1=Dashboard, 2=Marketplace, 3=Inbox, 4=Profile
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void setupClickListeners() {
        btnEdit.setOnClickListener(this);
        btnSettings.setOnClickListener(this);

        // Navigation listeners
        navHome.setOnClickListener(this);
        navDashboard.setOnClickListener(this);
        navMarketplace.setOnClickListener(this);
        navInbox.setOnClickListener(this);
        navProfile.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent = null;
        int tabIndex = -1;

        // Handle Toolbar buttons
        if (id == R.id.btnEdit) {
            handleEditClick();
            return; // Exit as this is not a navigation action
        } else if (id == R.id.btnSettings) {
            handleSettingsClick();
            return; // Exit as this is not a navigation action
        }

        // Handle Navigation Clicks
        else if (id == R.id.navHome) {
            tabIndex = 0;
            intent = new Intent(this, HomeActivity.class);
        } else if (id == R.id.navDashboard) {
            tabIndex = 1;
            intent = new Intent(this, DashboardActivity.class);
        } else if (id == R.id.navMarketplace) {
            tabIndex = 2;
            intent = new Intent(this, MarketplaceActivity.class);
        } else if (id == R.id.navInbox) {
            tabIndex = 3;
            intent = new Intent(this, InboxActivity.class);
        } else if (id == R.id.navProfile) {
            tabIndex = 4;
            // Already here, do nothing
        }

        if (intent != null) {
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }

        if (tabIndex != -1) {
            animateTabPress(v);
            selectTab(tabIndex);
        }
    }

    // --- Navigation Bar Animation and Selection Logic ---
    private void selectTab(int tabIndex) {
        resetAllTabs();
        switch (tabIndex) {
            case 0: setTabSelected(iconHome, labelHome, indicatorHome); break;
            case 1: setTabSelected(iconDashboard, labelDashboard, indicatorDashboard); break;
            case 2: break;
            case 3: setTabSelected(iconInbox, labelInbox, indicatorInbox); break;
            case 4: setTabSelected(iconProfile, labelProfile, indicatorProfile); break;
        }
    }

    private void resetAllTabs() {
        int unselectedColor = ContextCompat.getColor(this, R.color.text_secondary);
        iconHome.setColorFilter(unselectedColor);
        labelHome.setTextColor(unselectedColor);
        indicatorHome.setVisibility(View.INVISIBLE);

        iconDashboard.setColorFilter(unselectedColor);
        labelDashboard.setTextColor(unselectedColor);
        indicatorDashboard.setVisibility(View.INVISIBLE);

        iconInbox.setColorFilter(unselectedColor);
        labelInbox.setTextColor(unselectedColor);
        indicatorInbox.setVisibility(View.INVISIBLE);

        iconProfile.setColorFilter(unselectedColor);
        labelProfile.setTextColor(unselectedColor);
        indicatorProfile.setVisibility(View.INVISIBLE);
    }

    private void setTabSelected(ImageView icon, TextView label, View indicator) {
        int selectedColor = ContextCompat.getColor(this, R.color.primary_green);
        icon.setColorFilter(selectedColor);
        label.setTextColor(selectedColor);

        indicator.setVisibility(View.VISIBLE);
        indicator.setAlpha(0f);
        indicator.animate().alpha(1f).setDuration(250).start();

        animateIconBounce(icon);
    }

    private void animateTabPress(View tab) {
        tab.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100)
                .withEndAction(() -> tab.animate().scaleX(1f).scaleY(1f).setDuration(100).start())
                .start();
    }

    private void animateIconBounce(ImageView icon) {
        icon.animate().scaleX(1.2f).scaleY(1.2f).setDuration(150)
                .withEndAction(() -> icon.animate().scaleX(1f).scaleY(1f).setDuration(150).setInterpolator(new BounceInterpolator()).start())
                .start();
    }

    // --- Existing Profile Logic ---

    private void setupEditProfileLauncher() {
        editProfileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && currentUsername != null) {
                        fetchUserProfile(currentUsername);
                    }
                });
    }

    private void resetViewsForAnimation() {
        float translationY = 50f;
        profileHeaderCard.setTranslationY(translationY);
        profileInfoCard.setTranslationY(translationY);
        accountStatusCard.setTranslationY(translationY);

        profileHeaderCard.setAlpha(0f);
        profileInfoCard.setAlpha(0f);
        accountStatusCard.setAlpha(0f);
    }

    private void startEntranceAnimations() {
        animateCard(profileHeaderCard, 0);
        animateCard(profileInfoCard, ANIMATION_DELAY);
        animateCard(accountStatusCard, ANIMATION_DELAY * 2);
        animateToolbarItems();
    }

    private void animateCard(View card, long delay) {
        card.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(ANIMATION_DURATION)
                .setStartDelay(delay)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    private void animateToolbarItems() {
        btnEdit.setAlpha(0f);
        btnSettings.setAlpha(0f);

        btnEdit.animate()
                .alpha(1f)
                .setDuration(ANIMATION_DURATION)
                .setStartDelay(ANIMATION_DELAY)
                .start();

        btnSettings.animate()
                .alpha(1f)
                .setDuration(ANIMATION_DURATION)
                .setStartDelay(ANIMATION_DELAY * 2)
                .start();
    }

    private void handleEditClick() {
        if (currentUsername != null && !currentUsername.isEmpty()) {
            Intent intent = new Intent(this, EditProfileActivity.class);
            intent.putExtra("USERNAME_TO_EDIT", currentUsername);
            editProfileLauncher.launch(intent);
        } else {
            showToast("Cannot edit profile. User data not loaded.");
        }
    }

    private void handleSettingsClick() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void loadUserProfile() {
        String loggedInUsername = sharedPreferences.getString(KEY_USERNAME, "");
        if (!loggedInUsername.isEmpty()) {
            currentUsername = loggedInUsername;
            fetchUserProfile(currentUsername);
        } else {
            showToast("Error: Username not found. Please log in again.");
            finish();
        }
    }

    private void fetchUserProfile(final String username) {
        StringRequest request = new StringRequest(Request.Method.POST, GET_PROFILE_URL,
                this::handleProfileResponse,
                error -> handleProfileError(error.getMessage())) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("action", "get_profile");
                return params;
            }
        };
        requestQueue.add(request);
    }

    private void handleProfileResponse(String response) {
        Log.d("Profile Response", response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("success")) {
                JSONObject userData = jsonObject.getJSONObject("user_data");
                currentUsername = userData.getString("username");
                sharedPreferences.edit().putString(KEY_USERNAME, currentUsername).apply();
                populateUserData(userData);
                startEntranceAnimations();
            } else {
                showToast("Failed to fetch profile: " + jsonObject.getString("message"));
            }
        } catch (JSONException e) {
            handleProfileError("Error parsing profile data: " + e.getMessage());
        }
    }

    private void handleProfileError(String errorMessage) {
        Log.e("ProfileFetchError", errorMessage);
        showToast("Error fetching profile: " + errorMessage);
    }

    @SuppressLint("SetTextI18n")
    private void populateUserData(JSONObject userData) throws JSONException {
        textUsername.setText(userData.optString("username", "N/A"));
        textUserType.setText(userData.optString("user_type", "N/A"));
        textEmail.setText(userData.optString("email_address", "N/A"));
        textContact.setText(userData.optString("contact_number", "N/A"));
        textNic.setText(userData.optString("nic_number", "N/A"));
        textAddress.setText(userData.optString("address", "N/A"));
        textVerificationStatus.setText(userData.optString("verification_status", "N/A"));
        textIsActive.setText(userData.optString("is_active", "N/A"));

        updateLastUpdatedTime();
        loadProfileImage(userData.optString("profile_picture", ""));
    }

    private void loadProfileImage(String profilePicturePath) {
        if (profilePicturePath != null && !profilePicturePath.isEmpty() &&
                !profilePicturePath.equals("null") && !profilePicturePath.equals("images/ProfilePictures/")) {

            String fullImageUrl = profilePicturePath.startsWith(BASE_IMAGE_URL) ?
                    profilePicturePath : BASE_IMAGE_URL + profilePicturePath;

            Picasso.get()
                    .load(fullImageUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.profile_placeholder)
                    .into(profileImage);
        } else {
            profileImage.setImageResource(R.drawable.profile_placeholder);
        }
    }

    private void updateLastUpdatedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String currentTime = sdf.format(new Date());
        textLastUpdated.setText("Last updated: " + currentTime);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectTab(4); // Ensure Profile is selected when returning
        if (currentUsername != null) {
            fetchUserProfile(currentUsername);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
