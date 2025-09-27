package com.example.tridots;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.tridots.R;
import com.example.tridots.adapters.RentingAdapter;
import com.example.tridots.adapters.ServiceProviderAdapter;
import com.example.tridots.adapters.VehicleAdapter;
import com.example.tridots.models.RentingAd;
import com.example.tridots.models.ServiceProvider;
import com.example.tridots.models.VehicleAd;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SellerStoreActivity extends AppCompatActivity {

    private int sellerId;
    private String sellerName;
    private String sellerImageUrl; // To store the received image URL
    private TextView storeTitleTextView;
    private ImageView sellerStoreImageView; // Reference to the ImageView
    private RecyclerView storeRentingRecyclerView;
    private RentingAdapter storeRentingAdapter;
    private List<RentingAd> storeRentingList;

    private RecyclerView storeVehicleRecyclerView;
    private VehicleAdapter storeVehicleAdapter;
    private List<VehicleAd> storeVehicleList;

    private RecyclerView storeServiceRecyclerView;
    private ServiceProviderAdapter storeServiceAdapter;
    private List<ServiceProvider> storeServiceList;

    private static final String URL_USER_RENTINGS = "https://lionsgoldencircle.com/Tridots/Api/get_user_rentings.php?user_id=";
    private static final String URL_USER_VEHICLES = "https://lionsgoldencircle.com/Tridots/Api/get_user_vehicles.php?user_id=";
    private static final String URL_USER_SERVICES = "https://lionsgoldencircle.com/Tridots/Api/get_user_services.php?user_id=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_store);

        storeTitleTextView = findViewById(R.id.storeTitleTextView);
        sellerStoreImageView = findViewById(R.id.sellerStoreImageView); // Initialize the ImageView
        sellerId = getIntent().getIntExtra("user_id", -1);
        sellerName = getIntent().getStringExtra("seller_name");
        sellerImageUrl = getIntent().getStringExtra("seller_image_url"); // Retrieve the image URL

        if (sellerId != -1) {
            // Set the title of the store using the seller's name
            if (sellerName != null && !sellerName.isEmpty()) {
                storeTitleTextView.setText(sellerName + "'s Store");
            } else {
                storeTitleTextView.setText("Seller's Store"); // Fallback if name is not available
            }

            // Load the seller's image if a URL is available
            if (sellerImageUrl != null && !sellerImageUrl.isEmpty()) {
                Glide.with(this)
                        .load(sellerImageUrl)
                        .placeholder(R.drawable.placeholder_image) // Optional: Placeholder image
                        .error(R.drawable.image_error_placeholder) // Optional: Error image
                        .into(sellerStoreImageView);
            } else {
                sellerStoreImageView.setImageResource(R.drawable.placeholder_image); // Set a default placeholder if no URL
            }

            // Renting RecyclerView setup
            storeRentingRecyclerView = findViewById(R.id.storeRentingRecyclerView);
            storeRentingRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            storeRentingList = new ArrayList<>();
            storeRentingAdapter = new RentingAdapter(storeRentingList, this);
            storeRentingRecyclerView.setAdapter(storeRentingAdapter);
            fetchStoreRentings(sellerId);

            // Vehicle RecyclerView setup
            storeVehicleRecyclerView = findViewById(R.id.storeVehicleRecyclerView);
            storeVehicleRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            storeVehicleList = new ArrayList<>();
            storeVehicleAdapter = new VehicleAdapter(storeVehicleList, this);
            storeVehicleRecyclerView.setAdapter(storeVehicleAdapter);
            fetchStoreVehicles(sellerId);

            // Service RecyclerView setup
            storeServiceRecyclerView = findViewById(R.id.storeServiceRecyclerView);
            storeServiceRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            storeServiceList = new ArrayList<>();
            storeServiceAdapter = new ServiceProviderAdapter(storeServiceList, this);
            storeServiceRecyclerView.setAdapter(storeServiceAdapter);
            fetchStoreServices(sellerId);

        } else {
            Toast.makeText(this, "Error: Could not retrieve seller ID.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void fetchStoreRentings(int userId) {
        String url = URL_USER_RENTINGS + userId;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        storeRentingList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject rentingObject = response.getJSONObject(i);
                            RentingAd rentingAd = new RentingAd(
                                    rentingObject.getInt("rent_id"),
                                    rentingObject.getString("product_name"),
                                    rentingObject.getString("brand"),
                                    rentingObject.optString("model"),
                                    String.valueOf(rentingObject.optDouble("price_per_hour", 0.0)),
                                    String.valueOf(rentingObject.optDouble("price_per_day", 0.0)),
                                    rentingObject.optString("product_images"),
                                    rentingObject.optString("product_description"),
                                    rentingObject.optString("product_location"),
                                    rentingObject.optString("availability_status"),
                                    (float) rentingObject.optDouble("average_rating", 0.0),
                                    rentingObject.optInt("review_count", 0),
                                    rentingObject.optString("keywords"),
                                    rentingObject.getInt("user_id"),
                                    rentingObject.optString("lister_name")
                            );
                            storeRentingList.add(rentingAd);
                        }
                        storeRentingAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing renting data", Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
            Log.e("Volley Error", error.toString());
            Toast.makeText(this, "Error fetching renting data", Toast.LENGTH_SHORT).show();
        });
        Volley.newRequestQueue(this).add(request);
    }

    private void fetchStoreVehicles(int userId) {
        String url = URL_USER_VEHICLES + userId;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        storeVehicleList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject vehicleObject = response.getJSONObject(i);
                            VehicleAd vehicleAd = new VehicleAd(
                                    vehicleObject.getInt("vehicle_id"),
                                    vehicleObject.optString("vehicle_name"),
                                    vehicleObject.optString("brand"),
                                    vehicleObject.optString("model"),
                                    vehicleObject.optString("price_type"),
                                    vehicleObject.optDouble("amount", 0.0),
                                    vehicleObject.optString("vehicle_images"),
                                    vehicleObject.optString("location"),
                                    vehicleObject.optString("description"),
                                    vehicleObject.optString("capacity"),
                                    vehicleObject.optString("fuel_type"),
                                    vehicleObject.optString("transmission_type")
                            );
                            storeVehicleList.add(vehicleAd);
                        }
                        storeVehicleAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing vehicle data", Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
            Log.e("Volley Error", error.toString());
            Toast.makeText(this, "Error fetching vehicle data", Toast.LENGTH_SHORT).show();
        });
        Volley.newRequestQueue(this).add(request);
    }

    private void fetchStoreServices(int userId) {
        String url = URL_USER_SERVICES + userId;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        storeServiceList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject serviceObject = response.getJSONObject(i);
                            ServiceProvider serviceProvider = new ServiceProvider(
                                    serviceObject.getInt("seller_id"),
                                    serviceObject.getString("name"),
                                    serviceObject.getString("service_category_name"),
                                    serviceObject.optString("description"),
                                    serviceObject.optString("contact_number"),
                                    serviceObject.optString("email_address"),
                                    serviceObject.optString("address"),
                                    serviceObject.optInt("experience_years", 0),
                                    serviceObject.optString("qualifications"),
                                    serviceObject.optString("location"),
                                    serviceObject.getString("availability_status"),
                                    (float) serviceObject.optDouble("reviews_average", 0.0),
                                    serviceObject.optInt("review_count", 0),
                                    serviceObject.optString("verification_status"),
                                    serviceObject.optString("profile_picture")
                            );
                            serviceProvider.setUserId(serviceObject.getInt("user_id"));
                            serviceProvider.setListerName(serviceObject.optString("lister_name"));
                            storeServiceList.add(serviceProvider);
                        }
                        storeServiceAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing service data", Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
            Log.e("Volley Error", error.toString());
            Toast.makeText(this, "Error fetching service data", Toast.LENGTH_SHORT).show();
        });
        Volley.newRequestQueue(this).add(request);
    }
}