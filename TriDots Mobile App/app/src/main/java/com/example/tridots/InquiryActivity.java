// InquiryActivity.java
package com.example.tridots;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class InquiryActivity extends AppCompatActivity {

    private TextView productNameTextView;
    private EditText nameEditText, emailEditText, phoneEditText, messageEditText;
    private Button submitButton;
    private RequestQueue requestQueue;
    private String adType;
    private int adId;
    private int loggedInUserId; // To store the logged-in user's ID
    private int listerId; // To store the ad owner's ID (originally listerId)
    private static final String SUBMIT_INQUIRY_URL = "https://lionsgoldencircle.com/Tridots/Api/submit_inquiry.php";
    private static final String PREF_NAME = "user_session";
    private static final String KEY_USER_ID = "user_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiry);

        productNameTextView = findViewById(R.id.productNameTextView);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        messageEditText = findViewById(R.id.messageEditText);
        submitButton = findViewById(R.id.submitButton);
        requestQueue = Volley.newRequestQueue(this);

        // Get data passed from DetailedActivity
        Intent intent = getIntent();
        adType = intent.getStringExtra("ad_type");
        adId = intent.getIntExtra("ad_id", -1);
        String productName = intent.getStringExtra("product_name");
        listerId = intent.getIntExtra("lister_id", -1); // Retrieve lister_id

        Log.d("InquiryActivity", "onCreate() - Ad ID: " + adId + ", Lister ID (Ad Owner ID): " + listerId);

        if (productName != null) {
            productNameTextView.setText("Inquiry for: " + productName);
        }

        // Retrieve the logged-in user ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        loggedInUserId = sharedPreferences.getInt(KEY_USER_ID, -1);
        Log.d("InquiryActivity", "onCreate() - Logged In User ID: " + loggedInUserId);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitInquiry();
            }
        });
    }

    private void submitInquiry() {
        final String inquirerName = nameEditText.getText().toString().trim();
        final String inquirerEmail = emailEditText.getText().toString().trim();
        final String inquirerPhone = phoneEditText.getText().toString().trim();
        final String inquiryMessage = messageEditText.getText().toString().trim();

        if (inquirerName.isEmpty() || inquirerEmail.isEmpty() || inquiryMessage.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (adId != -1 && loggedInUserId != -1 && listerId != -1) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, SUBMIT_INQUIRY_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(InquiryActivity.this, response, Toast.LENGTH_LONG).show();
                            if (response.trim().equals("Inquiry submitted successfully")) {
                                finish(); // Go back to the product details page
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(InquiryActivity.this, "Error submitting inquiry: " + error.getMessage(), Toast.LENGTH_LONG).show();
                            Log.e("InquiryActivity", "Error submitting inquiry: " + error.toString());
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("user_id", String.valueOf(loggedInUserId));
                    params.put("ad_owner_id", String.valueOf(listerId)); // Send listerId as ad_owner_id
                    params.put("ad_type", adType);
                    params.put("ad_id", String.valueOf(adId));
                    params.put("inquirer_name", inquirerName);
                    params.put("inquirer_email", inquirerEmail);
                    params.put("inquirer_phone", inquirerPhone);
                    params.put("inquiry_message", inquiryMessage);
                    return params;
                }
            };
            requestQueue.add(stringRequest);
        } else {
            StringBuilder errorMessage = new StringBuilder("Error: Could not retrieve ");
            if (adId == -1) errorMessage.append("product ID, ");
            if (loggedInUserId == -1) errorMessage.append("user ID, ");
            if (listerId == -1) errorMessage.append("ad owner ID, ");
            errorMessage.delete(errorMessage.length() - 2, errorMessage.length()); // Remove the trailing ", "
            Toast.makeText(this, errorMessage.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}