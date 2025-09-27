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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class RentingAdEditActivity extends AppCompatActivity {

    private Toolbar toolbarEditRentingAd;
    private ImageView imageViewEditProduct;
    private Button buttonSelectImage, buttonSaveRentingAd, buttonDeleteRentingAd;
    private EditText editTextEditProductName, editTextEditBrand, editTextEditModel,
            editTextEditDescription, editTextEditKeywords, editTextEditPricePerHour,
            editTextEditPricePerDay, editTextEditLocation;
    private Switch availabilitySwitch;
    private int adId;
    private RequestQueue requestQueue;
    private static final String GET_RENTING_DETAIL_URL = "https://lionsgoldencircle.com/Tridots/Api/get_renting_detail.php";
    private static final String UPDATE_RENTING_URL = "https://lionsgoldencircle.com/Tridots/Api/update_renting.php";
    private static final String UPLOAD_IMAGE_URL = "https://lionsgoldencircle.com/Tridots/Api/upload_renting_image.php";
    private static final String DELETE_RENTING_URL = "https://lionsgoldencircle.com/Tridots/Api/delete_ad.php";
    private Uri selectedImageUri;
    private Bitmap bitmap;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 101; // Different code for clarity
    private String currentImageUrl;
    private String availabilityStatus;
    private String adType = "Renting"; // Hardcoded for this activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renting_ad_edit);

        toolbarEditRentingAd = findViewById(R.id.toolbarEditRentingAd);
        setSupportActionBar(toolbarEditRentingAd);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Renting Ad");

        imageViewEditProduct = findViewById(R.id.imageViewEditProduct);
        buttonSelectImage = findViewById(R.id.buttonSelectImage);
        buttonSaveRentingAd = findViewById(R.id.buttonSaveRentingAd);
        buttonDeleteRentingAd = findViewById(R.id.buttonDeleteRentingAd);
        editTextEditProductName = findViewById(R.id.editTextEditProductName);
        editTextEditBrand = findViewById(R.id.editTextEditBrand);
        editTextEditModel = findViewById(R.id.editTextEditModel);
        editTextEditDescription = findViewById(R.id.editTextEditDescription);
        editTextEditKeywords = findViewById(R.id.editTextEditKeywords);
        editTextEditPricePerHour = findViewById(R.id.editTextEditPricePerHour);
        editTextEditPricePerDay = findViewById(R.id.editTextEditPricePerDay);
        editTextEditLocation = findViewById(R.id.editTextEditLocation);
        availabilitySwitch = findViewById(R.id.availabilitySwitch);

        requestQueue = Volley.newRequestQueue(this);

        adId = getIntent().getIntExtra("adId", -1);
        if (adId != -1) {
            fetchRentingDetails(adId);
        } else {
            Toast.makeText(this, "Error: Ad ID not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        availabilitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                availabilityStatus = isChecked ? "Available" : "Not Available";
                availabilitySwitch.setText(availabilityStatus);
            }
        });

        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkStoragePermission();
            }
        });

        buttonSaveRentingAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedImageUri != null) {
                    uploadImage();
                } else {
                    updateRentingAd(currentImageUrl);
                }
            }
        });

        buttonDeleteRentingAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this renting ad?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteAd(adId, adType);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void deleteAd(final int adIdToDelete, final String adType) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DELETE_RENTING_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Delete Renting Ad Response", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            Toast.makeText(RentingAdEditActivity.this, message, Toast.LENGTH_SHORT).show();
                            if (status.equals("success")) {
                                finish();
                            } else {
                                Toast.makeText(RentingAdEditActivity.this, "Delete failed: " + message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RentingAdEditActivity.this, "Error parsing delete response: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = (error != null && error.getMessage() != null) ? error.getMessage() : (error != null ? error.toString() : "Unknown error occurred");
                        Toast.makeText(RentingAdEditActivity.this, "Error deleting ad: " + errorMessage, Toast.LENGTH_LONG).show();
                        Log.e("Delete Renting Ad Error", "Error deleting ad: " + (error != null ? error.toString() : "VolleyError is null"));
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("rent_id", String.valueOf(adIdToDelete)); // Correct ID key for Renting
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
                .setMessage("This app needs storage permission to select an image for your ad.")
                .setPositiveButton("Allow", (dialog, which) -> {
                    ActivityCompat.requestPermissions(RentingAdEditActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                    Toast.makeText(RentingAdEditActivity.this, "Permission denied. You won't be able to select an image.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "Permission denied. You won't be able to select an image.", Toast.LENGTH_SHORT).show();
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showGoToSettingsDialog();
                }
            }
        }
    }

    private void showGoToSettingsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Storage Permission Required")
                .setMessage("Storage permission is disabled. Please go to Settings > Apps > TriDots > Permissions and allow storage permission to select an image.")
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
                imageViewEditProduct.setImageBitmap(bitmap); // Set the rotated bitmap to the ImageView
            } catch (IOException e) {
                e.printStackTrace();
                Glide.with(this)
                        .load(selectedImageUri)
                        .placeholder(R.drawable.ad_placeholder)
                        .error(R.drawable.image_error_placeholder)
                        .into(imageViewEditProduct); // Fallback if bitmap loading fails
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

    private void fetchRentingDetails(int rentId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_RENTING_DETAIL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Fetch Renting Details", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            if (status.equals("success")) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                populateRentingDetails(data);
                            } else {
                                Toast.makeText(RentingAdEditActivity.this, "Failed to fetch details: " + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RentingAdEditActivity.this, "Error parsing renting details JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RentingAdEditActivity.this, "Error fetching renting details: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(rentId));
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void populateRentingDetails(JSONObject data) throws JSONException {
        editTextEditProductName.setText(data.getString("product_name"));
        editTextEditBrand.setText(data.getString("brand"));
        editTextEditModel.setText(data.getString("model"));
        editTextEditDescription.setText(data.getString("product_description"));
        editTextEditKeywords.setText(data.getString("keywords"));
        editTextEditPricePerHour.setText(data.getString("price_per_hour"));
        editTextEditPricePerDay.setText(data.getString("price_per_day"));
        editTextEditLocation.setText(data.getString("product_location"));

        currentImageUrl = data.getString("product_images");
        if (currentImageUrl != null && !currentImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(currentImageUrl.split(",")[0].trim()) // Assuming first image
                    .placeholder(R.drawable.ad_placeholder)
                    .error(R.drawable.image_error_placeholder)
                    .into(imageViewEditProduct);
        }

        String fetchedAvailability = data.getString("availability_status");
        availabilityStatus = fetchedAvailability;
        availabilitySwitch.setChecked(fetchedAvailability.equalsIgnoreCase("Available"));
        availabilitySwitch.setText(availabilityStatus);
    }

    private void uploadImage() {
        if (bitmap == null) {
            Toast.makeText(RentingAdEditActivity.this, "Please select an image to upload.", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(RentingAdEditActivity.this, "Image uploaded successfully.", Toast.LENGTH_SHORT).show();
                                updateRentingAd(imageUrl);
                            } else {
                                Toast.makeText(RentingAdEditActivity.this, "Image upload failed: " + message, Toast.LENGTH_SHORT).show();
                                updateRentingAd(currentImageUrl); // Fallback to current
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RentingAdEditActivity.this, "Error parsing image upload response", Toast.LENGTH_SHORT).show();
                            updateRentingAd(currentImageUrl); // Fallback on error
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RentingAdEditActivity.this, "Error uploading image: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        updateRentingAd(currentImageUrl); // Fallback on error
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("image", imageToString(bitmap));
                params.put("name", "renting_image_" + System.currentTimeMillis() + ".jpg");
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

    private void updateRentingAd(String imageUrl) {
        final String productName = editTextEditProductName.getText().toString().trim();
        final String brand = editTextEditBrand.getText().toString().trim();
        final String model = editTextEditModel.getText().toString().trim();
        final String description = editTextEditDescription.getText().toString().trim();
        final String keywords = editTextEditKeywords.getText().toString().trim();
        final String pricePerHour = editTextEditPricePerHour.getText().toString().trim();
        final String pricePerDay = editTextEditPricePerDay.getText().toString().trim();
        final String location = editTextEditLocation.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPDATE_RENTING_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Update Renting Ad", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            Toast.makeText(RentingAdEditActivity.this, message, Toast.LENGTH_SHORT).show();
                            if (status.equals("success")) {
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RentingAdEditActivity.this, "Error parsing update response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RentingAdEditActivity.this, "Error updating renting ad: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("rent_id", String.valueOf(adId));
                params.put("product_name", productName);
                params.put("brand", brand);
                params.put("model", model);
                params.put("product_description", description);
                params.put("keywords", keywords);
                params.put("price_per_hour", pricePerHour);
                params.put("price_per_day", pricePerDay);
                params.put("product_location", location);
                params.put("availability_status", availabilityStatus);
                params.put("product_images", imageUrl);
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