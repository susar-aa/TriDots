package com.example.tridots.detailed_screens;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.example.tridots.ChatActivity;
import com.example.tridots.InquiryActivity;
import com.example.tridots.R;
import com.example.tridots.detailed_screens.SellerDetailActivity;
import com.example.tridots.adapters.FeedbackAdapter;
import com.example.tridots.models.Feedback;
import com.example.tridots.system.VolleySingleton;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VehicleDetailActivity extends AppCompatActivity {

    // --- UI Components ---
    private ImageView detailImageView;
    private TextView detailNameTextView, detailBrandModelTextView, detailPriceTextView, detailLocationTextView,
            detailCapacityTextView, detailFuelTypeTextView, detailTransmissionTextView, detailDescriptionTextView;
    private ProgressBar progressBar;
    private Button inquireButton, sendMessageButton;
    private MaterialButton sellerInfoButton, previewImageButton;
    private RecyclerView feedbacksRecyclerView; // FIXED: Added missing declaration

    // --- Data & Networking ---
    private FeedbackAdapter feedbackAdapter;
    private List<Feedback> feedbackList = new ArrayList<>();
    private int listerId;
    private String listerName;
    private int currentUserId;
    private String firstImageUrl;

    private static final String PREF_NAME = "user_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String TAG = "VehicleDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_detail);

        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt(KEY_USER_ID, -1);

        final int vehicleId = getIntent().getIntExtra("vehicle_id", -1);

        initializeViews();
        setupRecyclerViews();
        setupListeners(vehicleId);

        if (vehicleId != -1) {
            fetchVehicleDetails(vehicleId);
            fetchFeedbacks(vehicleId);
        } else {
            Toast.makeText(this, "Error: Vehicle ID not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }

        detailImageView = findViewById(R.id.detailImageView);
        detailNameTextView = findViewById(R.id.detailNameTextView);
        detailBrandModelTextView = findViewById(R.id.detailBrandModelTextView);
        detailPriceTextView = findViewById(R.id.detailPriceTextView);
        detailLocationTextView = findViewById(R.id.detailLocationTextView);
        detailCapacityTextView = findViewById(R.id.detailCapacityTextView);
        detailFuelTypeTextView = findViewById(R.id.detailFuelTypeTextView);
        detailTransmissionTextView = findViewById(R.id.detailTransmissionTextView);
        detailDescriptionTextView = findViewById(R.id.detailDescriptionTextView);
        progressBar = findViewById(R.id.progressBar);
        inquireButton = findViewById(R.id.inquireButton);
        sendMessageButton = findViewById(R.id.sendMessageButton);
        feedbacksRecyclerView = findViewById(R.id.feedbacksRecyclerView);
        sellerInfoButton = findViewById(R.id.sellerInfoButton);
        previewImageButton = findViewById(R.id.previewImageButton);
    }

    private void setupRecyclerViews() {
        feedbackAdapter = new FeedbackAdapter(feedbackList);
        feedbacksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        feedbacksRecyclerView.setAdapter(feedbackAdapter);
        feedbacksRecyclerView.setNestedScrollingEnabled(false);
    }

    private void setupListeners(int vehicleId) {
        inquireButton.setOnClickListener(v -> {
            if (currentUserId == listerId) {
                Toast.makeText(this, "You cannot inquire about your own ad.", Toast.LENGTH_SHORT).show();
            } else if (listerId != -1) {
                Intent inquiryIntent = new Intent(VehicleDetailActivity.this, InquiryActivity.class);
                inquiryIntent.putExtra("ad_type", "vehicles");
                inquiryIntent.putExtra("ad_id", vehicleId);
                inquiryIntent.putExtra("product_name", detailNameTextView.getText().toString());
                inquiryIntent.putExtra("lister_id", listerId);
                startActivity(inquiryIntent);
            } else {
                Toast.makeText(VehicleDetailActivity.this, "Cannot inquire, lister details not available.", Toast.LENGTH_SHORT).show();
            }
        });

        sellerInfoButton.setOnClickListener(v -> {
            if (listerId != -1) {
                Intent sellerIntent = new Intent(VehicleDetailActivity.this, SellerDetailActivity.class);
                sellerIntent.putExtra("user_id", listerId);
                startActivity(sellerIntent);
            } else {
                Toast.makeText(VehicleDetailActivity.this, "Seller details not available.", Toast.LENGTH_SHORT).show();
            }
        });

        sendMessageButton.setOnClickListener(v -> {
            if (currentUserId != -1 && listerId != -1 && listerName != null && !listerName.isEmpty()) {
                if (currentUserId == listerId) {
                    Toast.makeText(VehicleDetailActivity.this, "You cannot send messages to yourself.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent chatIntent = new Intent(VehicleDetailActivity.this, ChatActivity.class);
                    chatIntent.putExtra("receiver_id", listerId);
                    chatIntent.putExtra("receiver_username", listerName);
                    startActivity(chatIntent);
                }
            } else {
                Toast.makeText(VehicleDetailActivity.this, "Cannot send message, lister details not available.", Toast.LENGTH_LONG).show();
            }
        });

        previewImageButton.setOnClickListener(v -> {
            if (firstImageUrl != null && !firstImageUrl.isEmpty()) {
                showImagePreviewDialog(firstImageUrl);
            } else {
                Toast.makeText(this, "Image not available for preview.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchVehicleDetails(int vehicleId) {
        progressBar.setVisibility(View.VISIBLE);
        String url = "https://lionsgoldencircle.com/Tridots/Api/fetch_vehicle_details.php?vehicle_id=" + vehicleId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        detailNameTextView.setText(response.getString("vehicle_name"));
                        detailBrandModelTextView.setText(response.getString("brand") + " " + response.getString("model"));
                        detailPriceTextView.setText(formatPrice(response.getString("price_type"), response.getDouble("amount")));
                        detailLocationTextView.setText(response.getString("location"));
                        detailCapacityTextView.setText(response.getString("capacity"));
                        detailFuelTypeTextView.setText(response.getString("fuel_type"));
                        detailTransmissionTextView.setText(response.getString("transmission_type"));
                        detailDescriptionTextView.setText(response.getString("description"));

                        listerName = response.getString("lister_name");
                        listerId = response.getInt("user_id");
                        sellerInfoButton.setText(listerName);

                        loadFirstImage(response.getString("vehicle_images"));

                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing vehicle details JSON", e);
                        Toast.makeText(VehicleDetailActivity.this, "Error parsing vehicle details", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Error fetching vehicle details", error);
                    Toast.makeText(VehicleDetailActivity.this, "Error fetching vehicle details", Toast.LENGTH_LONG).show();
                });

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private String formatPrice(String priceType, double amount) {
        if ("per_hour".equals(priceType)) {
            return String.format(Locale.US, "Rs. %,.0f / Hour", amount);
        } else if ("per_day".equals(priceType)) {
            return String.format(Locale.US, "Rs. %,.0f / Day", amount);
        } else if ("per_km".equals(priceType)) {
            return String.format(Locale.US, "Rs. %,.0f / KM", amount);
        } else {
            return String.format(Locale.US, "Rs. %,.2f", amount);
        }
    }

    private void loadFirstImage(String vehicleImages) {
        String[] images = vehicleImages.split(",");
        if (images.length > 0 && !images[0].isEmpty()) {
            firstImageUrl = images[0].trim();
            Glide.with(this)
                    .load(firstImageUrl)
                    .placeholder(R.drawable.ad_placeholder)
                    .error(R.drawable.image_error_placeholder)
                    .into(detailImageView);
        } else {
            detailImageView.setImageResource(R.drawable.ad_placeholder);
        }
    }

    private void fetchFeedbacks(int vehicleId) {
        String url = "https://lionsgoldencircle.com/Tridots/Api/fetch_feedbacks.php?feedback_type=vehicles&item_id=" + vehicleId;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    feedbackList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            feedbackList.add(new Feedback(
                                    obj.getInt("feedback_id"),
                                    obj.getInt("user_id"),
                                    obj.optString("user_name", "User"),
                                    obj.optInt("rating", 0),
                                    obj.optString("comment", ""),
                                    obj.optString("created_at", "")
                            ));
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing feedback JSON", e);
                        }
                    }
                    feedbackAdapter.notifyDataSetChanged();
                },
                error -> {
                    Log.e(TAG, "Error fetching feedbacks", error);
                }
        );
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void showImagePreviewDialog(String imageUrl) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_image_preview);

        PhotoView photoView = dialog.findViewById(R.id.photo_view);
        ImageView closeButton = dialog.findViewById(R.id.closeButton);

        Glide.with(this)
                .load(imageUrl)
                .into(photoView);

        closeButton.setOnClickListener(v -> dialog.dismiss());

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        dialog.show();
    }
}
