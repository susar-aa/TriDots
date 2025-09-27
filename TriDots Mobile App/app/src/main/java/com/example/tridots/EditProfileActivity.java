package com.example.tridots;



import android.Manifest;

import android.content.DialogInterface;

import android.content.Intent;

import android.content.SharedPreferences;

import android.content.pm.PackageManager;

import android.graphics.Bitmap;

import android.graphics.ImageDecoder;

import android.net.Uri;

import android.os.Build;

import android.os.Bundle;

import android.provider.MediaStore;

import android.provider.Settings;

import android.text.TextUtils;

import android.util.Base64;

import android.util.Log;

import android.view.View;

import android.widget.Button;

import android.widget.ImageView;

import android.widget.TextView; // Added for displaying user type

import android.widget.Toast;



import androidx.activity.result.ActivityResultLauncher;

import androidx.activity.result.contract.ActivityResultContracts;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import androidx.core.content.ContextCompat;



import com.android.volley.AuthFailureError;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;

import com.android.volley.RequestQueue;

import com.android.volley.Response;

import com.android.volley.VolleyError;

import com.android.volley.toolbox.StringRequest;

import com.android.volley.toolbox.Volley;

import com.example.tridots.R;

import com.google.android.material.textfield.TextInputEditText;

import com.google.android.material.textfield.TextInputLayout;

import com.squareup.picasso.Picasso;



import org.json.JSONException;

import org.json.JSONObject;



import java.io.ByteArrayOutputStream;

import java.io.IOException;

import java.util.HashMap;

import java.util.Map;

import java.util.Objects;



public class EditProfileActivity extends AppCompatActivity {



    private static final String TAG = "EditProfileActivity";

    private static final String GET_PROFILE_URL = "https://lionsgoldencircle.com/Tridots/Api/get_profile.php";

    private static final String UPDATE_PROFILE_URL = "https://lionsgoldencircle.com/Tridots/Api/update_profile.php";

// BASE_IMAGE_URL might not be needed if DB stores full URLs for display.

// private static final String BASE_IMAGE_URL = "https://lionsgoldencircle.com/Tridots/";





    private ImageView imageEditProfilePicture;

    private Button buttonChangePicture, buttonSaveProfile;

    private TextInputEditText editTextUsername, editTextEmail, editTextContact, editTextNic, editTextAddress, editTextBusinessName;

    private TextInputLayout layoutBusinessName;

    private TextView textViewUserTypeValue; // For displaying non-editable user type



    private RequestQueue requestQueue;

    private SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "user_session";

    private static final String KEY_USERNAME = "username";



    private String currentUsernameToEdit; // Username passed from ProfileActivity

    private String fetchedUserType; // To store the non-editable user type

    private String selectedProfilePictureBase64 = null; // To store base64 of new image

    private Uri selectedImageUri = null;



    private ActivityResultLauncher<String> requestPermissionLauncher;

