package com.example.tridots.edit_screens;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.tridots.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ServiceProviderAdEditActivity extends AppCompatActivity {

    private Toolbar toolbarEditServiceProviderAd;
    private ImageView imageViewEditProfilePicture;
    private Button buttonSelectProfilePicture, buttonSaveServiceProviderAd, buttonDeleteServiceProviderAd;
    private EditText editTextEditName, editTextEditDescription, editTextEditContactNumber,
            editTextEditEmailAddress, editTextEditAddress, editTextEditExperienceYears,
            editTextEditQualifications, editTextEditLocation;
    private Spinner spinnerEditServiceCategory;
    private Switch availabilitySwitch;
    private int sellerId;
    private RequestQueue requestQueue;
    private static final String GET_SERVICE_PROVIDER_DETAIL_URL = "https://lionsgoldencircle.com/Tridots/Api/get_service_provider_detail.php";
    private static final String UPDATE_SERVICE_PROVIDER_URL = "https://lionsgoldencircle.com/Tridots/Api/update_service_provider.php";
    private static final String UPLOAD_IMAGE_URL = "https://lionsgoldencircle.com/Tridots/Api/upload_service_image.php";
    private static final String DELETE_SERVICE_PROVIDER_URL = "https://lionsgoldencircle.com/Tridots/Api/delete_ad.php"; // Reusing delete URL
    private static final String GET_SERVICE_CATEGORIES_URL = "https://lionsgoldencircle.com/Tridots/Api/geting_service_categories.php"; // To fetch categories
    private Uri selectedImageUri;
    private Bitmap bitmap;
    private static final int PICK_IMAGE_REQUEST = 2; // Different request code
    private static final int STORAGE_PERMISSION_CODE = 102; // Different permission code
    private String currentProfilePictureUrl;
    private String availabilityStatus;
    private String adType = "Service"; // Hardcoded for this activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_ad_edit);

        toolbarEditServiceProviderAd = findViewById(R.id.toolbarEditServiceProviderAd);
        setSupportActionBar(toolbarEditServiceProviderAd);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Service Profile");

        imageViewEditProfilePicture = findViewById(R.id.imageViewEditProfilePicture);
        buttonSelectProfilePicture = findViewById(R.id.buttonSelectProfilePicture);
        buttonSaveServiceProviderAd = findViewById(R.id.buttonSaveServiceProviderAd);
        buttonDeleteServiceProviderAd = findViewById(R.id.buttonDeleteServiceProviderAd);
        editTextEditName = findViewById(R.id.editTextEditName);
        editTextEditDescription = findViewById(R.id.editTextEditDescription);
        editTextEditContactNumber = findViewById(R.id.editTextEditContactNumber);
        editTextEditEmailAddress = findViewById(R.id.editTextEditEmailAddress);
        editTextEditAddress = findViewById(R.id.editTextEditAddress);
        editTextEditExperienceYears = findViewById(R.id.editTextEditExperienceYears);
        editTextEditQualifications = findViewById(R.id.editTextEditQualifications);
        editTextEditLocation = findViewById(R.id.editTextEditLocation);
        spinnerEditServiceCategory = findViewById(R.id.spinnerEditServiceCategory);
        availabilitySwitch = findViewById(R.id.availabilitySwitch);

        requestQueue = Volley.newRequestQueue(this);

        sellerId = getIntent().getIntExtra("adId", -1);
        if (sellerId != -1) {
            fetchServiceProviderDetails(sellerId);
            fetchServiceCategories(); // Load service categories for the spinner
        } else {
            Toast.makeText(this, "Error: Seller ID not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        availabilitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                availabilityStatus = isChecked ? "Available" : "Not Available";
                availabilitySwitch.setText(availabilityStatus);
            }
        });

        buttonSelectProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkStoragePermission();
            }
        });

        buttonSaveServiceProviderAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedImageUri != null) {
                    uploadImage();
                } else {
                    updateServiceProviderAd(currentProfilePictureUrl);
                }
            }
        });

        buttonDeleteServiceProviderAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete your service profile?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteAd(sellerId, adType);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void deleteAd(final int adIdToDelete, final String adType) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DELETE_SERVICE_PROVIDER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Delete Service Ad Response", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            Toast.makeText(ServiceProviderAdEditActivity.this, message, Toast.LENGTH_SHORT).show();
                            if (status.equals("success")) {
                                finish();
                            } else {
                                Toast.makeText(ServiceProviderAdEditActivity.this, "Delete failed: " + message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ServiceProviderAdEditActivity.this, "Error parsing delete response: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = (error != null && error.getMessage() != null) ? error.getMessage() : (error != null ? error.toString() : "Unknown error occurred");
                        Toast.makeText(ServiceProviderAdEditActivity.this, "Error deleting profile: " + errorMessage, Toast.LENGTH_LONG).show();
                        Log.e("Delete Service Ad Error", "Error deleting profile: " + (error != null ? error.toString() : "VolleyError is null"));
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("seller_id", String.valueOf(adIdToDelete)); // Correct ID key for Service Provider
                params.put("ad_type", adType);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showPermissionRationaleDialog();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                }
            } else {
                openFileChooser();
            }
        } else {
            openFileChooser();
        }
    }

    private void showPermissionRationaleDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Storage Permission Required")
                .setMessage("This app needs storage permission to select your profile picture.")
                .setPositiveButton("Allow", (dialog, which) -> {
                    ActivityCompat.requestPermissions(ServiceProviderAdEditActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                    Toast.makeText(ServiceProviderAdEditActivity.this, "Permission denied. You won't be able to select a profile picture.", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFileChooser();
            } else {
                Toast.makeText(this, "Permission denied. You won't be able to select a profile picture.", Toast.LENGTH_SHORT).show();
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showGoToSettingsDialog();
                }
            }
        }
    }

    private void showGoToSettingsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Storage Permission Required")
                .setMessage("Storage permission is disabled. Please go to Settings > Apps > TriDots > Permissions and allow storage permission to select a profile picture.")
                .setPositiveButton("Go to Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setCancelable(false)
                .show();
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                bitmap = rotateImageIfRequired(bitmap, selectedImageUri); // Rotate the bitmap if needed
                imageViewEditProfilePicture.setImageBitmap(bitmap); // Set the rotated bitmap to the ImageView
            } catch (IOException e) {
                e.printStackTrace();
                Glide.with(this)
                        .load(selectedImageUri)
                        .placeholder(R.drawable.profile_placeholder)
                        .error(R.drawable.image_error_placeholder)
                        .into(imageViewEditProfilePicture); // Fallback if bitmap loading fails
            }
        }
    }

    private Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {
        InputStream input = getContentResolver().openInputStream(selectedImage);
        ExifInterface ei = new ExifInterface(input);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        input.close();

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotate(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotate(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotate(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotate(Bitmap img, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    private void fetchServiceProviderDetails(int sellerId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_SERVICE_PROVIDER_DETAIL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Fetch Service Details", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            if (status.equals("success")) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                populateServiceProviderDetails(data);
                            } else {
                                Toast.makeText(ServiceProviderAdEditActivity.this, "Failed to fetch profile details: " + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ServiceProviderAdEditActivity.this, "Error parsing service profile details JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ServiceProviderAdEditActivity.this, "Error fetching service profile details: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("seller_id", String.valueOf(sellerId));
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void fetchServiceCategories() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_SERVICE_CATEGORIES_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Fetch Categories", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            if (status.equals("success")) {
                                JSONArray categoriesArray = jsonObject.getJSONArray("categories");
                                String[] categories = new String[categoriesArray.length()];
                                for (int i = 0; i < categoriesArray.length(); i++) {
                                    categories[i] = categoriesArray.getString(i);
                                }
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(ServiceProviderAdEditActivity.this, android.R.layout.simple_spinner_dropdown_item, categories);
                                spinnerEditServiceCategory.setAdapter(adapter);
                            } else {
                                Toast.makeText(ServiceProviderAdEditActivity.this, "Failed to fetch service categories: " + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ServiceProviderAdEditActivity.this, "Error parsing service categories JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ServiceProviderAdEditActivity.this, "Error fetching service categories: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(stringRequest);
    }

    private void populateServiceProviderDetails(JSONObject data) throws JSONException {
        editTextEditName.setText(data.getString("name"));
        editTextEditDescription.setText(data.getString("description"));
        editTextEditContactNumber.setText(data.getString("contact_number"));
        editTextEditEmailAddress.setText(data.getString("email_address"));
        editTextEditAddress.setText(data.getString("address"));
        editTextEditExperienceYears.setText(String.valueOf(data.getInt("experience_years")));
        editTextEditQualifications.setText(data.getString("qualifications"));
        editTextEditLocation.setText(data.getString("location"));

        currentProfilePictureUrl = data.getString("profile_picture");
        if (currentProfilePictureUrl != null && !currentProfilePictureUrl.isEmpty()) {
            Glide.with(this)
                    .load(currentProfilePictureUrl)
                    .placeholder(R.drawable.profile_placeholder)
                    .error(R.drawable.image_error_placeholder)
                    .into(imageViewEditProfilePicture);
        }

        String fetchedAvailability = data.getString("availability_status");
        availabilityStatus = fetchedAvailability;
        availabilitySwitch.setChecked(availabilityStatus.equalsIgnoreCase("Available"));
        availabilitySwitch.setText(availabilityStatus);

// Set the spinner selection if the category ID is available
        if (data.has("service_category_id")) {
            int categoryId = data.getInt("service_category_id");
// We need to find the corresponding category name in the spinner's adapter
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerEditServiceCategory.getAdapter();
            if (adapter != null) {
// Assuming your get_service_categories.php returns category names
// You might need to adjust this based on your actual response
                String categoryNameToSelect = "";
// You might need to make another API call to get the category name by ID
// For now, we'll assume the ID directly corresponds to a position (not ideal)
                if (categoryId > 0 && categoryId <= adapter.getCount()) {
                    spinnerEditServiceCategory.setSelection(categoryId - 1); // Adjust for 0-based indexing
                }
            }
        }
    }

    private void uploadImage() {
        if (bitmap == null) {
            Toast.makeText(ServiceProviderAdEditActivity.this, "Please select a profile picture to upload.", Toast.LENGTH_SHORT).show();
            return;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_IMAGE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Image Upload Response", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            if (status.equals("success")) {
                                String imageUrl = jsonObject.getString("image_url");
                                Toast.makeText(ServiceProviderAdEditActivity.this, "Profile picture uploaded successfully.", Toast.LENGTH_SHORT).show();
                                updateServiceProviderAd(imageUrl);
                            } else {
                                Toast.makeText(ServiceProviderAdEditActivity.this, "Profile picture upload failed: " + message, Toast.LENGTH_SHORT).show();
                                updateServiceProviderAd(currentProfilePictureUrl); // Fallback
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ServiceProviderAdEditActivity.this, "Error parsing image upload response", Toast.LENGTH_SHORT).show();
                            updateServiceProviderAd(currentProfilePictureUrl); // Fallback
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ServiceProviderAdEditActivity.this, "Error uploading profile picture: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        updateServiceProviderAd(currentProfilePictureUrl); // Fallback
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("image", imageToString(bitmap));
                params.put("name", "profile_" + System.currentTimeMillis() + ".jpg");
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private String imageToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private void updateServiceProviderAd(String profilePictureUrl) {
        final String name = editTextEditName.getText().toString().trim();
        final String description = editTextEditDescription.getText().toString().trim();
        final String contactNumber = editTextEditContactNumber.getText().toString().trim();
        final String emailAddress = editTextEditEmailAddress.getText().toString().trim();
        final String address = editTextEditAddress.getText().toString().trim();
        final String experienceYears = editTextEditExperienceYears.getText().toString().trim();
        final String qualifications = editTextEditQualifications.getText().toString().trim();
        final String location = editTextEditLocation.getText().toString().trim();
        final String serviceCategory = spinnerEditServiceCategory.getSelectedItem().toString(); // Get selected category name

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPDATE_SERVICE_PROVIDER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Update Service Ad", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            Toast.makeText(ServiceProviderAdEditActivity.this, message, Toast.LENGTH_SHORT).show();
                            if (status.equals("success")) {
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ServiceProviderAdEditActivity.this, "Error parsing update response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ServiceProviderAdEditActivity.this, "Error updating service profile: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("seller_id", String.valueOf(sellerId));
                params.put("name", name);
                params.put("description", description);
                params.put("contact_number", contactNumber);
                params.put("email_address", emailAddress);
                params.put("address", address);
                params.put("experience_years", experienceYears);
                params.put("qualifications", qualifications);
                params.put("location", location);
                params.put("availability_status", availabilityStatus);
                params.put("profile_picture", profilePictureUrl);
                params.put("service_category", serviceCategory); // Send category name
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
