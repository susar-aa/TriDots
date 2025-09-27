// File: app/src/main/java/com/example/tridots/main_screens/ManageMarketplaceActivity.java
package com.example.tridots.Marketplace; // Ensure this package declaration matches your file's location

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tridots.Marketplace.AddProductActivity; // Import AddProductActivity
import com.example.tridots.R;
import com.example.tridots.Marketplace.ProductDetailActivity; // Import ProductDetailActivity
import com.example.tridots.adapters.MarketplacePagerAdapter;
import com.example.tridots.adapters.MarketplaceProductAdapter; // Import MarketplaceProductAdapter
import com.example.tridots.models.Product; // Import the Product model
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal; // Import BigDecimal
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.Serializable; // Product model implements Serializable (ensure Product.java has this)

// ManageMarketplaceActivity now implements MarketplaceProductAdapter.OnItemClickListener
public class ManageMarketplaceActivity extends AppCompatActivity implements MarketplaceProductAdapter.OnItemClickListener {

    private static final String TAG = "ManageMarketplaceAct";

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private MarketplacePagerAdapter pagerAdapter;
    private Button buttonAddNewProduct;
    private TextView textViewNoMarketplaceProducts; // Global no products message

    private RequestQueue requestQueue;
    private SharedPreferences sharedPreferences;
    private int sellerId;

    private static final String GET_MARKETPLACE_PRODUCTS_URL = "https://lionsgoldencircle.com/Tridots/Api/get_marketplace_products.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_marketplace);

        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        sellerId = sharedPreferences.getInt("user_id", -1);

