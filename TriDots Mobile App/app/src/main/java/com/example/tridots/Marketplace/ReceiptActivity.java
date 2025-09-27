package com.example.tridots.Marketplace;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast; // Ensure Toast is imported

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tridots.R;
import com.example.tridots.main_screens.DashboardActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * ReceiptActivity displays the details of a successfully placed order,
 * provides options to download the receipt as a PDF, share the PDF,
 * and navigate back to the main dashboard.
 */
public class ReceiptActivity extends AppCompatActivity {

    private static final String TAG = "ReceiptActivity"; // Tag for logging purposes

    // UI elements
    private TextView textViewReceiptOrderId, textViewReceiptOrderDate, textViewReceiptCustomerInfo,
            textViewReceiptSubtotal, textViewReceiptDeliveryFee, textViewReceiptTotal,
            textViewReceiptPaymentMethod, textViewReceiptPaymentStatus;
    private LinearLayout linearLayoutReceiptItems; // Container for dynamically added order items
    private Button buttonDownloadReceipt, buttonShareReceipt, buttonBackToDashboard; // Buttons for actions
    private LinearLayout receiptContentLayout; // The main layout containing receipt details, used for PDF generation

    private int orderId; // Stores the ID of the order being displayed
    private RequestQueue requestQueue; // Volley RequestQueue for network operations

    // API endpoint for fetching order details from your backend
    // ENSURE THESE ARE PLAIN STRING LITERALS, NO MARKDOWN []() FORMATTING
    private static final String API_BASE_URL = "https://lionsgoldencircle.com/Tridots/Api/"; // Corrected URL string
    private static final String GET_ORDER_DETAILS_URL = API_BASE_URL + "get_order_details.php";

