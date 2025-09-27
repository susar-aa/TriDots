package com.example.tridots.Marketplace;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tridots.R;
import com.example.tridots.models.Address;
import com.example.tridots.models.CartItem;
import com.google.android.material.button.MaterialButton;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * CheckoutActivity handles the final review of the order, displays the selected address
 * and order summary in a modern card-based UI, and initiates the payment process.
 */
public class CheckoutActivity extends AppCompatActivity {

    private static final String TAG = "CheckoutActivity";

    // --- UI Components from Redesigned Layout ---
    private TextView textViewSelectedAddress, textViewCheckoutTotal, buttonChangeAddress;
    private LinearLayout linearLayoutOrderItems;
    private MaterialButton buttonPay;
    private Toolbar toolbar;

    // --- Data and Logic ---
    private Address selectedDeliveryAddress;
    private int currentUserId;
    private RequestQueue requestQueue;
    private String selectedPaymentMethod;
    private BigDecimal currentDeliveryFee = BigDecimal.ZERO;
    private int currentOrderId = -1;

    // --- Stripe Components ---
    private PaymentSheet paymentSheet;
    private String clientSecret;

    // --- Constants ---
    private static final String PREF_NAME = "user_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String API_BASE_URL = "https://lionsgoldencircle.com/Tridots/Api/";
    private static final String CREATE_ORDER_URL = API_BASE_URL + "create_order.php";
    private static final String UPDATE_ORDER_STATUS_URL = API_BASE_URL + "update_order_status.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the content view to the redesigned layout
        setContentView(R.layout.activity_checkout);

        initializeViews();
        setupToolbar();
        initializeDependencies();

        if (!loadUserData()) {
            return; // Exit if user data is not available
        }