        if (sellerId == -1) {
            Toast.makeText(this, "Seller ID not found. Please log in again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        tabLayout = findViewById(R.id.tabLayoutMarketplace);
        viewPager = findViewById(R.id.viewPagerMarketplaceProducts);
        buttonAddNewProduct = findViewById(R.id.buttonAddNewProduct);
        textViewNoMarketplaceProducts = findViewById(R.id.textViewNoMarketplaceProducts);

        requestQueue = Volley.newRequestQueue(this);

        // Set up click listener for "Add New Product" button
        buttonAddNewProduct.setOnClickListener(v -> {
            Intent intent = new Intent(ManageMarketplaceActivity.this, AddProductActivity.class);
            startActivity(intent);
        });

        // Initial fetch of products
        fetchMarketplaceProducts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh products when returning to this activity (e.g., after adding a new product)
        fetchMarketplaceProducts();
    }

    /**
     * Fetches all marketplace products for the logged-in seller.
     * These products are then distributed to the respective tabs (fragments).
     */
    private void fetchMarketplaceProducts() {
        Log.d(TAG, "Fetching marketplace products for Seller ID: " + sellerId);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_MARKETPLACE_PRODUCTS_URL,
                response -> {
                    Log.d(TAG, "Marketplace Products Response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.has("error")) {
                            Toast.makeText(ManageMarketplaceActivity.this, "Error: " + jsonResponse.getString("error"), Toast.LENGTH_LONG).show();
                            textViewNoMarketplaceProducts.setText("Error loading products: " + jsonResponse.getString("error"));
                            textViewNoMarketplaceProducts.setVisibility(View.VISIBLE);
                            tabLayout.setVisibility(View.GONE);
                            viewPager.setVisibility(View.GONE);
                        } else if (jsonResponse.has("products")) {
                            JSONArray productsArray = jsonResponse.getJSONArray("products");
                            List<Product> allProducts = parseProductsJson(productsArray);

                            if (pagerAdapter == null) {
                                Log.d(TAG, "Initializing MarketplacePagerAdapter and TabLayoutMediator for the first time.");
                                // Pass 'this' as the item click listener to the PagerAdapter constructor
                                pagerAdapter = new MarketplacePagerAdapter(ManageMarketplaceActivity.this, ManageMarketplaceActivity.this); // Pass 'this' as listener
                                viewPager.setAdapter(pagerAdapter);

                                new TabLayoutMediator(tabLayout, viewPager,
                                        (tab, position) -> tab.setText(pagerAdapter.getPageTitle(position))
                                ).attach();
                            }
                            pagerAdapter.updateAllProducts(allProducts);

                            if (allProducts.isEmpty()) {
                                Log.d(TAG, "Overall Marketplace Product list is empty after parsing.");
                                textViewNoMarketplaceProducts.setText("No products found.");
                                textViewNoMarketplaceProducts.setVisibility(View.VISIBLE);
                                tabLayout.setVisibility(View.GONE);
                                viewPager.setVisibility(View.GONE);
                            } else {
                                Log.d(TAG, "Overall Marketplace Product list populated. Size: " + allProducts.size());
                                textViewNoMarketplaceProducts.setVisibility(View.GONE);
                                tabLayout.setVisibility(View.VISIBLE);
                                viewPager.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Toast.makeText(ManageMarketplaceActivity.this, "Unexpected response from server.", Toast.LENGTH_LONG).show();
                            textViewNoMarketplaceProducts.setText("Unexpected server response.");
                            textViewNoMarketplaceProducts.setVisibility(View.VISIBLE);
                            tabLayout.setVisibility(View.GONE);
                            viewPager.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON Parsing Error: " + e.getMessage());
                        Toast.makeText(ManageMarketplaceActivity.this, "Error parsing product data.", Toast.LENGTH_LONG).show();
                        textViewNoMarketplaceProducts.setText("Error parsing product data.");
                        textViewNoMarketplaceProducts.setVisibility(View.VISIBLE);
                        tabLayout.setVisibility(View.GONE);
                        viewPager.setVisibility(View.GONE);
                    }
                },
                error -> {
                    Log.e(TAG, "Volley Error: " + error.getMessage());
                    Toast.makeText(ManageMarketplaceActivity.this, "Failed to fetch products. Please check your internet connection.", Toast.LENGTH_LONG).show();
                    textViewNoMarketplaceProducts.setText("Failed to fetch products. Check internet.");
                    textViewNoMarketplaceProducts.setVisibility(View.VISIBLE);
                    tabLayout.setVisibility(View.GONE);
                    viewPager.setVisibility(View.GONE);
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("seller_id", String.valueOf(sellerId));
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    /**
     * Parses the JSON array of products into a List<Product> objects.
     * @param jsonArray The JSONArray containing product data.
     * @return A List of Product objects.
     * @throws JSONException If there's an error parsing the JSON.
     */
    private List<Product> parseProductsJson(JSONArray jsonArray) throws JSONException {
        List<Product> parsedProducts = new ArrayList<>();
        Log.d(TAG, "Parsing " + jsonArray.length() + " products from JSON response.");

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject productObject = jsonArray.getJSONObject(i);
            Product product = new Product();

            // Corrected: Use camelCase setters as defined in your Product.java model
            product.setProductId(productObject.optInt("product_id", 0));
            product.setSellerId(productObject.optInt("seller_id", 0));
            product.setCategoryId(productObject.optInt("category_id", 0));
            product.setProductName(productObject.optString("product_name", "N/A"));
            product.setProductDescription(productObject.optString("product_description", "N/A"));
            product.setPrice(parseBigDecimal(productObject.optString("price", "0.00")));
            product.setStockQuantity(productObject.optInt("stock_quantity", 0));
            product.setSku(productObject.optString("sku", null));
            product.setProductStatus(productObject.optString("product_status", "active"));
            product.setProductCondition(productObject.optString("product_condition", "N/A"));
            product.setPostedDate(productObject.optString("posted_date", "N/A"));
            product.setLastUpdatedDate(productObject.optString("last_updated_date", "N/A"));
            product.setApprovedStatus(productObject.optString("approved_status", "Pending"));
            product.setAvailabilityStatus(productObject.optString("availability_status", "Available"));
            product.setKeywords(productObject.optString("keywords", null));
            product.setBrand(productObject.optString("brand", null));
            product.setModel(productObject.optString("model", null));
            product.setCompatibility(productObject.optString("compatibility", null));
            product.setWarrantyPolicy(productObject.optString("warranty_policy", null));
            product.setWeightG(productObject.optInt("weight_g", 0));
            product.setDimensionsCm(productObject.optString("dimensions_cm", null));
            product.setMaterial(productObject.optString("material", null));
            product.setColor(productObject.optString("color", null));
            product.setManufacturingDate(productObject.optString("manufacturing_date", null));
            product.setExpirationDate(productObject.optString("expiration_date", null));
            product.setStoreAddressLine1(productObject.optString("store_address_line1", "N/A"));
            product.setStoreAddressLine2(productObject.optString("store_address_line2", null));
            product.setStoreCity(productObject.optString("store_city", "N/A"));
            product.setStoreDistrict(productObject.optString("store_district", "N/A"));
            product.setStorePostalCode(productObject.optString("store_postal_code", "N/A"));
            product.setStoreCountry(productObject.optString("store_country", "Sri Lanka"));
            product.setDeliveryFee(parseBigDecimal(productObject.optString("delivery_fee", "0.00")));
            product.setThumbnailImageUrl(productObject.optString("thumbnail_image_url", null));

            // Handle nested images and variants
            if (productObject.has("images") && !productObject.isNull("images")) {
                JSONArray imagesArray = productObject.getJSONArray("images");
                List<Product.Image> imagesList = new ArrayList<>();
                for (int j = 0; j < imagesArray.length(); j++) {
                    JSONObject imageObject = imagesArray.getJSONObject(j);
                    Product.Image img = new Product.Image();
                    img.setImageId(imageObject.optInt("image_id", 0));
                    img.setProductId(imageObject.optInt("product_id", 0));
                    img.setImageUrl(imageObject.optString("image_url", null));
                    img.setThumbnail(imageObject.optInt("is_thumbnail", 0) == 1); // Use setThumbnail
                    img.setDisplayOrder(imageObject.optInt("display_order", 0));
                    imagesList.add(img);
                }
                product.setImages(imagesList);
            }

            if (productObject.has("variants") && !productObject.isNull("variants")) {
                JSONArray variantsArray = productObject.getJSONArray("variants");
                List<Product.Variant> variantsList = new ArrayList<>();
                for (int j = 0; j < variantsArray.length(); j++) {
                    JSONObject variantObject = variantsArray.getJSONObject(j);
                    Product.Variant var = new Product.Variant();
                    var.setVariantId(variantObject.optInt("variant_id", 0));
                    var.setProductId(variantObject.optInt("product_id", 0));
                    var.setVariantName(variantObject.optString("variant_name", null));
                    var.setVariantPrice(parseBigDecimal(variantObject.optString("variant_price", "0.00")));
                    var.setVariantStockQuantity(variantObject.optInt("variant_stock_quantity", 0));
                    var.setVariantImageUrl(variantObject.optString("variant_image_url", null));
                    variantsList.add(var);
                }
                product.setVariants(variantsList);
            }


            parsedProducts.add(product);
            // Corrected: Use camelCase getters for logging
            Log.d(TAG, "Parsed Product ID: " + product.getProductId() + " with Approved Status: " + product.getApprovedStatus());
        }
        return parsedProducts;
    }

    /**
     * Helper method to safely parse a String to BigDecimal, handling null/empty/invalid strings.
     * @param value The string to parse.
     * @return The parsed BigDecimal or BigDecimal.ZERO if parsing fails.
     */
    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.trim().isEmpty() || value.equalsIgnoreCase("null")) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            Log.e(TAG, "NumberFormatException parsing BigDecimal from: '" + value + "' - " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    // --- Implementation of MarketplaceProductAdapter.OnItemClickListener ---
    @Override
    public void onItemClick(Product product) {
        // When a product item is clicked, navigate to ProductDetailActivity
        // Use the exact camelCase getters as defined in your Product.java
        Log.d(TAG, "Product item clicked: " + product.getProductName() + " (ID: " + product.getProductId() + ")");
        Intent intent = new Intent(ManageMarketplaceActivity.this, ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, product.getProductId()); // Pass productId
        // Add a flag to indicate origin from Manage Marketplace
        intent.putExtra(ProductDetailActivity.EXTRA_FROM_MANAGE_MARKETPLACE, true);
        startActivity(intent);
    }
}