    // Request code for checking/requesting storage permissions
    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt); // Set the layout for this activity

        // Initialize UI elements by finding their IDs from the layout file
        textViewReceiptOrderId = findViewById(R.id.textViewReceiptOrderId);
        textViewReceiptOrderDate = findViewById(R.id.textViewReceiptOrderDate);
        textViewReceiptCustomerInfo = findViewById(R.id.textViewReceiptCustomerInfo);
        linearLayoutReceiptItems = findViewById(R.id.linearLayoutReceiptItems);
        textViewReceiptSubtotal = findViewById(R.id.textViewReceiptSubtotal);
        textViewReceiptDeliveryFee = findViewById(R.id.textViewReceiptDeliveryFee);
        textViewReceiptTotal = findViewById(R.id.textViewReceiptTotal);
        textViewReceiptPaymentMethod = findViewById(R.id.textViewReceiptPaymentMethod);
        textViewReceiptPaymentStatus = findViewById(R.id.textViewReceiptPaymentStatus);
        buttonDownloadReceipt = findViewById(R.id.buttonDownloadReceipt);
        buttonShareReceipt = findViewById(R.id.buttonShareReceipt);
        buttonBackToDashboard = findViewById(R.id.buttonBackToDashboard); // Initialize the new Dashboard button
        receiptContentLayout = findViewById(R.id.receiptContentLayout); // Get reference to the layout that contains all receipt content

        // Initialize Volley RequestQueue for making API calls
        requestQueue = Volley.newRequestQueue(this);

        // Retrieve the order ID passed from the previous activity (CheckoutActivity)
        if (getIntent().hasExtra("order_id")) {
            orderId = getIntent().getIntExtra("order_id", -1); // Get the integer order ID
            if (orderId != -1) {
                // If a valid order ID is received, proceed to fetch its details
                fetchOrderDetails(orderId);
            } else {
                // If the order ID is invalid (-1), display an error and close the activity
                Toast.makeText(this, "Error: Order ID not found.", Toast.LENGTH_SHORT).show();
                finish(); // Close activity
            }
        } else {
            // If no order ID was passed in the Intent, display an error and close
            Toast.makeText(this, "Error: Order ID not provided.", Toast.LENGTH_SHORT).show();
            finish(); // Close activity
        }

        // Set up click listeners for the buttons
        buttonDownloadReceipt.setOnClickListener(v -> {
            // Before generating and saving PDF, check if storage permissions are granted
            if (checkAndRequestPermissions()) {
                generateAndSavePdfReceipt();
            }
        });

        buttonShareReceipt.setOnClickListener(v -> {
            // Before sharing PDF, check if storage permissions are granted
            if (checkAndRequestPermissions()) {
                sharePdfReceipt();
            }
        });

        // Set click listener for the "Back to Dashboard" button
        buttonBackToDashboard.setOnClickListener(v -> {
            navigateToDashboard(); // Call the method to navigate to Dashboard
        });
    }

    /**
     * Fetches detailed order information from the backend API.
     * Uses a POST request to send the order ID and expects a JSON response.
     * @param orderId The integer ID of the order to fetch.
     */
    private void fetchOrderDetails(int orderId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_ORDER_DETAILS_URL,
                response -> {
                    // Log the full backend response for debugging. This is very useful!
                    Log.d(TAG, "Backend Response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        if (success) {
                            // If the backend indicates success, parse the 'order' object
                            JSONObject order = jsonResponse.getJSONObject("order");
                            displayOrderDetails(order); // Populate UI with order data
                        } else {
                            // If backend indicates a logical error (e.g., order not found)
                            String message = jsonResponse.getString("message");
                            Toast.makeText(ReceiptActivity.this, "Failed to load order details: " + message, Toast.LENGTH_LONG).show();
                            Log.e(TAG, "Failed to load order details: " + message);
                            finish(); // Close activity on data loading failure
                        }
                    } catch (JSONException e) {
                        // This catch block handles cases where the response is not valid JSON,
                        // often due to a fatal PHP error on the server side which returns HTML instead of JSON.
                        Log.e(TAG, "JSON parsing error for order details response: " + e.getMessage() + " Response: " + response);
                        Toast.makeText(ReceiptActivity.this, "Error processing order details from server. Please check server logs.", Toast.LENGTH_LONG).show();
                        finish(); // Close activity on critical parsing error
                    }
                },
                error -> {
                    // This block handles network errors (e.g., no internet, server unreachable)
                    // or errors reported by Volley itself (e.g., HTTP error codes like 404, 500).
                    String errorMessage = "Failed to fetch order details. Please check your internet connection.";
                    if (error.networkResponse != null) {
                        errorMessage += " (HTTP Status: " + error.networkResponse.statusCode + ")";
                        try {
                            // Attempt to extract more details from the network response data
                            String errorData = new String(error.networkResponse.data);
                            Log.e(TAG, "Volley error data: " + errorData);
                        } catch (Exception ex) {
                            Log.e(TAG, "Error parsing network error data: " + ex.getMessage());
                        }
                    } else if (error.getMessage() != null) {
                        errorMessage += " Error: " + error.getMessage();
                    }
                    Log.e(TAG, "Volley error fetching order details: " + error.toString());
                    Toast.makeText(ReceiptActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    finish(); // Close activity on network/API error
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Define the POST parameters to be sent to the PHP script
                Map<String, String> params = new HashMap<>();
                params.put("order_id", String.valueOf(orderId)); // Pass the order ID
                Log.d(TAG, "Sending order details request for ID: " + orderId);
                return params;
            }
        };
        requestQueue.add(stringRequest); // Add the request to Volley's queue
    }

    /**
     * Populates the TextViews and LinearLayout with the fetched order details.
     * @param order The JSONObject containing the parsed order data from the backend.
     * @throws JSONException If any expected JSON key is missing or data type is incorrect.
     */
    private void displayOrderDetails(JSONObject order) throws JSONException {
        // Set main order details
        textViewReceiptOrderId.setText("Order ID: #" + order.optString("order_id", "N/A"));
        textViewReceiptOrderDate.setText("Date: " + order.optString("order_date", "N/A"));
        textViewReceiptPaymentMethod.setText("Payment Method: " + order.optString("payment_method", "N/A"));
        textViewReceiptPaymentStatus.setText("Payment Status: " + order.optString("payment_status", "Unknown"));

        // Extract customer and address details from the nested "delivery_address" object
        JSONObject address = null;
        String fullAddress = "N/A";
        String city = "N/A";
        String postalCode = "N/A";

        if (order.has("delivery_address") && !order.isNull("delivery_address")) {
            address = order.getJSONObject("delivery_address");
            fullAddress = address.optString("full_address", "N/A");
            city = address.optString("city", "N/A");
            postalCode = address.optString("postal_code", "N/A");
        }

        String customerName = order.optString("customer_name", "N/A");
        textViewReceiptCustomerInfo.setText(
                "Customer: " + customerName +
                        "\nDelivery Address: " + fullAddress +
                        ", " + city +
                        ", " + postalCode
        );

        // Dynamically add order items to the LinearLayout
        linearLayoutReceiptItems.removeAllViews();
        JSONArray items = order.optJSONArray("items");

        if (items != null) {
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                String itemName = item.optString("product_name", "N/A");
                if (item.has("variant_name") && !item.optString("variant_name").isEmpty() && !item.optString("variant_name").equalsIgnoreCase("null")) {
                    itemName += " (" + item.optString("variant_name") + ")";
                }
                int quantity = item.optInt("quantity", 0);
                BigDecimal unitPrice = new BigDecimal(item.optString("unit_price", "0.00"));
                BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));

                TextView itemTextView = (TextView) LayoutInflater.from(this)
                        .inflate(R.layout.item_receipt_product, linearLayoutReceiptItems, false);
                itemTextView.setText(String.format(Locale.getDefault(), "- %s (x%d) @ LKR %.2f = LKR %.2f",
                        itemName, quantity, unitPrice, itemTotal));
                linearLayoutReceiptItems.addView(itemTextView);
            }
        } else {
            Log.w(TAG, "No 'items' array found in order details for order_id: " + order.optString("order_id"));
            TextView noItemsTextView = new TextView(this);
            noItemsTextView.setText("No items listed for this order.");
            linearLayoutReceiptItems.addView(noItemsTextView);
        }

        BigDecimal subtotal = new BigDecimal(order.optString("sub_total_amount", "0.00"));
        BigDecimal deliveryFee = new BigDecimal(order.optString("delivery_fee", "0.00"));
        BigDecimal total = new BigDecimal(order.optString("total_amount", "0.00"));

        textViewReceiptSubtotal.setText(String.format(Locale.getDefault(), "Subtotal: LKR %.2f", subtotal));
        textViewReceiptDeliveryFee.setText(String.format(Locale.getDefault(), "Delivery Fee: LKR %.2f", deliveryFee));
        textViewReceiptTotal.setText(String.format(Locale.getDefault(), "Total: LKR %.2f", total));
    }

    /**
     * Generates a PDF representation of the current receipt content and saves it.
     */
    private void generateAndSavePdfReceipt() {
        receiptContentLayout.measure(
                View.MeasureSpec.makeMeasureSpec(receiptContentLayout.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        receiptContentLayout.layout(0, 0, receiptContentLayout.getMeasuredWidth(), receiptContentLayout.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(
                receiptContentLayout.getWidth(),
                receiptContentLayout.getHeight(),
                Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        receiptContentLayout.draw(canvas);

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(
                bitmap.getWidth(),
                bitmap.getHeight(),
                1
        ).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        page.getCanvas().drawBitmap(bitmap, 0, 0, null);
        document.finishPage(page);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "Tridots_Receipt_Order_" + orderId + "_" + timeStamp + ".pdf";
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs();
        }
        File file = new File(downloadsDir, fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            document.writeTo(fos);
            Toast.makeText(this, "Receipt downloaded to Downloads folder!", Toast.LENGTH_LONG).show();
            Log.d(TAG, "PDF generated successfully at: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error downloading receipt: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error generating PDF: " + e.getMessage());
        } finally {
            if (document != null) {
                document.close();
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Initiates the sharing of the generated PDF receipt.
     */
    private void sharePdfReceipt() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "Tridots_Receipt_Order_" + orderId + "_" + timeStamp + ".pdf";
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(downloadsDir, fileName);

        if (!file.exists()) {
            Toast.makeText(this, "Generating receipt before sharing...", Toast.LENGTH_SHORT).show();
            generateAndSavePdfReceipt();
            if (!file.exists()) {
                Toast.makeText(this, "Failed to create receipt for sharing. Please try again.", Toast.LENGTH_LONG).show();
                return;
            }
        }

        Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", file);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/pdf");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(shareIntent, "Share Receipt via"));
    }

    /**
     * Checks if the app has the necessary storage permissions.
     */
    private boolean checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                return false;
            }
        }
        return true;
    }

    /**
     * Callback method for the result of requesting permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage permission granted! You can now download and share.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Storage permission denied. Cannot download or share receipt.", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Navigates the user back to the main DashboardActivity.
     */
    private void navigateToDashboard() {
        Intent intent = new Intent(ReceiptActivity.this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
