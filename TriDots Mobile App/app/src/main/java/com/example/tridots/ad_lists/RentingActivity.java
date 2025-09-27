package com.example.tridots.ad_lists;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.tridots.R;
import com.example.tridots.adapters.RentingAdapter;
import com.example.tridots.models.RentingAd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RentingActivity extends AppCompatActivity {

    private RecyclerView rentingListView;
    private List<RentingAd> rentingList;
    private RentingAdapter adapter;
    private EditText searchEditText;
    private List<RentingAd> originalRentingList; // To hold the original full list
    private int subCategoryId;
    private static final String URL_RENTING_ITEMS = "https://lionsgoldencircle.com/Tridots/Api/get_renting_items.php?sub_category_id=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renting);

        rentingListView = findViewById(R.id.rentingListView);
        rentingListView.setLayoutManager(new LinearLayoutManager(this));
        searchEditText = findViewById(R.id.searchEditText);
        rentingList = new ArrayList<>();
        originalRentingList = new ArrayList<>(); // Initialize the original list
        adapter = new RentingAdapter(rentingList, this);
        rentingListView.setAdapter(adapter);

        subCategoryId = getIntent().getIntExtra("sub_category_id", -1);

        if (subCategoryId != -1) {
            fetchRentingItems(subCategoryId);
        } else {
            Toast.makeText(this, "Error: Sub Category ID not provided", Toast.LENGTH_SHORT).show();
            finish();
        }

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterRentingItems(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });
    }

    private void fetchRentingItems(int subCategoryId) {
        String url = URL_RENTING_ITEMS + subCategoryId;
        Log.d("Fetching URL", url);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("JSON Response", response.toString());
                        try {
                            rentingList.clear();
                            originalRentingList.clear(); // Clear the original list
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject rentingObject = response.getJSONObject(i);

                                int rentId = rentingObject.optInt("rent_id", -1);
                                String productName = rentingObject.optString("product_name", "N/A");
                                String brand = rentingObject.optString("brand", "N/A");
                                String model = rentingObject.optString("model", "");
                                String pricePerHour = String.valueOf(rentingObject.optDouble("price_per_hour", 0.0));
                                String pricePerDay = String.valueOf(rentingObject.optDouble("price_per_day", 0.0));
                                String productImageWithSlashes = rentingObject.optString("product_images", "");
                                String productImage = productImageWithSlashes.replace("\\/", "/");
                                String productDescription = rentingObject.optString("product_description", "");
                                String productLocation = rentingObject.optString("product_location", "");
                                String availabilityStatus = rentingObject.optString("availability_status", "");
                                float averageRating = (float) rentingObject.optDouble("average_rating", 0.0);
                                int reviewCount = rentingObject.optInt("review_count", 0);
                                String keywords = rentingObject.optString("keywords", "");
                                int listerId = rentingObject.optInt("user_id", -1);
                                String listerName = rentingObject.optString("lister_name", "N/A");


                                RentingAd rentingItem = new RentingAd(
                                        rentId,
                                        productName,
                                        brand,
                                        model,
                                        pricePerHour,
                                        pricePerDay,
                                        productImage,
                                        productDescription,
                                        productLocation,
                                        availabilityStatus,
                                        averageRating,
                                        reviewCount,
                                        keywords,
                                        listerId, // Include listerId
                                        listerName // Include listerName
                                );
                                rentingList.add(rentingItem);
                                originalRentingList.add(rentingItem);
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Log.e("JSON Parsing Error", "Error processing the JSON array: " + e.getMessage());
                            e.printStackTrace();
                            Toast.makeText(RentingActivity.this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", error.toString());
                Toast.makeText(RentingActivity.this, "Error fetching renting items", Toast.LENGTH_SHORT).show();
            }
        });

        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }

    private void filterRentingItems(String query) {
        query = query.toLowerCase(Locale.getDefault());
        rentingList.clear();
        if (query.isEmpty()) {
            rentingList.addAll(originalRentingList);
        } else {
            for (RentingAd ad : originalRentingList) {
                if (ad.getProductName().toLowerCase(Locale.getDefault()).contains(query) ||
                        ad.getBrand().toLowerCase(Locale.getDefault()).contains(query) ||
                        ad.getProductDescription().toLowerCase(Locale.getDefault()).contains(query) ||
                        ad.getProductLocation().toLowerCase(Locale.getDefault()).contains(query) ||
                        ad.getListerName().toLowerCase(Locale.getDefault()).contains(query)) { // Included lister name in filter
                    rentingList.add(ad);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}