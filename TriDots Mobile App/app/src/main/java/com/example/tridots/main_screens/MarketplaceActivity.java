package com.example.tridots.main_screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.tridots.Marketplace.CartActivity;
import com.example.tridots.Marketplace.ProductDetailActivity;
import com.example.tridots.R;
import com.example.tridots.adapters.ProductAdapter;
import com.example.tridots.models.Product;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MarketplaceActivity extends AppCompatActivity implements ProductAdapter.OnItemClickListener, View.OnClickListener {

    // --- UI Components ---
    private RecyclerView recyclerViewProducts;

    // --- Custom Bottom Navigation Components ---
    private LinearLayout navHome, navDashboard, navMarketplace, navInbox, navProfile;
    private View indicatorHome, indicatorDashboard, indicatorInbox, indicatorProfile;
    private ImageView iconHome, iconDashboard, iconInbox, iconProfile;
    private TextView labelHome, labelDashboard, labelInbox, labelProfile;

    // --- Data & Networking ---
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private RequestQueue requestQueue;

    private static final String API_BASE_URL = "https://lionsgoldencircle.com/Tridots/Api/";
    private static final String GET_PRODUCTS_URL = API_BASE_URL + "get_approved_products.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketplace);

        initializeViews();
        setupRecyclerView();
        setupListeners();

        requestQueue = Volley.newRequestQueue(this);
        fetchApprovedProducts();
    }

    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbar_marketplace);
        setSupportActionBar(toolbar);

        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);

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

        // Set Marketplace as the default selected tab
        selectTab(2); // 0=Home, 1=Dashboard, 2=Marketplace
    }

    private void setupRecyclerView() {
        recyclerViewProducts.setLayoutManager(new GridLayoutManager(this, 2));
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, productList, this);
        recyclerViewProducts.setAdapter(productAdapter);
    }

    private void setupListeners() {
        navHome.setOnClickListener(this);
        navDashboard.setOnClickListener(this);
        navMarketplace.setOnClickListener(this);
        navInbox.setOnClickListener(this);
        navProfile.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        int id = v.getId();
        int tabIndex = -1;

        // Handle Navigation Clicks
        if (id == R.id.navHome) {
            tabIndex = 0;
            intent = new Intent(this, HomeActivity.class);
        } else if (id == R.id.navDashboard) {
            tabIndex = 1;
            intent = new Intent(this, DashboardActivity.class);
        } else if (id == R.id.navMarketplace) {
            tabIndex = 2;
            // Already here, do nothing
        } else if (id == R.id.navInbox) {
            tabIndex = 3;
            intent = new Intent(this, InboxActivity.class);
        } else if (id == R.id.navProfile) {
            tabIndex = 4;
            intent = new Intent(this, ProfileActivity.class);
        }

        if (intent != null) {
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish(); // Finish current activity to prevent stacking
        }

        if (tabIndex != -1) {
            animateTabPress(v);
            selectTab(tabIndex);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_cart) {
            startActivity(new Intent(this, CartActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    // --- Data Fetching Logic ---
    private void fetchApprovedProducts() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                GET_PRODUCTS_URL,
                null,
                response -> {
                    List<Product> fetchedProducts = new ArrayList<>();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject productObject = response.getJSONObject(i);
                            Product product = new Product();
                            product.setProductId(productObject.getInt("product_id"));
                            product.setProductName(productObject.getString("product_name"));
                            product.setProductDescription(productObject.getString("product_description"));
                            product.setPrice(new BigDecimal(productObject.getString("price")));
                            product.setStockQuantity(productObject.getInt("stock_quantity"));
                            product.setProductCondition(productObject.getString("product_condition"));
                            product.setBrand(productObject.optString("brand"));
                            product.setModel(productObject.optString("model"));
                            product.setWeightG(productObject.optInt("weight_g"));
                            product.setDimensionsCm(productObject.optString("dimensions_cm"));
                            product.setColor(productObject.optString("color"));
                            product.setMaterial(productObject.optString("material"));
                            product.setStoreCity(productObject.optString("store_city"));
                            product.setStoreDistrict(productObject.optString("store_district"));

                            if (!productObject.isNull("delivery_fee")) {
                                product.setDeliveryFee(new BigDecimal(productObject.getString("delivery_fee")));
                            } else {
                                product.setDeliveryFee(BigDecimal.ZERO);
                            }

                            product.setThumbnailImageUrl(productObject.optString("thumbnail_image_url", ""));
                            fetchedProducts.add(product);
                        }
                        productAdapter.updateProducts(fetchedProducts);
                    } catch (JSONException e) {
                        Log.e("MarketplaceActivity", "JSON parsing error: " + e.getMessage());
                        Toast.makeText(MarketplaceActivity.this, "Error parsing product data.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("MarketplaceActivity", "Volley error: " + error.getMessage());
                    Toast.makeText(MarketplaceActivity.this, "Failed to load products. Check network.", Toast.LENGTH_SHORT).show();
                }
        );
        requestQueue.add(jsonArrayRequest);
    }

    @Override
    public void onItemClick(Product product) {
        Intent intent = new Intent(MarketplaceActivity.this, ProductDetailActivity.class);
        intent.putExtra("product_id", product.getProductId());
        startActivity(intent);
    }
}