        handleIntentData();
        setupClickListeners();
        updateCheckoutSummary();
    }

    /**
     * Initializes all views from the XML layout.
     */
    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar_checkout);
        textViewSelectedAddress = findViewById(R.id.textViewSelectedAddress);
        textViewCheckoutTotal = findViewById(R.id.textViewCheckoutTotal);
        linearLayoutOrderItems = findViewById(R.id.linearLayoutOrderItems);
        buttonPay = findViewById(R.id.buttonPay);
        buttonChangeAddress = findViewById(R.id.buttonChangeAddress);
    }

    /**
     * Sets up the toolbar with a title and back navigation.
     */
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Hide default title to use custom TextView
        }
    }

    /**
     * Initializes non-view dependencies like RequestQueue and PaymentSheet.
     */
    private void initializeDependencies() {
        requestQueue = Volley.newRequestQueue(this);
        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);
    }

    /**
     * Loads the current user's ID from SharedPreferences.
     * @return true if user ID is found, false otherwise.
     */
    private boolean loadUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt(KEY_USER_ID, -1);
        if (currentUserId == -1) {
            Toast.makeText(this, "User session not found. Please log in again.", Toast.LENGTH_LONG).show();
            finish();
            return false;
        }
        return true;
    }

    /**
     * Retrieves and processes data passed via Intent, such as the selected address.
     */
    private void handleIntentData() {
        if (getIntent().hasExtra("selected_address")) {
            selectedDeliveryAddress = (Address) getIntent().getSerializableExtra("selected_address");
            if (selectedDeliveryAddress != null) {
                textViewSelectedAddress.setText(selectedDeliveryAddress.getFullAddress());
                currentDeliveryFee = selectedDeliveryAddress.getDelivery_fee() != null ? selectedDeliveryAddress.getDelivery_fee() : BigDecimal.ZERO;
            } else {
                handleNoAddressSelected();
            }
        } else {
            handleNoAddressSelected();
        }
    }

    /**
     * Sets up listeners for clickable views.
     */
    private void setupClickListeners() {
        buttonPay.setOnClickListener(v -> handlePaymentButtonClick());
        buttonChangeAddress.setOnClickListener(v -> {
            // Navigate back to the address selection screen
            Toast.makeText(this, "Functionality to change address to be implemented.", Toast.LENGTH_SHORT).show();
            // Example:
            // Intent intent = new Intent(CheckoutActivity.this, AddressSelectionActivity.class);
            // startActivity(intent);
            // finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Updates the UI with the current cart items, subtotal, delivery fee, and total.
     */
    private void updateCheckoutSummary() {
        List<CartItem> cartItems = CartManager.getInstance().getCartItems();
        BigDecimal cartSubtotal = CartManager.getInstance().getCartTotal();
        BigDecimal total = cartSubtotal.add(currentDeliveryFee);

        // Set the final total in the designated TextView
        textViewCheckoutTotal.setText(String.format(Locale.getDefault(), "LKR %.2f", total));

        // Dynamically populate the order items
        linearLayoutOrderItems.removeAllViews(); // Clear previous items
        LayoutInflater inflater = LayoutInflater.from(this);

        if (cartItems.isEmpty()) {
            TextView emptyCartView = new TextView(this);
            emptyCartView.setText("Your cart is empty.");
            linearLayoutOrderItems.addView(emptyCartView);
            buttonPay.setEnabled(false);
            buttonPay.setAlpha(0.5f);
        } else {
            for (CartItem item : cartItems) {
                // Inflate a custom layout for each item for better styling (optional but recommended)
                // For simplicity, we'll create TextViews programmatically here.
                TextView itemView = new TextView(this);
                itemView.setText(String.format(Locale.getDefault(), "%s (x%d) - LKR %.2f",
                        item.getDisplayName(), item.getQuantity(), item.getUnitPrice()));
                itemView.setTextSize(16);
                itemView.setPadding(0, 4, 0, 4);
                linearLayoutOrderItems.addView(itemView);
            }
            // Add delivery fee to the summary
            TextView deliveryFeeView = new TextView(this);
            deliveryFeeView.setText(String.format(Locale.getDefault(), "Delivery Fee - LKR %.2f", currentDeliveryFee));
            deliveryFeeView.setTextSize(16);
            deliveryFeeView.setPadding(0, 12, 0, 4);
            linearLayoutOrderItems.addView(deliveryFeeView);

            buttonPay.setEnabled(true);
            buttonPay.setAlpha(1.0f);
        }
    }

    /**
     * Handles the logic when the main payment button is clicked.
     */
    private void handlePaymentButtonClick() {
        if (selectedDeliveryAddress == null) {
            Toast.makeText(this, "Please select a delivery address.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (CartManager.getInstance().getCartItemCount() == 0) {
            Toast.makeText(this, "Your cart is empty.", Toast.LENGTH_SHORT).show();
            return;
        }
        showPaymentMethodSelectionDialog();
    }

    /**
     * Displays a dialog for the user to select their preferred payment method.
     */
    private void showPaymentMethodSelectionDialog() {
        final String[] paymentMethods = {"Stripe (Card Payment)", "Cash On Delivery"};
        // FIX: Removed the missing R.style.AlertDialogTheme
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Payment Method");
        builder.setItems(paymentMethods, (dialog, which) -> {
            selectedPaymentMethod = (which == 0) ? "Stripe" : "CashOnDelivery";
            initiatePaymentAndOrderCreation();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    /**
     * Initiates the process of creating an order on the backend and then proceeds to payment.
     */
    private void initiatePaymentAndOrderCreation() {
        Log.d(TAG, "Initiating order creation with payment method: " + selectedPaymentMethod);
        Toast.makeText(this, "Placing your order...", Toast.LENGTH_SHORT).show();
        buttonPay.setEnabled(false); // Disable button to prevent multiple clicks

        // Prepare cart items as a JSON array
        final JSONArray cartItemsArray = new JSONArray();
        for (CartItem item : CartManager.getInstance().getCartItems()) {
            try {
                JSONObject itemObject = new JSONObject();
                itemObject.put("product_id", item.getProduct().getProductId());
                itemObject.put("quantity", item.getQuantity());
                itemObject.put("price", item.getUnitPrice().toPlainString());
                if (item.getVariant() != null) {
                    itemObject.put("variant_id", item.getVariant().getVariantId());
                }
                cartItemsArray.put(itemObject);
            } catch (JSONException e) {
                Log.e(TAG, "Error creating JSON for cart item: " + e.getMessage());
                Toast.makeText(this, "Error preparing order data.", Toast.LENGTH_SHORT).show();
                buttonPay.setEnabled(true);
                return;
            }
        }

        // Create the network request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CREATE_ORDER_URL,
                response -> handleCreateOrderResponse(response),
                error -> {
                    Log.e(TAG, "Volley error creating order: " + error.toString());
                    Toast.makeText(CheckoutActivity.this, "Failed to create order. Please check your network.", Toast.LENGTH_LONG).show();
                    buttonPay.setEnabled(true);
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(currentUserId));
                params.put("address_id", String.valueOf(selectedDeliveryAddress.getAddressId()));
                params.put("total_amount", CartManager.getInstance().getCartTotal().add(currentDeliveryFee).toPlainString());
                params.put("cart_items_json", cartItemsArray.toString());
                params.put("payment_method", selectedPaymentMethod != null ? selectedPaymentMethod : "unselected");
                params.put("delivery_fee", currentDeliveryFee.toPlainString());
                Log.d(TAG, "Order Params: " + params);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    /**
     * Handles the response from the create_order.php API endpoint.
     * @param response The JSON response string from the server.
     */
    private void handleCreateOrderResponse(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            if (jsonResponse.getBoolean("success")) {
                currentOrderId = jsonResponse.optInt("order_id", -1);
                Log.d(TAG, "Order created successfully. Order ID: " + currentOrderId);

                if ("Stripe".equals(selectedPaymentMethod)) {
                    clientSecret = jsonResponse.optString("client_secret");
                    String publishableKey = jsonResponse.optString("stripe_publishable_key");

                    if (publishableKey != null && !publishableKey.isEmpty()) {
                        PaymentConfiguration.init(getApplicationContext(), publishableKey);
                    }

                    if (clientSecret != null && !clientSecret.isEmpty()) {
                        presentPaymentSheet(clientSecret);
                    } else {
                        handlePaymentError("Payment initialization failed: Missing client secret.");
                    }
                } else { // CashOnDelivery
                    Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_LONG).show();
                    CartManager.getInstance().clearCart();
                    // Optionally navigate to a success screen or back to the main activity
                    finish();
                }
            } else {
                handlePaymentError("Order creation failed: " + jsonResponse.getString("message"));
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON parsing error in create order response: " + e.getMessage(), e);
            handlePaymentError("Error processing order response.");
        }
    }

    /**
     * Presents the Stripe Payment Sheet to the user.
     * @param clientSecret The client secret for the PaymentIntent.
     */
    private void presentPaymentSheet(String clientSecret) {
        PaymentSheet.Configuration configuration = new PaymentSheet.Configuration("TriDots Checkout");
        paymentSheet.presentWithPaymentIntent(clientSecret, configuration);
    }

    /**
     * Handles the result from the Stripe Payment Sheet.
     */
    private void onPaymentSheetResult(@NonNull PaymentSheetResult paymentSheetResult) {
        buttonPay.setEnabled(true);
        String paymentStatus, orderStatus, statusMessage;

        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            paymentStatus = "Paid";
            orderStatus = "Pending"; // Or "Processing"
            statusMessage = "Payment Succeeded!";
            Log.d(TAG, "PaymentSheet: Completed for Order ID: " + currentOrderId);

            updateOrderStatusOnBackend(currentOrderId, orderStatus, paymentStatus);
            CartManager.getInstance().clearCart();
            Intent intent = new Intent(this, ReceiptActivity.class);
            intent.putExtra("order_id", currentOrderId);
            startActivity(intent);
            finish();

        } else if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            paymentStatus = "Canceled";
            orderStatus = "Pending"; // Or "Cancelled"
            statusMessage = "Payment Canceled.";
            Log.d(TAG, "PaymentSheet: Canceled for Order ID: " + currentOrderId);
            updateOrderStatusOnBackend(currentOrderId, orderStatus, paymentStatus);
            Toast.makeText(this, statusMessage, Toast.LENGTH_LONG).show();

        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            PaymentSheetResult.Failed failedResult = (PaymentSheetResult.Failed) paymentSheetResult;
            paymentStatus = "Failed";
            orderStatus = "Cancelled";
            statusMessage = "Payment Failed: " + failedResult.getError().getLocalizedMessage();
            Log.e(TAG, "PaymentSheet: Failed for Order ID: " + currentOrderId, failedResult.getError());
            updateOrderStatusOnBackend(currentOrderId, orderStatus, paymentStatus);
            Toast.makeText(this, statusMessage, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Updates the order status on the backend server.
     */
    private void updateOrderStatusOnBackend(int orderId, String newOrderStatus, String newPaymentStatus) {
        if (orderId == -1) return;

        StringRequest statusUpdateRequest = new StringRequest(Request.Method.POST, UPDATE_ORDER_STATUS_URL,
                response -> Log.d(TAG, "Backend status update response: " + response),
                error -> Log.e(TAG, "Volley error updating order status: " + error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("order_id", String.valueOf(orderId));
                params.put("order_status", newOrderStatus);
                params.put("payment_status", newPaymentStatus);
                return params;
            }
        };
        requestQueue.add(statusUpdateRequest);
    }

    /**
     * Handles UI and logging for when no address is selected.
     */
    private void handleNoAddressSelected() {
        textViewSelectedAddress.setText("No address selected. Please go back and choose one.");
        buttonPay.setEnabled(false);
        buttonPay.setAlpha(0.5f);
    }

    /**
     * Centralized method to handle payment errors, re-enabling the button and showing a toast.
     * @param message The error message to display.
     */
    private void handlePaymentError(String message) {
        Log.e(TAG, message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        buttonPay.setEnabled(true);
        if (currentOrderId != -1) {
            updateOrderStatusOnBackend(currentOrderId, "Failed", "PaymentIntent_Error");
        }
    }

    // --- METHODS CALLED FROM FRAGMENTS ---

    /**
     * Called from PaymentMethodFragment to set the chosen payment method.
     * @param paymentMethod The selected payment method (e.g., "Stripe", "CashOnDelivery").
     */
    public void setSelectedPaymentMethod(String paymentMethod) {
        this.selectedPaymentMethod = paymentMethod;
        Log.d(TAG, "Payment method set from fragment: " + paymentMethod);
    }

    /**
     * Called from ShippingAddressFragment to update the delivery fee.
     * @param deliveryFee The calculated delivery fee.
     */
    public void updateDeliveryFee(BigDecimal deliveryFee) {
        this.currentDeliveryFee = deliveryFee;
        Log.d(TAG, "Delivery fee updated from fragment to: " + deliveryFee);
        updateCheckoutSummary();
    }

    /**
     * Called from ShippingAddressFragment to proceed to the next step (e.g., payment).
     */
    public void goToNextStep() {
        Log.d(TAG, "goToNextStep called from fragment.");
        // This is where you would transition to the payment fragment if using a ViewPager
        Toast.makeText(this, "Proceeding to payment selection.", Toast.LENGTH_SHORT).show();
    }

    public void placeOrderFromFragment() {
        Log.d(TAG, "placeOrderFromFragment called.");
        if (selectedPaymentMethod == null || selectedPaymentMethod.isEmpty()) {
            // This case should ideally not happen if the fragment logic is correct, but as a fallback:
            showPaymentMethodSelectionDialog();
        } else {
            initiatePaymentAndOrderCreation();
        }
    }
}
