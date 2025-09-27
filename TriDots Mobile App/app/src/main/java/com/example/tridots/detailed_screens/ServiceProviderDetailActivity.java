package com.example.tridots.detailed_screens;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.tridots.ChatActivity;
import com.example.tridots.InquiryActivity;
import com.example.tridots.R;
import com.example.tridots.adapters.FeedbackAdapter;
import com.example.tridots.models.Feedback;
import com.example.tridots.system.VolleySingleton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ServiceProviderDetailActivity extends AppCompatActivity {

    private static final String TAG = "ServiceProviderDetail";
    private static final String PREF_NAME = "user_session";
    private static final String KEY_USER_ID = "user_id";

    // Data
    private int serviceProviderId;
    private int listerId;
    private String listerName;
    private String serviceProviderName;
    private int currentUserId;

    // UI Components
    private CircleImageView detailProfileImageView;
    private TextView detailNameTextView, detailCategoryTextView, detailLocationTextView,
            detailContactTextView, detailEmailTextView, detailExperienceTextView,
            detailQualificationsTextView, detailDescriptionContentTextView,
            detailReviewsTextView, detailVerificationStatusTextView, listedByTextView, toolbarTitle;
    private Button inquireButton;
    private FloatingActionButton sendMessageFab;
    private RecyclerView feedbacksRecyclerView;
    private MaterialCardView headerCard, detailsCard, contactCard, feedbackCard;

    // Adapters
    private FeedbackAdapter feedbackAdapter;
    private List<Feedback> feedbackList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_detail);

        serviceProviderId = getIntent().getIntExtra("service_provider_id", -1);

        initializeViews();
        setupToolbar();
        resetViewsForAnimation();

        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt(KEY_USER_ID, -1);

        if (serviceProviderId != -1) {
            fetchServiceProviderDetails();
            fetchFeedbacks();
        } else {
            Toast.makeText(this, "Error: Service Provider ID not found.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initializeViews() {
        // Cards
        headerCard = findViewById(R.id.headerCard);
        detailsCard = findViewById(R.id.detailsCard);
        contactCard = findViewById(R.id.contactCard);
        feedbackCard = findViewById(R.id.feedbackCard);

        // Header Card content
        detailProfileImageView = findViewById(R.id.detailProfileImageView);
        detailNameTextView = findViewById(R.id.detailNameTextView);
        listedByTextView = findViewById(R.id.listedByTextView);
        detailCategoryTextView = findViewById(R.id.detailCategoryTextView);
        detailVerificationStatusTextView = findViewById(R.id.detailVerificationStatusTextView);
        toolbarTitle = findViewById(R.id.toolbarTitle);

        // Details Card content
        detailLocationTextView = findViewById(R.id.detailLocationTextView);
        detailExperienceTextView = findViewById(R.id.detailExperienceTextView);
        detailQualificationsTextView = findViewById(R.id.detailQualificationsTextView);
        detailDescriptionContentTextView = findViewById(R.id.detailDescriptionContentTextView);

        // Contact Card content
        detailContactTextView = findViewById(R.id.detailContactTextView);
        detailEmailTextView = findViewById(R.id.detailEmailTextView);
        inquireButton = findViewById(R.id.inquireButton);

        // Feedback Card content
        detailReviewsTextView = findViewById(R.id.detailReviewsTextView);
        feedbacksRecyclerView = findViewById(R.id.feedbacksRecyclerView);

        // Floating Action Button
        sendMessageFab = findViewById(R.id.sendMessageFab);

        // Setup RecyclerView
        feedbackAdapter = new FeedbackAdapter(feedbackList);
        feedbacksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        feedbacksRecyclerView.setAdapter(feedbackAdapter);
        feedbacksRecyclerView.setNestedScrollingEnabled(false);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void fetchServiceProviderDetails() {
        String url = "https://lionsgoldencircle.com/Tridots/Api/fetch_service_provider_details.php?id=" + serviceProviderId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        populateUi(response);
                        setupClickListeners(); // Setup listeners only after data is loaded
                        startAnimations();
                    } catch (JSONException e) {
                        handleError("Failed to parse service provider data: " + e.getMessage());
                    }
                },
                error -> handleError("Network error: " + error.getMessage())
        );
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    @SuppressLint("SetTextI18n")
    private void populateUi(JSONObject data) throws JSONException {
        serviceProviderId = data.getInt("id");
        listerId = data.getInt("user_id");
        listerName = data.getString("lister_name");
        serviceProviderName = data.getString("name");

        detailNameTextView.setText(serviceProviderName);
        toolbarTitle.setText(serviceProviderName);
        listedByTextView.setText("Listed by: " + listerName);
        detailCategoryTextView.setText(data.getString("service_category_name"));
        detailLocationTextView.setText(data.optString("location", "N/A"));
        detailContactTextView.setText(data.optString("contact_number", "N/A"));
        detailEmailTextView.setText(data.optString("email_address", "N/A"));
        detailExperienceTextView.setText(data.optInt("experience_years", 0) + " Years");
        detailQualificationsTextView.setText(data.optString("qualifications", "N/A"));
        detailDescriptionContentTextView.setText(data.optString("description", "No description provided."));

        String verificationStatus = data.getString("verification_status");
        if ("Verified".equalsIgnoreCase(verificationStatus)) {
            detailVerificationStatusTextView.setVisibility(View.VISIBLE);
            detailVerificationStatusTextView.setText(verificationStatus);
        } else {
            detailVerificationStatusTextView.setVisibility(View.GONE);
        }

        int reviewCount = data.getInt("review_count");
        detailReviewsTextView.setText("Reviews (" + reviewCount + ")");

        String imageUrl = data.optString("profile_picture");
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.profile_placeholder)
                .error(R.drawable.profile_error_placeholders)
                .into(detailProfileImageView);
    }

    private void fetchFeedbacks() {
        String url = "https://lionsgoldencircle.com/Tridots/Api/fetch_feedbacks.php?feedback_type=service_provider&item_id=" + serviceProviderId;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    feedbackList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            feedbackList.add(new Feedback(
                                    obj.getInt("feedback_id"),
                                    obj.getInt("user_id"),
                                    obj.optString("user_name", "Anonymous"),
                                    obj.optInt("rating", 0),
                                    obj.optString("comment", ""),
                                    obj.optString("created_at", "")
                            ));
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing feedback JSON: " + e.getMessage());
                        }
                    }
                    feedbackAdapter.notifyDataSetChanged();
                },
                error -> {
                    Log.e(TAG, "Could not fetch feedbacks: " + error.getMessage());
                }
        );
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void setupClickListeners() {
        inquireButton.setOnClickListener(v -> {
            // ADDED: Self-inquiry block
            if (currentUserId == listerId) {
                Toast.makeText(this, "You cannot inquire own services.", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, InquiryActivity.class);
            intent.putExtra("ad_type", "service_providers");
            intent.putExtra("ad_id", serviceProviderId);
            intent.putExtra("product_name", serviceProviderName);
            intent.putExtra("lister_id", listerId);
            startActivity(intent);
        });

        listedByTextView.setOnClickListener(v -> {
            Intent intent = new Intent(this, SellerDetailActivity.class);
            intent.putExtra("user_id", listerId);
            startActivity(intent);
        });

        sendMessageFab.setOnClickListener(v -> {
            if (currentUserId == listerId) {
                Toast.makeText(this, "You cannot message yourself.", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("receiver_id", listerId);
            intent.putExtra("receiver_username", listerName);
            startActivity(intent);
        });
    }

    private void resetViewsForAnimation() {
        headerCard.setAlpha(0f);
        detailsCard.setAlpha(0f);
        contactCard.setAlpha(0f);
        feedbackCard.setAlpha(0f);

        float translationY = 100f;
        headerCard.setTranslationY(translationY);
        detailsCard.setTranslationY(translationY);
        contactCard.setTranslationY(translationY);
        feedbackCard.setTranslationY(translationY);
    }

    private void startAnimations() {
        animateCard(headerCard, 0);
        animateCard(detailsCard, 100);
        animateCard(contactCard, 200);
        animateCard(feedbackCard, 300);
    }

    private void animateCard(View card, long delay) {
        card.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(600)
                .setStartDelay(delay)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    private void handleError(String message) {
        Log.e(TAG, "An error occurred: " + message);
        Toast.makeText(this, "Error: " + message, Toast.LENGTH_LONG).show();
    }
}
