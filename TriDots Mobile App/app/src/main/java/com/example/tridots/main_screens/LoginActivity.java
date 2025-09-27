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

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextLogin, editTextPassword;
    private Button buttonLogin;
    private RequestQueue requestQueue;
    private static final String LOGIN_URL = "https://lionsgoldencircle.com/Tridots/Api/login.php";
    private static final String PREF_NAME = "user_session";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PROFILE_PICTURE = "profile_picture_path";
    private static final String KEY_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id"; // Define KEY_USER_ID

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Check if the user is already logged in
        if (sharedPreferences.getBoolean(KEY_LOGGED_IN, false)) {
            // User is logged in, navigate directly to HomeActivity
            String savedUsername = sharedPreferences.getString(KEY_USERNAME, "");
            String savedProfilePicture = sharedPreferences.getString(KEY_PROFILE_PICTURE, "");
            navigateToHome(savedUsername, savedProfilePicture);
            finish(); // Prevent going back to LoginActivity
            return;
        }

        setContentView(R.layout.activity_login);

        editTextLogin = findViewById(R.id.edit_text_email);
        editTextPassword = findViewById(R.id.edit_text_password);
        buttonLogin = findViewById(R.id.button_login);

        requestQueue = Volley.newRequestQueue(this);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        final String loginIdentifier = editTextLogin.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        if (loginIdentifier.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter username/email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Login Response", response);
                        if (response.trim().startsWith("success:")) {
                            String[] parts = response.split(":");
                            if (parts.length == 4) { // Expecting 4 parts now
                                String loggedInUsername = parts[1];
                                String profilePicturePath = parts[2];
                                String loggedInUserIdStr = parts[3]; // Get the user ID as a string

                                try {
                                    int loggedInUserId = Integer.parseInt(loggedInUserIdStr);

                                    // Save login data to SharedPreferences
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean(KEY_LOGGED_IN, true);
                                    editor.putString(KEY_USERNAME, loggedInUsername);
                                    editor.putString(KEY_PROFILE_PICTURE, profilePicturePath);
                                    editor.putInt(KEY_USER_ID, loggedInUserId); // Save the user ID
                                    editor.apply(); // Or editor.commit()

                                    Log.d("LoginActivity", "User ID saved: " + loggedInUserId);

                                    // Verify if saved correctly (for debugging)
                                    SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                                    Log.d("LoginActivity", "Verified saved User ID: " + prefs.getInt(KEY_USER_ID, -1));

                                    // Navigate to HomeActivity
                                    navigateToHome(loggedInUsername, profilePicturePath);
                                    finish();
                                } catch (NumberFormatException e) {
                                    Toast.makeText(LoginActivity.this, "Login successful, but invalid user ID format", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "Login successful, but data format error", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Login failed: " + response, Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, "Login error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("login_identifier", loginIdentifier);
                params.put("password", password);
                params.put("action", "login");
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void navigateToHome(String username, String profilePicturePath) {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class); // Changed to HomeActivity
        intent.putExtra("username", username);
        intent.putExtra("profile_picture_path", profilePicturePath);
        startActivity(intent);
    }
}