    private ActivityResultLauncher<Intent> pickImageLauncher;



    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_profile);



        if (getSupportActionBar() != null) {

            getSupportActionBar().setTitle("Edit Profile");

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }



        imageEditProfilePicture = findViewById(R.id.image_edit_profile_picture);

        buttonChangePicture = findViewById(R.id.button_change_picture);

        buttonSaveProfile = findViewById(R.id.button_save_profile);

        editTextUsername = findViewById(R.id.edit_text_username);

        editTextEmail = findViewById(R.id.edit_text_email);

        editTextContact = findViewById(R.id.edit_text_contact);

        editTextNic = findViewById(R.id.edit_text_nic);

        editTextAddress = findViewById(R.id.edit_text_address);

        editTextBusinessName = findViewById(R.id.edit_text_business_name);

        layoutBusinessName = findViewById(R.id.layout_business_name);

        textViewUserTypeValue = findViewById(R.id.text_view_user_type_value); // Initialize TextView



        requestQueue = Volley.newRequestQueue(this);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);



        currentUsernameToEdit = getIntent().getStringExtra("USERNAME_TO_EDIT");

        if (currentUsernameToEdit == null || currentUsernameToEdit.isEmpty()) {

            Toast.makeText(this, "Error: User not identified.", Toast.LENGTH_LONG).show();

            finish();

            return;

        }



        initializePermissionLaunchers();

        fetchUserProfileData(currentUsernameToEdit);



        buttonChangePicture.setOnClickListener(v -> checkStoragePermissionAndPickImage());

        buttonSaveProfile.setOnClickListener(v -> attemptSaveProfile());

    }



    private void initializePermissionLaunchers() {

        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {

            if (isGranted) {

                openImagePicker();

            } else {

                Toast.makeText(this, "Storage permission is required to select an image.", Toast.LENGTH_LONG).show();

                showPermissionRationaleDialog();

            }

        });



        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

            if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {

                selectedImageUri = result.getData().getData();

                try {

                    Bitmap bitmap;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

                        bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), selectedImageUri));

                    } else {

                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);

                    }

                    imageEditProfilePicture.setImageBitmap(bitmap);

                    selectedProfilePictureBase64 = bitmapToBase64(bitmap);

                } catch (IOException e) {

                    Log.e(TAG, "Error loading image: " + e.getMessage());

                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();

                    selectedProfilePictureBase64 = null;

                }

            }

        });

    }



    private void checkStoragePermissionAndPickImage() {

        String permission;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+

            permission = Manifest.permission.READ_MEDIA_IMAGES;

        } else { // Older versions

            permission = Manifest.permission.READ_EXTERNAL_STORAGE;

        }



        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {

            openImagePicker();

        } else if (shouldShowRequestPermissionRationale(permission)) {

            showPermissionRationaleDialog();

        } else {

            requestPermissionLauncher.launch(permission);

        }

    }



    private void showPermissionRationaleDialog() {

        new AlertDialog.Builder(this)

                .setTitle("Permission Needed")

                .setMessage("This permission is needed to access your gallery for profile picture selection. Please grant the permission in app settings.")

                .setPositiveButton("Go to Settings", (dialog, which) -> {

                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

                    Uri uri = Uri.fromParts("package", getPackageName(), null);

                    intent.setData(uri);

                    startActivity(intent);

                })

                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())

                .create().show();

    }



    private void openImagePicker() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        pickImageLauncher.launch(intent);

    }



    private String bitmapToBase64(Bitmap bitmap) {
        if (bitmap == null) return null;

        // First compress the bitmap to reduce size
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // Resize the bitmap if it's too large
        int maxDimension = 800; // Maximum width or height
        if (bitmap.getWidth() > maxDimension || bitmap.getHeight() > maxDimension) {
            float scale = Math.min(
                    (float) maxDimension / bitmap.getWidth(),
                    (float) maxDimension / bitmap.getHeight()
            );
            int width = Math.round(bitmap.getWidth() * scale);
            int height = Math.round(bitmap.getHeight() * scale);
            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
        }

        // Compress with 70% quality
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);

        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }





    private void fetchUserProfileData(String username) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_PROFILE_URL,

                response -> {

                    Log.d(TAG, "Fetch Profile Response: " + response);

                    try {

                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getString("status").equals("success")) {

                            JSONObject userData = jsonObject.getJSONObject("user_data");

                            editTextUsername.setText(userData.getString("username"));

                            editTextEmail.setText(userData.getString("email_address"));

                            editTextContact.setText(userData.optString("contact_number"));

                            editTextNic.setText(userData.optString("nic_number"));

                            editTextAddress.setText(userData.optString("address"));



                            fetchedUserType = userData.getString("user_type"); // Store fetched user type

                            textViewUserTypeValue.setText(fetchedUserType); // Display it



                            if ("Business".equalsIgnoreCase(fetchedUserType)) {

                                layoutBusinessName.setVisibility(View.VISIBLE);

                                editTextBusinessName.setText(userData.optString("business_name"));

                            } else {

                                layoutBusinessName.setVisibility(View.GONE);

                            }



                            String profilePicturePath = userData.optString("profile_picture");

// If DB stores full URL, Picasso loads it directly

                            if (profilePicturePath != null && !profilePicturePath.isEmpty() && !profilePicturePath.equals("null") && profilePicturePath.startsWith("http")) {

                                Picasso.get().load(profilePicturePath)

                                        .placeholder(R.drawable.profile_placeholder)

                                        .error(R.drawable.profile_placeholder)

                                        .into(imageEditProfilePicture);

                            } else {

                                imageEditProfilePicture.setImageResource(R.drawable.profile_placeholder);

                            }

                        } else {

                            Toast.makeText(EditProfileActivity.this, "Failed to fetch profile: " + jsonObject.getString("message"), Toast.LENGTH_LONG).show();

                        }

                    } catch (JSONException e) {

                        Log.e(TAG, "JSON parsing error: " + e.getMessage());

                        Toast.makeText(EditProfileActivity.this, "Error parsing profile data", Toast.LENGTH_LONG).show();

                    }

                },

                error -> {

                    Log.e(TAG, "Volley error fetching profile: " + error.toString());

                    Toast.makeText(EditProfileActivity.this, "Error fetching profile: " + error.getMessage(), Toast.LENGTH_LONG).show();

                }) {

            @Override

            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();

                params.put("username", username);

                params.put("action", "get_profile");

                return params;

            }

        };

        requestQueue.add(stringRequest);

    }



    private void attemptSaveProfile() {

        String newUsername = Objects.requireNonNull(editTextUsername.getText()).toString().trim();

        String email = Objects.requireNonNull(editTextEmail.getText()).toString().trim();

        String contact = Objects.requireNonNull(editTextContact.getText()).toString().trim();

        String nic = Objects.requireNonNull(editTextNic.getText()).toString().trim();

        String address = Objects.requireNonNull(editTextAddress.getText()).toString().trim();

        String businessName = Objects.requireNonNull(editTextBusinessName.getText()).toString().trim();



        if (TextUtils.isEmpty(newUsername)) {

            editTextUsername.setError("Username is required");

            editTextUsername.requestFocus();

            return;

        }

        if (TextUtils.isEmpty(email)) {

            editTextEmail.setError("Email is required");

            editTextEmail.requestFocus();

            return;

        }



        updateProfileOnServer(newUsername, email, contact, nic, address, businessName);

    }



    private void updateProfileOnServer(final String newUsername, final String email, final String contact,
                                       final String nic, final String address, final String businessName) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPDATE_PROFILE_URL,
                response -> {
                    Log.d(TAG, "Update Profile Response: " + response);
                    try {
                        // Remove any HTML tags or warnings before parsing JSON
                        String cleanResponse = response.replaceAll("<[^>]*>", "").trim();
                        JSONObject jsonObject = new JSONObject(cleanResponse);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        Toast.makeText(EditProfileActivity.this, message, Toast.LENGTH_LONG).show();

                        if (status.equals("success")) {
                            if (!currentUsernameToEdit.equals(newUsername)) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(KEY_USERNAME, newUsername);
                                editor.apply();
                            }
                            setResult(RESULT_OK);
                            finish();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error on update: " + e.getMessage());
                        Toast.makeText(EditProfileActivity.this, "Error parsing server response.", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Volley error updating profile: " + error.toString());
                    if (error.networkResponse != null) {
                        String errorResponse = new String(error.networkResponse.data);
                        Log.e(TAG, "Error Response: " + errorResponse);
                        try {
                            // Try to parse error response if it's JSON
                            JSONObject errorObj = new JSONObject(errorResponse);
                            String errorMsg = errorObj.getString("message");
                            Toast.makeText(EditProfileActivity.this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            Toast.makeText(EditProfileActivity.this, "Error updating profile", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("current_username", currentUsernameToEdit);
                params.put("new_username", newUsername);
                params.put("email_address", email);
                params.put("contact_number", contact);
                params.put("nic_number", nic);
                params.put("address", address);

                if ("Business".equalsIgnoreCase(fetchedUserType)) {
                    params.put("business_name", businessName);
                } else {
                    params.put("business_name", "");
                }

                if (selectedProfilePictureBase64 != null) {
                    params.put("profile_picture_base64", selectedProfilePictureBase64);
                }
                params.put("action", "update_profile");
                Log.d(TAG, "Params: " + params.toString());
                return params;
            }
        };

        // Set retry policy
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000, // 10 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }



    @Override

    public boolean onSupportNavigateUp() {

        finish();

        return true;

    }

}


