// File: app/src/main/java/com/example/tridots/main_screens/MyOrdersActivity.java
package com.example.tridots.Marketplace;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tridots.R;
import com.example.tridots.Marketplace.ReceiptActivity;
import com.example.tridots.adapters.MyOrdersAdapter;
import com.example.tridots.adapters.OrderPagerAdapter;
import com.example.tridots.models.Order;
import com.example.tridots.models.OrderItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyOrdersActivity extends AppCompatActivity
        implements MyOrdersAdapter.OnItemClickListener, MyOrdersAdapter.OnCancelClickListener { // Implement both interfaces

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private OrderPagerAdapter pagerAdapter;

    private RequestQueue requestQueue;
    private SharedPreferences sharedPreferences;
    private int userId;
    private TextView noOrdersTextView;

    private static final String GET_USER_ORDERS_URL = "https://lionsgoldencircle.com/Tridots/Api/get_user_orders.php";
    private static final String CANCEL_ORDER_URL = "https://lionsgoldencircle.com/Tridots/Api/cancel_order.php";
    private static final String TAG = "MyOrdersActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "User ID not found. Please log in again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        tabLayout = findViewById(R.id.tabLayoutOrders);
        viewPager = findViewById(R.id.viewPagerOrders);
        noOrdersTextView = findViewById(R.id.textViewNoOrders);

        requestQueue = Volley.newRequestQueue(this);

        fetchUserOrders();
    }

    /**
     * Fetches ALL orders for the logged-in user from the backend API.
     * After fetching, it updates the PagerAdapter which handles filtering into tabs.
     */
    private void fetchUserOrders() {
        Log.d(TAG, "Fetching user orders for User ID: " + userId);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_USER_ORDERS_URL,
                response -> {
                    Log.d(TAG, "Orders Response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.has("error")) {
                            Toast.makeText(MyOrdersActivity.this, "Error: " + jsonResponse.getString("error"), Toast.LENGTH_LONG).show();
                            noOrdersTextView.setText("Error loading orders: " + jsonResponse.getString("error"));
                            noOrdersTextView.setVisibility(View.VISIBLE);
                            tabLayout.setVisibility(View.GONE);
                            viewPager.setVisibility(View.GONE);
                        } else if (jsonResponse.has("orders")) {
                            JSONArray ordersArray = jsonResponse.getJSONArray("orders");
                            List<Order> allOrders = parseOrdersJson(ordersArray);

                            if (pagerAdapter == null) {
                                Log.d(TAG, "Initializing PagerAdapter and TabLayoutMediator for the first time.");
                                // IMPORTANT: Pass 'this' as both item click and cancel click listener to OrderPagerAdapter
                                pagerAdapter = new OrderPagerAdapter(MyOrdersActivity.this, MyOrdersActivity.this, MyOrdersActivity.this);
                                viewPager.setAdapter(pagerAdapter);

                                new TabLayoutMediator(tabLayout, viewPager,
                                        (tab, position) -> tab.setText(pagerAdapter.getPageTitle(position))
                                ).attach();
                            }
                            pagerAdapter.updateAllOrders(allOrders);

                            if (allOrders.isEmpty()) {
                                Log.d(TAG, "Overall Order list is empty after parsing.");
                                noOrdersTextView.setText("No orders found.");
                                noOrdersTextView.setVisibility(View.VISIBLE);
                                tabLayout.setVisibility(View.GONE);
                                viewPager.setVisibility(View.GONE);
                            } else {
                                Log.d(TAG, "Overall Order list populated. Size: " + allOrders.size());
                                noOrdersTextView.setVisibility(View.GONE);
                                tabLayout.setVisibility(View.VISIBLE);
                                viewPager.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Toast.makeText(MyOrdersActivity.this, "Unexpected response from server.", Toast.LENGTH_LONG).show();
                            noOrdersTextView.setText("Unexpected server response.");
                            noOrdersTextView.setVisibility(View.VISIBLE);
                            tabLayout.setVisibility(View.GONE);
                            viewPager.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON Parsing Error: " + e.getMessage());
                        Toast.makeText(MyOrdersActivity.this, "Error parsing order data.", Toast.LENGTH_LONG).show();
                        noOrdersTextView.setText("Error parsing order data.");
                        noOrdersTextView.setVisibility(View.VISIBLE);
                        tabLayout.setVisibility(View.GONE);
                        viewPager.setVisibility(View.GONE);
                    }
                },
                error -> {
                    Log.e(TAG, "Volley Error: " + error.getMessage());
                    Toast.makeText(MyOrdersActivity.this, "Failed to fetch orders. Please check your internet connection.", Toast.LENGTH_LONG).show();
                    noOrdersTextView.setText("Failed to fetch orders. Check internet.");
                    noOrdersTextView.setVisibility(View.VISIBLE);
                    tabLayout.setVisibility(View.GONE);
                    viewPager.setVisibility(View.GONE);
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

    /**
     * Parses the JSON array of orders into a List<Order> objects.
     */
    private List<Order> parseOrdersJson(JSONArray jsonArray) throws JSONException {
        List<Order> parsedOrders = new ArrayList<>();
        Log.d(TAG, "Parsing " + jsonArray.length() + " orders from JSON response.");

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject orderObject = jsonArray.getJSONObject(i);
            Order order = new Order();

            order.setOrderId(orderObject.optInt("order_id", 0));

            if (orderObject.has("delivery_address") && !orderObject.isNull("delivery_address")) {
                JSONObject deliveryAddressObject = orderObject.getJSONObject("delivery_address");
                if (deliveryAddressObject.has("address_id") && !deliveryAddressObject.isNull("address_id")) {
                    order.setAddressId(deliveryAddressObject.optInt("address_id", -1));
                } else {
                    Log.w(TAG, "address_id is missing or null within delivery_address for order_id: " + order.getOrderId());
                    order.setAddressId(-1);
                }
            } else {
                Log.w(TAG, "delivery_address object is missing or null for order_id: " + order.getOrderId());
                order.setAddressId(-1);
            }

            order.setOrderDate(orderObject.optString("order_date", "N/A"));
            order.setTotalAmount(new BigDecimal(orderObject.optString("total_amount", "0.00")));
            order.setDeliveryFee(new BigDecimal(orderObject.optString("delivery_fee", "0.00")));
            order.setOrderStatus(orderObject.optString("order_status", "Unknown"));
            order.setPaymentMethod(orderObject.optString("payment_method", "N/A"));
            order.setPaymentStatus(orderObject.optString("payment_status", "Unknown"));
            order.setStripePaymentIntentId(orderObject.optString("stripe_payment_intent_id"));
            order.setTransactionId(orderObject.optString("transaction_id"));

            List<OrderItem> items = new ArrayList<>();
            if (orderObject.has("items")) {
                JSONArray itemsArray = orderObject.getJSONArray("items");
                for (int j = 0; j < itemsArray.length(); j++) {
                    JSONObject itemObject = itemsArray.getJSONObject(j);
                    OrderItem item = new OrderItem();
                    item.setOrderItemId(itemObject.optInt("order_item_id", 0));
                    item.setOrderId(itemObject.optInt("order_id", 0));
                    item.setProductId(itemObject.optInt("product_id", 0));
                    if (itemObject.has("variant_id") && !itemObject.isNull("variant_id")) {
                        item.setVariantId(itemObject.optInt("variant_id", 0));
                    } else {
                        item.setVariantId(null);
                    }
                    item.setQuantity(itemObject.optInt("quantity", 0));
                    item.setUnitPrice(new BigDecimal(itemObject.optString("unit_price", "0.00")));
                    items.add(item);
                }
            }
            order.setItems(items);
            parsedOrders.add(order);
            Log.d(TAG, "Parsed Order ID: " + order.getOrderId() + " with status: " + order.getOrderStatus());
        }
        return parsedOrders;
    }

    @Override
    public void onItemClick(Order order) {
        Log.d(TAG, "Order item clicked: " + order.getOrderId());
        Intent intent = new Intent(MyOrdersActivity.this, ReceiptActivity.class);
        intent.putExtra("order_id", order.getOrderId());
        startActivity(intent);
    }

    @Override
    public void onCancelClick(Order order) {
        Log.d(TAG, "Cancel button clicked for order: " + order.getOrderId());
        new AlertDialog.Builder(this)
                .setTitle("Cancel Order")
                .setMessage("Are you sure you want to cancel order #" + order.getOrderId() + "? This action cannot be undone.")
                .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                    Log.d(TAG, "Confirmed cancellation for order: " + order.getOrderId());
                    sendCancelOrderRequest(order.getOrderId());
                })
                .setNegativeButton("No", (dialog, which) -> {
                    Log.d(TAG, "Cancelled cancellation for order: " + order.getOrderId());
                })
                .show();
    }

    private void sendCancelOrderRequest(int orderId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CANCEL_ORDER_URL,
                response -> {
                    Log.d("CancelOrder", "Cancel Response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        String message = jsonResponse.getString("message");
                        if (success) {
                            Toast.makeText(MyOrdersActivity.this, "Order #" + orderId + " cancelled successfully!", Toast.LENGTH_LONG).show();
                            fetchUserOrders();
                        } else {
                            Toast.makeText(MyOrdersActivity.this, "Failed to cancel order: " + message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Log.e("CancelOrder", "JSON Parsing Error for cancel response: " + e.getMessage() + " Response: " + response);
                        Toast.makeText(MyOrdersActivity.this, "Error processing cancel response.", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Log.e("CancelOrder", "Volley Error cancelling order: " + error.getMessage());
                    Toast.makeText(MyOrdersActivity.this, "Network error during cancellation. Please try again.", Toast.LENGTH_LONG).show();
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("order_id", String.valueOf(orderId));
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}
