package com.example.tridots;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tridots.adapters.SoldProductsPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.example.tridots.R;
import com.example.tridots.adapters.SoldOrdersAdapter; // Import the adapter
import com.example.tridots.models.SoldOrderHeader;
import com.example.tridots.models.SoldOrderItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// ProductsSoldActivity will now implement the listener interface
public class ProductsSoldActivity extends AppCompatActivity implements SoldOrdersAdapter.OnItemStatusChangeListener {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private SoldProductsPagerAdapter pagerAdapter;

    private RequestQueue requestQueue;
    private SharedPreferences sharedPreferences;
    private int loggedInUserId;

    private final String[] ORDER_STATUSES = {
            "All",
            "Pending",
            "Out for Delivery",
            "Ready to Pick Up",
            "Return",
            "Cancelled",
            "Out of Stock",
            "Completed"
    };

    private Map<String, ArrayList<SoldOrderHeader>> groupedOrderHeaders;

    private static final String GET_SOLD_PRODUCTS_URL = "https://lionsgoldencircle.com/Tridots/Api/get_sold_products.php";
    private static final String UPDATE_ITEM_STATUS_URL = "https://lionsgoldencircle.com/Tridots/Api/update_order_item_delivery_status.php"; // New API endpoint
    private static final String PREF_NAME = "user_session";
    private static final String KEY_USER_ID = "user_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_sold);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Products Sold");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        loggedInUserId = sharedPreferences.getInt(KEY_USER_ID, -1);

        if (loggedInUserId == -1) {
            Toast.makeText(this, "Seller ID not found. Please log in again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        tabLayout = findViewById(R.id.tabLayoutStatus);
        viewPager = findViewById(R.id.viewPagerSoldProducts);

        requestQueue = Volley.newRequestQueue(this);

        groupedOrderHeaders = new LinkedHashMap<>();
        for (String status : ORDER_STATUSES) {
            groupedOrderHeaders.put(status, new ArrayList<>());
        }

        fetchSoldProducts();
    }

    private void setupViewPagerAndTabs() {
        pagerAdapter = new SoldProductsPagerAdapter(this, ORDER_STATUSES, groupedOrderHeaders);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(pagerAdapter.getTabTitle(position))
        ).attach();
    }

    private void fetchSoldProducts() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_SOLD_PRODUCTS_URL,
                response -> {
                    Log.d("ProductsSoldActivity", "Response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.has("error")) {
                            Toast.makeText(ProductsSoldActivity.this, "Error: " + jsonResponse.getString("error"), Toast.LENGTH_LONG).show();
                            for (String status : ORDER_STATUSES) {
                                groupedOrderHeaders.get(status).clear();
                            }
                        } else if (jsonResponse.has("sold_products")) {
                            JSONArray jsonArray = jsonResponse.getJSONArray("sold_products");
                            parseAndGroupSoldProducts(jsonArray);
                        } else {
                            Toast.makeText(ProductsSoldActivity.this, "No sold products found or unexpected response.", Toast.LENGTH_LONG).show();
                            for (String status : ORDER_STATUSES) {
                                groupedOrderHeaders.get(status).clear();
                            }
                        }
                        setupViewPagerAndTabs();

                    } catch (JSONException e) {
                        Log.e("ProductsSoldActivity", "JSON Parsing Error: " + e.getMessage());
                        Toast.makeText(ProductsSoldActivity.this, "Error parsing sold products data.", Toast.LENGTH_LONG).show();
                        for (String status : ORDER_STATUSES) {
                            groupedOrderHeaders.get(status).clear();
                        }
                        setupViewPagerAndTabs();
                    }
                },
                error -> {
                    Log.e("ProductsSoldActivity", "Volley Error: " + (error.getMessage() != null ? error.getMessage() : "Unknown error"));
                    Toast.makeText(ProductsSoldActivity.this, "Failed to fetch sold products. Check network.", Toast.LENGTH_LONG).show();
                    for (String status : ORDER_STATUSES) {
                        groupedOrderHeaders.get(status).clear();
                    }
                    setupViewPagerAndTabs();
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("seller_id", String.valueOf(loggedInUserId));
                Log.d("ProductsSoldActivity", "Sending seller_id: " + loggedInUserId);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void parseAndGroupSoldProducts(JSONArray jsonArray) throws JSONException {
        for (String status : ORDER_STATUSES) {
            groupedOrderHeaders.get(status).clear();
        }

        LinkedHashMap<Integer, SoldOrderHeader> allOrdersMap = new LinkedHashMap<>();
        Map<String, LinkedHashMap<Integer, SoldOrderHeader>> tempGroupedByDeliveryStatus = new LinkedHashMap<>();

        for (String status : ORDER_STATUSES) {
            if (!status.equals("All")) {
                tempGroupedByDeliveryStatus.put(status, new LinkedHashMap<>());
            }
        }

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject itemObject = jsonArray.getJSONObject(i);

            int orderId = itemObject.getInt("order_id");
            String orderStatus = itemObject.getString("order_status");
            String deliveryStatus = itemObject.optString("delivery_status", "Pending");

            SoldOrderHeader allHeader = allOrdersMap.get(orderId);
            if (allHeader == null) {
                allHeader = new SoldOrderHeader(
                        orderId,
                        itemObject.getInt("buyer_user_id"),
                        itemObject.getString("buyer_username"),
                        itemObject.getString("order_date"),
                        orderStatus,
                        itemObject.getString("payment_status"),
                        itemObject.getDouble("total_amount"),
                        itemObject.getDouble("delivery_fee")
                );
                allOrdersMap.put(orderId, allHeader);
            }

            LinkedHashMap<Integer, SoldOrderHeader> statusSpecificOrdersMap = tempGroupedByDeliveryStatus.get(deliveryStatus);

            if (statusSpecificOrdersMap != null) { // Only process if the deliveryStatus maps to a defined tab
                SoldOrderHeader statusHeader = statusSpecificOrdersMap.get(orderId);
                if (statusHeader == null) {
                    statusHeader = new SoldOrderHeader(
                            orderId,
                            itemObject.getInt("buyer_user_id"),
                            itemObject.getString("buyer_username"),
                            itemObject.getString("order_date"),
                            orderStatus,
                            itemObject.getString("payment_status"),
                            itemObject.getDouble("total_amount"),
                            itemObject.getDouble("delivery_fee")
                    );
                    statusSpecificOrdersMap.put(orderId, statusHeader);
                }
                statusHeader.addSoldOrderItem(createSoldOrderItem(itemObject, deliveryStatus));
            } else {
                Log.w("ProductsSoldActivity", "Delivery status '" + deliveryStatus + "' from database is not defined in ORDER_STATUSES for specific tabs. Item " + itemObject.optInt("order_item_id") + " not added to specific tab.");
            }

            allHeader.addSoldOrderItem(createSoldOrderItem(itemObject, deliveryStatus));
        }

        groupedOrderHeaders.get("All").addAll(allOrdersMap.values());

        for (Map.Entry<String, LinkedHashMap<Integer, SoldOrderHeader>> entry : tempGroupedByDeliveryStatus.entrySet()) {
            String statusKey = entry.getKey();
            if (groupedOrderHeaders.containsKey(statusKey)) {
                groupedOrderHeaders.get(statusKey).addAll(entry.getValue().values());
            }
        }

        if (pagerAdapter != null) {
            for (String status : ORDER_STATUSES) {
                pagerAdapter.updateFragmentData(status, groupedOrderHeaders.get(status));
            }
        }
    }

    private SoldOrderItem createSoldOrderItem(JSONObject itemObject, String deliveryStatus) throws JSONException {
        int variantId = itemObject.optInt("variant_id", 0);
        String variantName = itemObject.optString("variant_name", "");
        double variantPrice = itemObject.optDouble("variant_price", itemObject.getDouble("unit_price"));
        String variantImageUrl = itemObject.optString("variant_image_url", "");

        SoldOrderItem item = new SoldOrderItem(
                itemObject.getInt("order_item_id"),
                itemObject.getInt("product_id"),
                itemObject.getString("product_name"),
                itemObject.getInt("quantity"),
                itemObject.getDouble("unit_price"),
                itemObject.optString("product_image_url"),
                variantId,
                variantName,
                variantPrice,
                variantImageUrl
        );
        item.setDeliveryStatus(deliveryStatus);
        return item;
    }

    // --- Implementation of OnItemStatusChangeListener ---
    @Override
    public void onStatusChangeRequested(int orderItemId, String newStatus) {
        Log.d("ProductsSoldActivity", "Requested status change for item ID: " + orderItemId + " to: " + newStatus);
        updateOrderItemStatus(orderItemId, newStatus);
    }

    private void updateOrderItemStatus(int orderItemId, String newStatus) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPDATE_ITEM_STATUS_URL,
                response -> {
                    Log.d("UpdateStatus", "Response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (!jsonResponse.has("error")) {
                            Toast.makeText(ProductsSoldActivity.this, "Status updated successfully!", Toast.LENGTH_SHORT).show();
                            // Refresh all data after successful update
                            fetchSoldProducts();
                        } else {
                            Toast.makeText(ProductsSoldActivity.this, "Failed to update status: " + jsonResponse.getString("error"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Log.e("UpdateStatus", "JSON Parsing Error: " + e.getMessage());
                        Toast.makeText(ProductsSoldActivity.this, "Error parsing update response.", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Log.e("UpdateStatus", "Volley Error: " + (error.getMessage() != null ? error.getMessage() : "Unknown error"));
                    Toast.makeText(ProductsSoldActivity.this, "Network error updating status.", Toast.LENGTH_LONG).show();
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("order_item_id", String.valueOf(orderItemId));
                params.put("new_delivery_status", newStatus);
                // Optionally, send seller_id for additional security/validation on backend
                params.put("seller_id", String.valueOf(loggedInUserId));
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}