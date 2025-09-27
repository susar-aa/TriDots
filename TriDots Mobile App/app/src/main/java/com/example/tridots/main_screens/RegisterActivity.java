package com.example.tridots.main_screens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tridots.R;
import com.example.tridots.UploadProfilePictureActivity;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextEmail, editTextPassword, editTextContact, editTextNic, editTextAddress;
    private Button buttonRegister;
    private RequestQueue requestQueue;
    private static final String REGISTER_URL = "https://lionsgoldencircle.com/Tridots/Api/register.php"; // Your PHP file path

    // SharedPreferences constants (same as LoginActivity for consistency)
    private static final String PREF_NAME = "user_session";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PROFILE_PICTURE = "profile_picture_path"; // Will be empty initially for new registration
    private static final String KEY_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE); // Initialize SharedPreferences

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextContact = findViewById(R.id.editTextContact);
        editTextNic = findViewById(R.id.editTextNic);
        editTextAddress = findViewById(R.id.editTextAddress);
        buttonRegister = findViewById(R.id.buttonRegister);

        requestQueue = Volley.newRequestQueue(this);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        final String username = editTextUsername.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String contact = editTextContact.getText().toString().trim();
        final String nic = editTextNic.getText().toString().trim();
        final String address = editTextAddress.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || contact.isEmpty() || nic.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Register Response", "Raw response: '" + response + "'"); // Log the raw response

                        // Handle the response from the PHP script
                        if (response.trim().startsWith("success:")) {
                            Log.d("Register Response", "Response starts with 'success:'. Proceeding to parse.");
                            String[] parts = response.trim().split(":");
                            Log.d("Register Response", "Split parts length: " + parts.length);

                            if (parts.length == 3) { // Expecting success:username:user_id
                                String registeredUsername = parts[1];
                                String registeredUserIdStr = parts[2];

                                try {
                                    int registeredUserId = Integer.parseInt(registeredUserIdStr);

                                    // Save registered user data to SharedPreferences
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean(KEY_LOGGED_IN, true);
                                    editor.putString(KEY_USERNAME, registeredUsername);
                                    editor.putInt(KEY_USER_ID, registeredUserId);
                                    // For new registration, profile picture path is initially empty or default
                                    editor.putString(KEY_PROFILE_PICTURE, "");
                                    editor.apply();

                                    Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_LONG).show();
                                    Log.d("RegisterActivity", "User ID saved: " + registeredUserId);
                                    Log.d("RegisterActivity", "Username saved: " + registeredUsername);


                                    // Navigate to the profile picture upload screen
                                    Intent intent = new Intent(RegisterActivity.this, UploadProfilePictureActivity.class);
                                    intent.putExtra("username", registeredUsername);
                                    intent.putExtra("user_id", registeredUserId); // Pass user ID to upload activity
                                    startActivity(intent);
                                    finish(); // Close the registration activity
                                } catch (NumberFormatException e) {
                                    Toast.makeText(RegisterActivity.this, "Registration successful, but invalid user ID format from server", Toast.LENGTH_LONG).show();
                                    Log.e("RegisterActivity", "Error parsing user ID from response: " + registeredUserIdStr + ". Error: " + e.getMessage());
                                }
                            } else {
                                Toast.makeText(RegisterActivity.this, "Registration successful, but server response format error.", Toast.LENGTH_LONG).show();
                                Log.e("RegisterActivity", "Unexpected response format. Expected 'success:username:user_id', got: " + response);
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration Failed: " + response, Toast.LENGTH_LONG).show();
                            Log.d("Register Response", "Response did NOT start with 'success:'. Full response: " + response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegisterActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("RegisterActivity", "Volley Error during registration: " + error.getMessage(), error);
                        if (error.networkResponse != null) {
                            Log.e("RegisterActivity", "Network response data: " + new String(error.networkResponse.data));
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("email_address", email);
                params.put("password_hash", password); // In a real application, hash this password securely!
                params.put("contact_number", contact);
                params.put("nic_number", nic);
                params.put("address", address);
                params.put("user_type", "Personal"); // Set automatically
                params.put("verification_status", "Not Verified"); // Set automatically
                params.put("is_active", "Active"); // Set automatically
                params.put("action", "register"); // Add an action identifier for your PHP script
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }
}
