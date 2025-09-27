package com.example.tridots.main_screens;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tridots.ManageAdsActivity;
import com.example.tridots.Marketplace.ManageMarketplaceActivity;
import com.example.tridots.Marketplace.MyOrdersActivity;
import com.example.tridots.ProductsSoldActivity;
import com.example.tridots.R;
import com.example.tridots.adapters.DashboardInquiryAdapter;
import com.example.tridots.models.Inquiry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    // --- UI Components ---
    private LinearLayout myAdsButton, inquiriesButton, myOrdersButton, manageMarketplaceButton, productsSoldButton; // Added productsSoldButton
    private RecyclerView recyclerViewToReview;

    // --- Custom Bottom Navigation Components ---
    private LinearLayout navHome, navDashboard, navMarketplace, navInbox, navProfile;
    private View indicatorHome, indicatorDashboard, indicatorInbox, indicatorProfile;
    private ImageView iconHome, iconDashboard, iconInbox, iconProfile;
    private TextView labelHome, labelDashboard, labelInbox, labelProfile;

    // --- Data & Networking ---
    private DashboardInquiryAdapter toReviewAdapter;
    private List<Inquiry> toReviewInquiriesList = new ArrayList<>();
    private RequestQueue requestQueue;
    private SharedPreferences sharedPreferences;
    private int userId;

    private static final String GET_INQUIRIES_URL = "https://lionsgoldencircle.com/Tridots/Api/get_inquiries.php";
    private static final String GET_RENTING_AD_DETAILS_URL = "https://lionsgoldencircle.com/Tridots/Api/get_renting_ad_details.php";
    private static final String GET_VEHICLE_AD_DETAILS_URL = "https://lionsgoldencircle.com/Tridots/Api/get_vehicle_ad_details.php";
    private static final String GET_SERVICE_PROVIDER_DETAILS_URL = "https://lionsgoldencircle.com/Tridots/Api/get_service_provider_details.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "User ID not found. Please log in again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        initializeViews();
        setupRecyclerViews();
        setupListeners();

        requestQueue = Volley.newRequestQueue(this);
        fetchToReviewInquiries();
    }

    private void initializeViews() {
        myAdsButton = findViewById(R.id.my_ads_button);
        inquiriesButton = findViewById(R.id.inquiries_button);
        myOrdersButton = findViewById(R.id.my_orders_button);
        manageMarketplaceButton = findViewById(R.id.manage_marketplace_button);
        productsSoldButton = findViewById(R.id.products_sold_button); // Initialize the new button
        recyclerViewToReview = findViewById(R.id.recyclerViewToReview);

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

        // Set Dashboard as the default selected tab
        selectTab(1); // 0=Home, 1=Dashboard
    }

    private void setupRecyclerViews() {
        recyclerViewToReview.setLayoutManager(new LinearLayoutManager(this));
        toReviewAdapter = new DashboardInquiryAdapter(toReviewInquiriesList);
        recyclerViewToReview.setAdapter(toReviewAdapter);
    }

    private void setupListeners() {
        myAdsButton.setOnClickListener(this);
        inquiriesButton.setOnClickListener(this);
        myOrdersButton.setOnClickListener(this);
        manageMarketplaceButton.setOnClickListener(this);
        productsSoldButton.setOnClickListener(this); // Set listener for the new button

        navHome.setOnClickListener(this);
        navDashboard.setOnClickListener(this);
        navMarketplace.setOnClickListener(this);
        navInbox.setOnClickListener(this);
        navProfile.setOnClickListener(this);

        toReviewAdapter.setOnFeedbackClickListener((position, inquiry) -> showFeedbackDialog(inquiry));
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        int id = v.getId();
        int tabIndex = -1;

        // Handle Dashboard Buttons
        if (id == R.id.my_ads_button) {
            intent = new Intent(this, ManageAdsActivity.class);
        } else if (id == R.id.inquiries_button) {
            intent = new Intent(this, InquiriesActivity.class);
        } else if (id == R.id.my_orders_button) {
            intent = new Intent(this, MyOrdersActivity.class);
        } else if (id == R.id.manage_marketplace_button) {
            intent = new Intent(this, ManageMarketplaceActivity.class);
        } else if (id == R.id.products_sold_button) { // Handle the new button click
            intent = new Intent(this, ProductsSoldActivity.class); // Navigate to ProductsSoldActivity
        }

        // Handle Navigation Clicks
        else if (id == R.id.navHome) {
            tabIndex = 0;
            intent = new Intent(this, HomeActivity.class);
        } else if (id == R.id.navDashboard) {
            tabIndex = 1;
            // Already here, do nothing
        } else if (id == R.id.navMarketplace) {
            tabIndex = 2;
            intent = new Intent(this, MarketplaceActivity.class);
        } else if (id == R.id.navInbox) {
            tabIndex = 3;
            intent = new Intent(this, InboxActivity.class);
        } else if (id == R.id.navProfile) {
            tabIndex = 4;
            intent = new Intent(this, ProfileActivity.class);
        }

        // Navigate if an intent was created
        if (intent != null) {
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }

        // Update tab selection visuals
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
            case 2: break; // Marketplace has no selection state change
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

    // --- Data Fetching and Dialog Logic ---

    private void fetchToReviewInquiries() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_INQUIRIES_URL,
                response -> {
                    Log.d("Fetch To Review", "Response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.has("error")) {
                            Toast.makeText(DashboardActivity.this, "Error fetching inquiries: " + jsonResponse.getString("error"), Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (jsonResponse.has("To Review")) {
                            JSONArray toReviewArray = jsonResponse.getJSONArray("To Review");
                            parseToReviewInquiries(toReviewArray);
                        }
                    } catch (JSONException e) {
                        Log.e("Fetch To Review", "JSON Parsing Error: " + e.getMessage());
                        Toast.makeText(DashboardActivity.this, "Error parsing inquiry data", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Log.e("Fetch To Review", "Volley Error: " + error.getMessage());
                    Toast.makeText(DashboardActivity.this, "Failed to fetch inquiries", Toast.LENGTH_LONG).show();
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void parseToReviewInquiries(JSONArray jsonArray) throws JSONException {
        toReviewInquiriesList.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject inquiryObject = jsonArray.getJSONObject(i);
            Inquiry inquiry = new Inquiry();
            inquiry.setInquiryId(inquiryObject.getInt("inquiry_id"));
            inquiry.setAdType(inquiryObject.getString("ad_type"));
            inquiry.setAdId(inquiryObject.getInt("ad_id"));
            inquiry.setInquirerName(inquiryObject.optString("inquirer_name"));
            toReviewInquiriesList.add(inquiry);
            fetchAdDetails(inquiry);
        }
        toReviewAdapter.notifyDataSetChanged();
    }

    private void fetchAdDetails(final Inquiry inquiry) {
        String url;
        StringRequest stringRequest = null;

        if ("renting".equals(inquiry.getAdType())) {
            url = GET_RENTING_AD_DETAILS_URL;
            stringRequest = new StringRequest(Request.Method.POST, url,
                    response -> {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (!jsonResponse.has("error")) {
                                inquiry.setAdName(jsonResponse.getString("product_name"));
                                inquiry.setAdImageUrl(jsonResponse.optString("product_images").split(",")[0]);
                                toReviewAdapter.updateInquiry(inquiry.getInquiryId(), inquiry.getAdName(), inquiry.getAdImageUrl());
                            }
                        } catch (JSONException e) {
                            Log.e("Fetch Renting Ad", "JSON Parsing Error: " + e.getMessage());
                        }
                    },
                    error -> Log.e("Fetch Renting Ad", "Volley Error: " + error.getMessage())) {
                @Nullable
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("rent_id", String.valueOf(inquiry.getAdId()));
                    return params;
                }
            };
        } else if ("vehicles".equals(inquiry.getAdType())) {
            url = GET_VEHICLE_AD_DETAILS_URL;
            stringRequest = new StringRequest(Request.Method.POST, url,
                    response -> {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (!jsonResponse.has("error")) {
                                inquiry.setAdName(jsonResponse.getString("vehicle_name"));
                                inquiry.setAdImageUrl(jsonResponse.optString("vehicle_images").split(",")[0]);
                                toReviewAdapter.updateInquiry(inquiry.getInquiryId(), inquiry.getAdName(), inquiry.getAdImageUrl());
                            }
                        } catch (JSONException e) {
                            Log.e("Fetch Vehicle Ad", "JSON Parsing Error: " + e.getMessage());
                        }
                    },
                    error -> Log.e("Fetch Vehicle Ad", "Volley Error: " + error.getMessage())) {
                @Nullable
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("vehicle_id", String.valueOf(inquiry.getAdId()));
                    return params;
                }
            };
        } else if ("service_providers".equals(inquiry.getAdType())) {
            url = GET_SERVICE_PROVIDER_DETAILS_URL;
            stringRequest = new StringRequest(Request.Method.POST, url,
                    response -> {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (!jsonResponse.has("error")) {
                                inquiry.setAdName(jsonResponse.getString("name"));
                                inquiry.setAdImageUrl(jsonResponse.optString("profile_picture"));
                                toReviewAdapter.updateInquiry(inquiry.getInquiryId(), inquiry.getAdName(), inquiry.getAdImageUrl());
                            }
                        } catch (JSONException e) {
                            Log.e("Fetch Service Ad", "JSON Parsing Error: " + e.getMessage());
                        }
                    },
                    error -> Log.e("Fetch Service Ad", "Volley Error: " + error.getMessage())) {
                @Nullable
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("seller_id", String.valueOf(inquiry.getAdId()));
                    return params;
                }
            };
        }

        if (stringRequest != null) {
            requestQueue.add(stringRequest);
        }
    }

    private void showFeedbackDialog(Inquiry inquiry) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_feedback, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Send Feedback");
        builder.setView(dialogView);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            // This is overridden below to prevent the dialog from closing on validation failure.
        });
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            RatingBar ratingBar = dialogView.findViewById(R.id.ratingBarFeedback);
            EditText editTextComment = dialogView.findViewById(R.id.commentEditTextFeedback);

            float rating = ratingBar.getRating();
            String comment = editTextComment.getText().toString().trim();

            if (rating == 0) {
                Toast.makeText(this, "Please provide a rating", Toast.LENGTH_SHORT).show();
                return; // Keep the dialog open
            }

            sendFeedback(inquiry, rating, comment, dialog);
        });
    }

    private void sendFeedback(Inquiry inquiry, float rating, String comment, AlertDialog dialog) {
        String url = "https://lionsgoldencircle.com/Tridots/Api/submit_feedback.php";
        final String feedbackType;
        if ("renting".equals(inquiry.getAdType())) {
            feedbackType = "renting";
        } else if ("vehicles".equals(inquiry.getAdType())) {
            feedbackType = "vehicles";
        } else if ("service_providers".equals(inquiry.getAdType())) {
            feedbackType = "service_provider";
        } else {
            feedbackType = "";
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (!jsonObject.has("error")) {
                            Toast.makeText(this, "Feedback submitted!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            fetchToReviewInquiries(); // Refresh the list
                        } else {
                            Toast.makeText(this, "Failed: " + jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                params.put("feedback_type", feedbackType);
                params.put("item_id", String.valueOf(inquiry.getAdId()));
                params.put("rating", String.valueOf(rating));
                params.put("comment", comment);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}