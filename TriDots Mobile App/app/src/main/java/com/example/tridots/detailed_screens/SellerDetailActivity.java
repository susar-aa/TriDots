package com.example.tridots.detailed_screens;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.tridots.R;
import com.example.tridots.SellerStoreActivity;
import com.example.tridots.system.VolleySingleton;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class SellerDetailActivity extends AppCompatActivity {

    private static final String TAG = "SellerDetailActivity";
    private static final long ANIMATION_DURATION = 500;
    private static final long ANIMATION_DELAY = 100;
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String API_BASE_URL = "https://lionsgoldencircle.com/Tridots/Api/";
    private static final TimeZone UTC_TIMEZONE = TimeZone.getTimeZone("UTC");

    // UI Components
    private CircleImageView profileImageView;
    private TextView nameTextView;
    private TextView typeTextView;
    private ImageView typeIcon;
    private MaterialButton viewStoreButton;
    private LinearLayout detailsContainer;
    private MaterialCardView headerCard;
    private TextView lastUpdatedTextView;
    private TextView toolbarTitle;
    private View verificationBadge;
    private ImageView websiteIcon, instagramIcon, facebookIcon;

    // Data
    private int userId;
    private String sellerName;
    private String sellerImageUrl;
    private String websiteUrl = "";
    private String instagramUrl = "";
    private String facebookUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_detail);

        initializeViews();
        setupToolbar();
        setupClickListeners();
        resetViewsForAnimation();

        userId = getIntent().getIntExtra("user_id", -1);
        if (userId != -1) {
            fetchSellerDetails(userId);
        } else {
            handleError("Could not retrieve user ID");
        }
    }

    private void initializeViews() {
        profileImageView = findViewById(R.id.profileImageView);
        nameTextView = findViewById(R.id.nameTextView);
        typeTextView = findViewById(R.id.typeTextView);
        typeIcon = findViewById(R.id.typeIcon);
        viewStoreButton = findViewById(R.id.viewStoreButton);
        detailsContainer = findViewById(R.id.detailsContainer);
        headerCard = findViewById(R.id.headerCard);
        lastUpdatedTextView = findViewById(R.id.lastUpdatedTextView);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        verificationBadge = findViewById(R.id.verificationBadge);
        websiteIcon = findViewById(R.id.websiteIcon);
        instagramIcon = findViewById(R.id.instagramIcon);
        facebookIcon = findViewById(R.id.facebookIcon);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbarTitle.setText(R.string.seller_details_title);
    }

    private void setupClickListeners() {
        viewStoreButton.setOnClickListener(v -> navigateToStore());
        websiteIcon.setOnClickListener(v -> openUrl(websiteUrl));
        instagramIcon.setOnClickListener(v -> openUrl(instagramUrl));
        facebookIcon.setOnClickListener(v -> openUrl(facebookUrl));
    }

    private void resetViewsForAnimation() {
        float translationY = getResources().getDimensionPixelSize(R.dimen.animation_translation);
        headerCard.setAlpha(0f);
        headerCard.setTranslationY(translationY);
        detailsContainer.setAlpha(0f);
        detailsContainer.setTranslationY(translationY);
        viewStoreButton.setAlpha(0f);
        viewStoreButton.setScaleX(0.8f);
        viewStoreButton.setScaleY(0.8f);

        // Reset social icons
        websiteIcon.setVisibility(View.GONE);
        instagramIcon.setVisibility(View.GONE);
        facebookIcon.setVisibility(View.GONE);
        verificationBadge.setVisibility(View.GONE);
    }

    private void fetchSellerDetails(int userId) {
        String url = API_BASE_URL + "fetch_seller_details.php?user_id=" + userId;
        Log.d(TAG, "Fetching seller details: " + url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                this::handleSuccessResponse,
                error -> handleError("Network error: " + error.getMessage()));

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void handleSuccessResponse(@NonNull JSONObject response) {
        try {
            String type = response.getString("type");
            JSONObject details = response.getJSONObject("details");
            updateUI(type, details);
            startAnimations();
            updateLastUpdatedTime();
        } catch (JSONException e) {
            handleError("Error parsing response: " + e.getMessage());
        }
    }

    private void updateUI(String type, JSONObject details) throws JSONException {
        detailsContainer.removeAllViews();

        sellerName = type.equals("business") ?
                details.optString("business_name", "N/A") :
                details.optString("username", "N/A");

        sellerImageUrl = type.equals("business") ?
                details.optString("logo", "") :
                details.optString("profile_picture", "");

        nameTextView.setText(sellerName);
        typeTextView.setText(type.equals("business") ?
                getString(R.string.business_account) :
                getString(R.string.personal_account));
        typeIcon.setImageResource(type.equals("business") ?
                R.drawable.ic_business :
                R.drawable.ic_person);

        // Handle verification status
        String verificationStatus = details.optString("verification_status", "").toLowerCase();
        verificationBadge.setVisibility(verificationStatus.equals("verified") ?
                View.VISIBLE : View.GONE);

        loadImage(profileImageView, sellerImageUrl);

        if (type.equals("business")) {
            updateBusinessUI(details);
        } else {
            updatePersonalUI(details);
        }
    }

    private void updateBusinessUI(JSONObject details) throws JSONException {
        // Update social media URLs and visibility
        websiteUrl = details.optString("website_url", "");
        instagramUrl = details.optString("instagram_link", "");
        facebookUrl = details.optString("facebook_link", "");

        websiteIcon.setVisibility(!websiteUrl.isEmpty() ? View.VISIBLE : View.GONE);
        instagramIcon.setVisibility(!instagramUrl.isEmpty() ? View.VISIBLE : View.GONE);
        facebookIcon.setVisibility(!facebookUrl.isEmpty() ? View.VISIBLE : View.GONE);

        addBusinessFields(details);
    }

    private void updatePersonalUI(JSONObject details) throws JSONException {
        addPersonalFields(details);
    }

    private void addBusinessFields(JSONObject details) throws JSONException {
        addInfoField("Bio", details.optString("bio_description", "N/A"), R.drawable.ic_description);
        addInfoField("WhatsApp", details.optString("whatsapp_number", "N/A"), R.drawable.ic_whatsapp);
        addInfoField("Operating Hours", details.optString("operating_hours_days", "N/A"), R.drawable.ic_time);
        addInfoField("Contact", details.optString("contact_number", "N/A"), R.drawable.ic_phone);
        addInfoField("Email", details.optString("email_address", "N/A"), R.drawable.ic_email);
        addInfoField("Address", details.optString("address", "N/A"), R.drawable.ic_location);
        addInfoField("Rating Count", details.optString("rating_count", "0"), R.drawable.ic_star);
        addInfoField("Status", details.optString("is_active", "N/A"), R.drawable.ic_check_circle);
    }

    private void addPersonalFields(JSONObject details) throws JSONException {
        addInfoField("Contact", details.optString("contact_number", "N/A"), R.drawable.ic_phone);
        addInfoField("Email", details.optString("email_address", "N/A"), R.drawable.ic_email);
        addInfoField("Address", details.optString("address", "N/A"), R.drawable.ic_location);
        addInfoField("Status", details.optString("is_active", "N/A"), R.drawable.ic_check_circle);
    }

    private void addInfoField(String label, String value, int iconResId) {
        View fieldView = LayoutInflater.from(this).inflate(R.layout.layout_info_field, detailsContainer, false);

        ImageView icon = fieldView.findViewById(R.id.fieldIcon);
        TextView labelView = fieldView.findViewById(R.id.fieldLabel);
        TextView valueView = fieldView.findViewById(R.id.fieldValue);

        icon.setImageResource(iconResId);

        // Don't tint social media icons
        if (iconResId != R.drawable.icons8_instagram_48 && iconResId != R.drawable.ic_facebook) {
            icon.setColorFilter(getColor(R.color.primary_green));
        }

        labelView.setText(label);
        valueView.setText(value);

        fieldView.setAlpha(0f);
        fieldView.setTranslationX(-50f);

        detailsContainer.addView(fieldView);
    }

    private void loadImage(ImageView imageView, String url) {
        if (url != null && !url.isEmpty()) {
            Glide.with(this)
                    .load(url)
                    .placeholder(R.drawable.profile_placeholder)
                    .error(R.drawable.profile_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.profile_placeholder);
        }
    }

    private void openUrl(String url) {
        if (url == null || url.isEmpty()) {
            Toast.makeText(this, R.string.no_link_available, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.no_browser_available, Toast.LENGTH_SHORT).show();
        }
    }

    private void startAnimations() {
        // Header animation
        headerCard.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(ANIMATION_DURATION)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        // Details container animation with fields
        detailsContainer.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(ANIMATION_DURATION)
                .setStartDelay(ANIMATION_DELAY)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        // Animate each field
        for (int i = 0; i < detailsContainer.getChildCount(); i++) {
            View field = detailsContainer.getChildAt(i);
            field.animate()
                    .alpha(1f)
                    .translationX(0f)
                    .setDuration(ANIMATION_DURATION)
                    .setStartDelay(ANIMATION_DELAY * (i + 2))
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .start();
        }

        // Button animation
        viewStoreButton.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(ANIMATION_DURATION)
                .setStartDelay(ANIMATION_DELAY * (detailsContainer.getChildCount() + 2))
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    private void navigateToStore() {
        Intent intent = new Intent(this, SellerStoreActivity.class);
        intent.putExtra("user_id", userId);
        intent.putExtra("seller_name", sellerName);
        intent.putExtra("seller_image_url", sellerImageUrl);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void updateLastUpdatedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        sdf.setTimeZone(UTC_TIMEZONE);
        String currentTime = sdf.format(new Date());
        lastUpdatedTextView.setText(getString(R.string.last_updated_format, currentTime));
    }

    private void handleError(String message) {
        Log.e(TAG, "Error: " + message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        if (!isFinishing()) {
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}