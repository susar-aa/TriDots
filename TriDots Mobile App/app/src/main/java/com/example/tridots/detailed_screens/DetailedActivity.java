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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.tridots.ChatActivity;
import com.example.tridots.InquiryActivity;
import com.example.tridots.R;
import com.example.tridots.detailed_screens.SellerDetailActivity;
import com.example.tridots.adapters.FeedbackAdapter;
import com.example.tridots.models.Feedback;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DetailedActivity extends AppCompatActivity {

    private ImageView productImage;
    private TextView productName, brand, model, productDescription, pricePerHour, pricePerDay,
            productLocation, availabilityStatus, averageRating, keywords;
    private Button inquireButton, sendMessageButton;
    private MaterialButton sellerInfoButton, previewImageButton;

    private int listerId;
    private String listerName;
    private String firstImageUrl;
    private int currentUserId;

    private RecyclerView feedbacksRecyclerView;
    private FeedbackAdapter feedbackAdapter;
    private List<Feedback> feedbackList;

    private static final String PREF_NAME = "user_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String TAG = "DetailedActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt(KEY_USER_ID, -1);

        initializeViews();
        setupRecyclerView();

        Intent intent = getIntent();
        final int adId = intent.getIntExtra("ad_id", -1);
        final String adType = intent.getStringExtra("ad_type");

        if (adId != -1 && adType != null) {
            populateFromIntent(intent);
            setupListeners(adType, adId);
            fetchFeedbacks(adType, adId);
        } else {
            Toast.makeText(this, "Error: Missing ad details.", Toast.LENGTH_LONG).show();
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

        productImage = findViewById(R.id.productImage);
        productName = findViewById(R.id.productName);
        brand = findViewById(R.id.brand);
        model = findViewById(R.id.model);
        productDescription = findViewById(R.id.productDescription);
        pricePerHour = findViewById(R.id.pricePerHour);
        pricePerDay = findViewById(R.id.pricePerDay);
        productLocation = findViewById(R.id.productLocation);
        availabilityStatus = findViewById(R.id.availabilityStatus);
        averageRating = findViewById(R.id.averageRating);
        keywords = findViewById(R.id.keywords);
        inquireButton = findViewById(R.id.inquireButton);
        sendMessageButton = findViewById(R.id.sendMessageButton);
        sellerInfoButton = findViewById(R.id.sellerInfoButton);
        previewImageButton = findViewById(R.id.previewImageButton);
        feedbacksRecyclerView = findViewById(R.id.feedbacksRecyclerView);
    }

    private void setupRecyclerView() {
        feedbackList = new ArrayList<>();
        feedbackAdapter = new FeedbackAdapter(feedbackList);
        feedbacksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        feedbacksRecyclerView.setAdapter(feedbackAdapter);
        feedbacksRecyclerView.setNestedScrollingEnabled(false);
    }

    private void populateFromIntent(Intent intent) {
        firstImageUrl = intent.getStringExtra("image_url");
        Glide.with(this).load(firstImageUrl).placeholder(R.drawable.ad_placeholder).into(productImage);

        productName.setText(intent.getStringExtra("product_name"));
        brand.setText(intent.getStringExtra("brand"));
        model.setText("Model: " + intent.getStringExtra("model"));
        productDescription.setText(intent.getStringExtra("description"));
        pricePerHour.setText(String.format(Locale.US, "Rs. %s / Hour", intent.getStringExtra("price_per_hour")));
        pricePerDay.setText(String.format(Locale.US, "Rs. %s / Day", intent.getStringExtra("price_per_day")));
        productLocation.setText(intent.getStringExtra("location"));
        availabilityStatus.setText(intent.getStringExtra("availability_status"));

        float rating = intent.getFloatExtra("average_rating", 0.0f);
        int reviews = intent.getIntExtra("review_count", 0);
        averageRating.setText(String.format(Locale.US, "%.1f (%d Reviews)", rating, reviews));

        keywords.setText(intent.getStringExtra("keywords"));

        listerId = intent.getIntExtra("lister_id", -1);
        listerName = intent.getStringExtra("lister_name");
        sellerInfoButton.setText(listerName);
    }

    private void setupListeners(String adType, int adId) {
        // MODIFIED: Added self-inquiry blocking logic
        inquireButton.setOnClickListener(v -> {
            if (currentUserId == listerId) {
                Toast.makeText(DetailedActivity.this, "You cannot inquire about your own ad.", Toast.LENGTH_SHORT).show();
            } else if (listerId != -1) {
                Intent inquiryIntent = new Intent(DetailedActivity.this, InquiryActivity.class);
                inquiryIntent.putExtra("ad_type", adType);
                inquiryIntent.putExtra("ad_id", adId);
                inquiryIntent.putExtra("product_name", productName.getText().toString());
                inquiryIntent.putExtra("lister_id", listerId);
                startActivity(inquiryIntent);
            } else {
                Toast.makeText(this, "Lister information not available.", Toast.LENGTH_SHORT).show();
            }
        });

        sendMessageButton.setOnClickListener(v -> {
            if (currentUserId != -1 && listerId != -1 && listerName != null && !listerName.isEmpty()) {
                if (currentUserId == listerId) {
                    Toast.makeText(DetailedActivity.this, "You cannot send a message to yourself.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent chatIntent = new Intent(DetailedActivity.this, ChatActivity.class);
                    chatIntent.putExtra("receiver_id", listerId);
                    chatIntent.putExtra("receiver_username", listerName);
                    startActivity(chatIntent);
                }
            } else {
                Toast.makeText(this, "Lister information not available.", Toast.LENGTH_SHORT).show();
            }
        });

        sellerInfoButton.setOnClickListener(v -> {
            if (listerId != -1) {
                Intent sellerIntent = new Intent(DetailedActivity.this, SellerDetailActivity.class);
                sellerIntent.putExtra("user_id", listerId);
                startActivity(sellerIntent);
            } else {
                Toast.makeText(this, "Lister information not available.", Toast.LENGTH_SHORT).show();
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

    private void fetchFeedbacks(String feedbackType, int itemId) {
        String url = "https://lionsgoldencircle.com/Tridots/Api/fetch_feedbacks.php?feedback_type=" + feedbackType + "&item_id=" + itemId;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    feedbackList.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            feedbackList.add(new Feedback(
                                    obj.getInt("feedback_id"),
                                    obj.getInt("user_id"),
                                    obj.optString("user_name", "User"),
                                    obj.optInt("rating", 0),
                                    obj.optString("comment", ""),
                                    obj.optString("created_at", "")
                            ));
                        }
                        feedbackAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing feedback JSON", e);
                    }
                },
                error -> Log.e(TAG, "Error fetching feedbacks", error)
        );
        Volley.newRequestQueue(this).add(request);
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
