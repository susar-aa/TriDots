package com.example.tridots.main_screens;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.tridots.R;
import com.example.tridots.adapters.BannerAdapter;
import com.example.tridots.adapters.RentingAdapter;
import com.example.tridots.adapters.ServiceProviderAdapter;
import com.example.tridots.adapters.VehicleAdapter;
import com.example.tridots.category_lists.LaborersCategoryActivity;
import com.example.tridots.category_lists.MachinesCategoryActivity;
import com.example.tridots.category_lists.VehiclesCategoryActivity;
import com.example.tridots.models.BannerImage;
import com.example.tridots.models.RentingAd;
import com.example.tridots.models.ServiceProvider;
import com.example.tridots.models.VehicleAd;
import com.example.tridots.system.VolleySingleton;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import android.os.Build;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    // --- UI Components ---
    private RecyclerView rentingRecyclerView, bannerRecyclerView, vehiclesRecyclerView, serviceProvidersRecyclerView;
    private ProgressBar progressBar;
    private MaterialCardView machinesCategory, vehiclesCategory, laborersCategory, marketplaceCategory;
    private TextView moreMachinesButton, moreVehiclesButton, moreServiceProvidersButton;

    // --- Custom Bottom Navigation Components ---
    private LinearLayout navHome, navDashboard, navMarketplace, navInbox, navProfile;
    private View indicatorHome, indicatorDashboard, indicatorInbox, indicatorProfile;
    private ImageView iconHome, iconDashboard, iconInbox, iconProfile;
    private TextView labelHome, labelDashboard, labelInbox, labelProfile;

    // --- Adapters & Data ---
    private RentingAdapter rentingAdapter;
    private BannerAdapter bannerAdapter;
    private VehicleAdapter vehicleAdapter;
    private ServiceProviderAdapter serviceProviderAdapter;
    private final List<RentingAd> rentingAds = new ArrayList<>();
    private final List<BannerImage> bannerImages = new ArrayList<>();
    private final List<VehicleAd> vehicleAds = new ArrayList<>();
    private final List<ServiceProvider> serviceProvidersList = new ArrayList<>();

    // --- Autoscroll Logic ---
    private LinearLayoutManager bannerLayoutManager;
    private final int SCROLL_DELAY = 3000;
    private final Handler scrollHandler = new Handler();
    private final Runnable scrollRunnable = new Runnable() {
        @Override
        public void run() {
            if (bannerAdapter != null && bannerAdapter.getItemCount() > 1) {
                int currentPosition = bannerLayoutManager.findFirstVisibleItemPosition();
                int nextPosition = (currentPosition + 1) % bannerAdapter.getItemCount();
                bannerRecyclerView.smoothScrollToPosition(nextPosition);
                scrollHandler.postDelayed(this, SCROLL_DELAY);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initializeViews();
        setupRecyclerViews();
        setupListeners();
        fetchAllData();
    }

    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        rentingRecyclerView = findViewById(R.id.recyclerView);
        bannerRecyclerView = findViewById(R.id.bannerRecyclerView);
        vehiclesRecyclerView = findViewById(R.id.vehiclesRecyclerView);
        serviceProvidersRecyclerView = findViewById(R.id.serviceProvidersRecyclerView);
        progressBar = findViewById(R.id.progressBar);

        machinesCategory = findViewById(R.id.machinesCategory);
        vehiclesCategory = findViewById(R.id.vehiclesCategory);
        laborersCategory = findViewById(R.id.laborersCategory);
        marketplaceCategory = findViewById(R.id.marketplaceCategory);

        moreMachinesButton = findViewById(R.id.moreMachinesButton);
        moreVehiclesButton = findViewById(R.id.moreVehiclesButton);
        moreServiceProvidersButton = findViewById(R.id.moreServiceProvidersButton);

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

        // Set initial selection
        selectTab(0);
    }

    private void setupRecyclerViews() {
        bannerLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        bannerRecyclerView.setLayoutManager(bannerLayoutManager);
        bannerAdapter = new BannerAdapter(bannerImages, this);
        bannerRecyclerView.setAdapter(bannerAdapter);

        rentingRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rentingAdapter = new RentingAdapter(rentingAds, this);
        rentingRecyclerView.setAdapter(rentingAdapter);

        vehiclesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        vehicleAdapter = new VehicleAdapter(vehicleAds, this);
        vehiclesRecyclerView.setAdapter(vehicleAdapter);

        serviceProvidersRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        serviceProviderAdapter = new ServiceProviderAdapter(serviceProvidersList, this);
        serviceProvidersRecyclerView.setAdapter(serviceProviderAdapter);
    }

    private void setupListeners() {
        machinesCategory.setOnClickListener(this);
        vehiclesCategory.setOnClickListener(this);
        laborersCategory.setOnClickListener(this);
        marketplaceCategory.setOnClickListener(this);
        moreMachinesButton.setOnClickListener(this);
        moreVehiclesButton.setOnClickListener(this);
        moreServiceProvidersButton.setOnClickListener(this);

        // --- Custom Bottom Navigation Listeners ---
        navHome.setOnClickListener(this);
        navDashboard.setOnClickListener(this);
        navMarketplace.setOnClickListener(this);
        navInbox.setOnClickListener(this);
        navProfile.setOnClickListener(this);
    }

    private void fetchAllData() {
        progressBar.setVisibility(View.VISIBLE);
        requestCounter = 0;
        fetchBannerImages();
        fetchRentingAds();
        fetchVehicles();
        fetchServiceProviders();
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        int id = v.getId();
        int tabIndex = -1;

        // Handle Navigation Clicks and set tab index
        if (id == R.id.navHome) {
            tabIndex = 0;
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
            intent = new Intent(this, ProfileActivity.class);
        }

        // Handle Category Clicks
        else if (id == R.id.machinesCategory || id == R.id.moreMachinesButton) {
            intent = new Intent(this, MachinesCategoryActivity.class);
        } else if (id == R.id.vehiclesCategory || id == R.id.moreVehiclesButton) {
            intent = new Intent(this, VehiclesCategoryActivity.class);
        } else if (id == R.id.marketplaceCategory || id == R.id.marketplaceCategory) {
            intent = new Intent(this, MarketplaceActivity.class);
        } else if (id == R.id.laborersCategory || id == R.id.moreServiceProvidersButton) {
            intent = new Intent(this, LaborersCategoryActivity.class);
        }

        // Perform navigation if an intent was created
        if (intent != null) {
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }

        // Update tab selection visuals if a nav item was clicked
        if (tabIndex != -1) {
            animateTabPress(v);
            selectTab(tabIndex);
        }
    }

    private void selectTab(int tabIndex) {
        resetAllTabs();
        switch (tabIndex) {
            case 0: // Home
                setTabSelected(iconHome, labelHome, indicatorHome);
                break;
            case 1: // Dashboard
                setTabSelected(iconDashboard, labelDashboard, indicatorDashboard);
                break;
            case 2: // Marketplace (no visual change, handled by its own style)
                break;
            case 3: // Inbox
                setTabSelected(iconInbox, labelInbox, indicatorInbox);
                break;
            case 4: // Profile
                setTabSelected(iconProfile, labelProfile, indicatorProfile);
                break;
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
        tab.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100)
                .withEndAction(() -> tab.animate().scaleX(1f).scaleY(1f).setDuration(100).start())
                .start();
    }

    private void animateIconBounce(ImageView icon) {
        icon.animate().scaleX(1.2f).scaleY(1.2f).setDuration(150)
                .withEndAction(() -> icon.animate().scaleX(1f).scaleY(1f).setDuration(150).setInterpolator(new BounceInterpolator()).start())
                .start();
    }

    // --- Data Fetching Methods ---
    private int requestCounter = 0;

    private void fetchBannerImages() {
        String url = "https://lionsgoldencircle.com/Tridots/Api/fetch_banners.php";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        bannerImages.clear();
                        for (int i = 0; i < response.length(); i++) {
                            bannerImages.add(new BannerImage(response.getString(i)));
                        }
                        bannerAdapter.notifyDataSetChanged();
                        if (bannerImages.size() > 1) startAutoScroll();
                    } catch (Exception e) {
                        Log.e("HomeActivity", "Error parsing banner data", e);
                    } finally {
                        checkIfLoadingComplete();
                    }
                },
                error -> {
                    Log.e("HomeActivity", "Network Error (Banner): " + error.getMessage());
                    checkIfLoadingComplete();
                });
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void fetchRentingAds() {
        String url = "https://lionsgoldencircle.com/Tridots/Api/fetch_renting_ads.php";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        rentingAds.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject ad = response.getJSONObject(i);
                            rentingAds.add(new RentingAd(
                                    ad.optInt("rent_id", -1),
                                    ad.optString("product_name", "N/A"),
                                    ad.optString("brand", "N/A"),
                                    ad.optString("model", ""),
                                    ad.optString("price_per_hour", "0"),
                                    ad.optString("price_per_day", "0"),
                                    ad.optString("product_images", ""),
                                    ad.optString("product_description", ""),
                                    ad.optString("product_location", ""),
                                    ad.optString("availability_status", ""),
                                    (float) ad.optDouble("average_rating", 0.0),
                                    ad.optInt("review_count", 0),
                                    ad.optString("keywords", ""),
                                    ad.optInt("user_id", -1),
                                    ad.optString("lister_name", "N/A")
                            ));
                        }
                        rentingAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Log.e("HomeActivity", "Error parsing renting data", e);
                    } finally {
                        checkIfLoadingComplete();
                    }
                },
                error -> {
                    Log.e("HomeActivity", "Network Error (Renting): " + error.getMessage());
                    checkIfLoadingComplete();
                });
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void fetchVehicles() {
        String url = "https://lionsgoldencircle.com/Tridots/Api/fetch_vehicles.php";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        vehicleAds.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject vehicle = response.getJSONObject(i);
                            vehicleAds.add(new VehicleAd(
                                    vehicle.getInt("vehicle_id"),
                                    vehicle.optString("vehicle_name", "N/A"),
                                    vehicle.optString("brand", "N/A"),
                                    vehicle.optString("model", "N/A"),
                                    vehicle.optString("price_type", ""),
                                    vehicle.optDouble("amount", 0.00),
                                    vehicle.optString("vehicle_images", ""),
                                    vehicle.optString("location", "N/A"),
                                    vehicle.optString("description", "N/A"),
                                    vehicle.optString("capacity", "N/A"),
                                    vehicle.optString("fuel_type", "N/A"),
                                    vehicle.optString("transmission_type", "N/A")
                            ));
                        }
                        vehicleAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Log.e("HomeActivity", "Error parsing vehicle data", e);
                    } finally {
                        checkIfLoadingComplete();
                    }
                },
                error -> {
                    Log.e("HomeActivity", "Network Error (Vehicles): " + error.getMessage());
                    checkIfLoadingComplete();
                });
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void fetchServiceProviders() {
        String url = "https://lionsgoldencircle.com/Tridots/Api/fetch_service_providers.php";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        serviceProvidersList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject provider = response.getJSONObject(i);
                            ServiceProvider serviceProvider = new ServiceProvider(
                                    provider.getInt("seller_id"),
                                    provider.getString("name"),
                                    provider.getString("service_category_name"),
                                    provider.getString("description"),
                                    provider.getString("contact_number"),
                                    provider.getString("email_address"),
                                    provider.getString("address"),
                                    provider.getInt("experience_years"),
                                    provider.getString("qualifications"),
                                    provider.getString("location"),
                                    provider.getString("availability_status"),
                                    (float) provider.optDouble("reviews_average", 0.0),
                                    provider.getInt("review_count"),
                                    provider.getString("verification_status"),
                                    provider.getString("profile_picture")
                            );
                            serviceProvider.setUserId(provider.getInt("user_id"));
                            serviceProvider.setListerName(provider.getString("lister_name"));
                            serviceProvidersList.add(serviceProvider);
                        }
                        serviceProviderAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Log.e("HomeActivity", "Error parsing service provider data", e);
                    } finally {
                        checkIfLoadingComplete();
                    }
                },
                error -> {
                    Log.e("HomeActivity", "Network Error (Service Providers): " + error.getMessage());
                    checkIfLoadingComplete();
                });
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private synchronized void checkIfLoadingComplete() {
        requestCounter++;
        if (requestCounter >= 4) { // 4 requests: banners, renting, vehicles, providers
            progressBar.setVisibility(View.GONE);
        }
    }

    // --- Autoscroll Lifecycle ---
    private void startAutoScroll() {
        scrollHandler.removeCallbacks(scrollRunnable);
        scrollHandler.postDelayed(scrollRunnable, SCROLL_DELAY);
    }

    private void stopAutoScroll() {
        scrollHandler.removeCallbacks(scrollRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectTab(0); // Ensure home is selected when returning
        startAutoScroll();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAutoScroll();
    }
}
