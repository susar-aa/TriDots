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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class VehicleAdEditActivity extends AppCompatActivity {

    private Toolbar toolbarEditVehicleAd;
    private ImageView imageViewEditVehicle;
    private Button buttonSelectImage, buttonSaveVehicleAd, buttonDeleteVehicleAd;
    private EditText editTextEditVehicleName, editTextEditBrand, editTextEditModel,
            editTextEditDescription, editTextEditCapacity, editTextEditLocation,
            editTextEditAmount, editTextEditKeywords;
    private Spinner spinnerEditFuelType, spinnerEditTransmissionType, spinnerEditPriceType;
    private Switch availabilitySwitch;
    private int adId;
    private RequestQueue requestQueue;
    private static final String GET_VEHICLE_DETAIL_URL = "https://lionsgoldencircle.com/Tridots/Api/get_vehicle_detail.php";
    private static final String UPDATE_VEHICLE_URL = "https://lionsgoldencircle.com/Tridots/Api/update_vehicle.php";
    private static final String UPLOAD_IMAGE_URL = "https://lionsgoldencircle.com/Tridots/Api/upload_vehicle_image.php";
    private static final String DELETE_VEHICLE_URL = "https://lionsgoldencircle.com/Tridots/Api/delete_ad.php";
    private Uri selectedImageUri;
    private Bitmap bitmap;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 100;
    private String currentImageUrl;
    private String availabilityStatus; // To store availability status
    private String adType = "Vehicle"; // Hardcoded for this activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_ad_edit);

        toolbarEditVehicleAd = findViewById(R.id.toolbarEditVehicleAd);
        setSupportActionBar(toolbarEditVehicleAd);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Vehicle Ad");

        imageViewEditVehicle = findViewById(R.id.imageViewEditVehicle);
        buttonSelectImage = findViewById(R.id.buttonSelectImage);
        buttonSaveVehicleAd = findViewById(R.id.buttonSaveVehicleAd);
        buttonDeleteVehicleAd = findViewById(R.id.buttonDeleteVehicleAd); // Initialize delete button
        editTextEditVehicleName = findViewById(R.id.editTextEditVehicleName);
        editTextEditBrand = findViewById(R.id.editTextEditBrand);
        editTextEditModel = findViewById(R.id.editTextEditModel);
        editTextEditDescription = findViewById(R.id.editTextEditDescription);
        editTextEditCapacity = findViewById(R.id.editTextEditCapacity);
        editTextEditLocation = findViewById(R.id.editTextEditLocation);
        editTextEditAmount = findViewById(R.id.editTextEditAmount);
        editTextEditKeywords = findViewById(R.id.editTextEditKeywords);

        spinnerEditFuelType = findViewById(R.id.spinnerEditFuelType);
        spinnerEditTransmissionType = findViewById(R.id.spinnerEditTransmissionType);
        spinnerEditPriceType = findViewById(R.id.spinnerEditPriceType);
        availabilitySwitch = findViewById(R.id.availabilitySwitch); // Initialize the Switch

        requestQueue = Volley.newRequestQueue(this);

        // Get the ad ID from the intent
        adId = getIntent().getIntExtra("adId", -1);
        if (adId != -1) {
            fetchVehicleDetails(adId);
        } else {
            Toast.makeText(this, "Error: Ad ID not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Set up Spinners
        ArrayAdapter<CharSequence> priceTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.price_type_array, android.R.layout.simple_spinner_item);
        priceTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEditPriceType.setAdapter(priceTypeAdapter);

        ArrayAdapter<CharSequence> fuelTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.fuel_type_array, android.R.layout.simple_spinner_item);
        fuelTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEditFuelType.setAdapter(fuelTypeAdapter);

        ArrayAdapter<CharSequence> transmissionTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.transmission_type_array, android.R.layout.simple_spinner_item);
        transmissionTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEditTransmissionType.setAdapter(transmissionTypeAdapter);

        // Set listener for the availability switch
        availabilitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                availabilityStatus = isChecked ? "available" : "not available";
                availabilitySwitch.setText(availabilityStatus.equalsIgnoreCase("available") ? "Available" : "Not Available");
                Log.d("Availability", "Availability status changed to: " + availabilityStatus);
            }
        });

        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkStoragePermission();
            }
        });

        buttonSaveVehicleAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedImageUri != null) {
                    uploadImage();
                } else {
                    updateVehicleAd(currentImageUrl); // Save without uploading new image
                }
            }
        });

        // Set listener for the delete button
        buttonDeleteVehicleAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this vehicle ad?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteAd(adId, adType);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void deleteAd(final int adIdToDelete, final String adType) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DELETE_VEHICLE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Delete Vehicle Ad Response", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            Toast.makeText(VehicleAdEditActivity.this, message, Toast.LENGTH_SHORT).show();
                            if (status.equals("success")) {
                                finish(); // Go back to Manage Ads after successful deletion
                            } else {
                                Toast.makeText(VehicleAdEditActivity.this, "Delete failed: " + message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(VehicleAdEditActivity.this, "Error parsing delete response: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        } finally {
                            // Hide progress bar if you showed one
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = (error != null && error.getMessage() != null) ? error.getMessage() : (error != null ? error.toString() : "Unknown error occurred");
                        Toast.makeText(VehicleAdEditActivity.this, "Error deleting ad: " + errorMessage, Toast.LENGTH_LONG).show();
                        Log.e("Delete Vehicle Ad Error", "Error deleting ad: " + (error != null ? error.toString() : "VolleyError is null"));
                        // Hide progress bar if you showed one
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("vehicle_id", String.valueOf(adIdToDelete)); // Use the correct ID key for Vehicle
                params.put("ad_type", adType);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void checkStoragePermission() {
        Log.d("PermissionCheck", "Checking storage permission (SDK " + Build.VERSION.SDK_INT + ")...");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Log.d("PermissionCheck", "Storage permission NOT granted (SDK " + Build.VERSION.SDK_INT + ").");
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Log.d("PermissionCheck", "Showing permission rationale (SDK " + Build.VERSION.SDK_INT + ").");
                    showPermissionRationaleDialog();
                } else {
                    Log.d("PermissionCheck", "Requesting permission directly (SDK " + Build.VERSION.SDK_INT + ").");
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                }
            } else {
                Log.d("PermissionCheck", "Storage permission ALREADY granted (SDK " + Build.VERSION.SDK_INT + ").");
                openFileChooser();
            }
        } else {
            Log.d("PermissionCheck", "Device older than Marshmallow (SDK " + Build.VERSION.SDK_INT + "), opening file chooser.");
            openFileChooser();
        }
    }

    private void showPermissionRationaleDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Storage Permission Required")
                .setMessage("This app needs storage permission to select an image for your ad.")
                .setPositiveButton("Allow", (dialog, which) -> {
                    // Request the permission
                    ActivityCompat.requestPermissions(VehicleAdEditActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                    Toast.makeText(VehicleAdEditActivity.this, "Permission denied. You won't be able to select an image.", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("PermissionResult", "onRequestPermissionsResult called for requestCode: " + requestCode);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("PermissionResult", "Storage permission GRANTED.");
                openFileChooser();
            } else {
                Log.d("PermissionResult", "Storage permission DENIED.");
                Toast.makeText(this, "Permission denied. You won't be able to select an image.", Toast.LENGTH_SHORT).show();
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Log.d("PermissionResult", "Showing 'Don't ask again' dialog.");
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
            Log.d("ImageSelection", "Selected Image URI: " + selectedImageUri);

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                bitmap = rotateImageIfRequired(bitmap, selectedImageUri); // Rotate the bitmap if needed
                imageViewEditVehicle.setImageBitmap(bitmap); // Set the rotated bitmap to the ImageView
                Log.d("ImageSelection", "Bitmap created and rotated successfully: " + bitmap.getWidth() + "x" + bitmap.getHeight());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("ImageSelection", "Error getting or rotating Bitmap from URI: " + e.getMessage());
                Glide.with(this)
                        .load(selectedImageUri)
                        .placeholder(R.drawable.ad_placeholder)
                        .error(R.drawable.image_error_placeholder)
                        .into(imageViewEditVehicle); // Fallback if bitmap loading/rotation fails
            }
        } else {
            Log.d("ImageSelection", "Image selection cancelled or data is null.");
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

    private void fetchVehicleDetails(int vehicleId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_VEHICLE_DETAIL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Fetch Vehicle Details", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            if (status.equals("success")) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                populateVehicleDetails(data);
                            } else {
                                Toast.makeText(VehicleAdEditActivity.this, "Failed to fetch details: " + jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(VehicleAdEditActivity.this, "Error parsing vehicle details JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(VehicleAdEditActivity.this, "Error fetching vehicle details: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(vehicleId));
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void populateVehicleDetails(JSONObject data) throws JSONException {
        editTextEditVehicleName.setText(data.getString("vehicle_name"));
        editTextEditBrand.setText(data.getString("brand"));
        editTextEditModel.setText(data.getString("model"));
        editTextEditDescription.setText(data.getString("description"));
        editTextEditCapacity.setText(data.getString("capacity"));
        editTextEditLocation.setText(data.getString("location"));
        editTextEditAmount.setText(data.getString("amount"));
        editTextEditKeywords.setText(data.getString("keywords"));

        currentImageUrl = data.getString("vehicle_images");
        if (currentImageUrl != null && !currentImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(currentImageUrl)
                    .placeholder(R.drawable.ad_placeholder)
                    .error(R.drawable.image_error_placeholder)
                    .into(imageViewEditVehicle);
        }

        // Set Spinner selections based on the fetched data
        setSpinnerSelection(spinnerEditFuelType, data.getString("fuel_type"));
        setSpinnerSelection(spinnerEditTransmissionType, data.getString("transmission_type"));
        setSpinnerSelection(spinnerEditPriceType, data.getString("price_type"));

        // Set the initial state of the availability switch
        String fetchedAvailability = data.getString("availability_status");
        availabilityStatus = fetchedAvailability;
        availabilitySwitch.setChecked(availabilityStatus.equalsIgnoreCase("available"));
        availabilitySwitch.setText(availabilityStatus.equalsIgnoreCase("available") ? "Available" : "Not Available");
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinner.getAdapter();
        if (adapter != null) {
            int position = adapter.getPosition(value);
            if (position != -1) {
                spinner.setSelection(position);
            }
        }
    }

    private void uploadImage() {
        if (bitmap == null) {
            Log.e("ImageUpload", "Bitmap is null. Cannot upload.");
            Toast.makeText(VehicleAdEditActivity.this, "Error: Could not process the selected image.", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("ImageUpload", "Attempting to upload image with Bitmap: " + bitmap.getWidth() + "x" + bitmap.getHeight());
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
                                Toast.makeText(VehicleAdEditActivity.this, "Image uploaded successfully.", Toast.LENGTH_SHORT).show();
                                updateVehicleAd(imageUrl); // Save ad details with the new image URL
                            } else {
                                Toast.makeText(VehicleAdEditActivity.this, "Image upload failed: " + message, Toast.LENGTH_SHORT).show();
                                updateVehicleAd(currentImageUrl); // Save ad details even if image upload fails
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(VehicleAdEditActivity.this, "Error parsing image upload response", Toast.LENGTH_SHORT).show();
                            updateVehicleAd(currentImageUrl); // Save ad details on error
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(VehicleAdEditActivity.this, "Error uploading image: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        updateVehicleAd(currentImageUrl); // Save ad details on error
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("image", imageToString(bitmap));
                params.put("name", "vehicle_image_" + System.currentTimeMillis() + ".jpg"); // Ensure file extension
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

    private void updateVehicleAd(String imageUrl) {
        final String vehicleName = editTextEditVehicleName.getText().toString().trim();
        final String brand = editTextEditBrand.getText().toString().trim();
        final String model = editTextEditModel.getText().toString().trim();
        final String description = editTextEditDescription.getText().toString().trim();
        final String capacity = editTextEditCapacity.getText().toString().trim();
        final String location = editTextEditLocation.getText().toString().trim();
        final String amount = editTextEditAmount.getText().toString().trim();
        final String keywords = editTextEditKeywords.getText().toString().trim();
        final String fuelType = spinnerEditFuelType.getSelectedItem().toString();
        final String transmissionType = spinnerEditTransmissionType.getSelectedItem().toString();
        final String priceType = spinnerEditPriceType.getSelectedItem().toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPDATE_VEHICLE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Update Vehicle Ad", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            Toast.makeText(VehicleAdEditActivity.this, message, Toast.LENGTH_SHORT).show();
                            if (status.equals("success")) {
                                finish(); // Go back to the Manage Ads screen
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(VehicleAdEditActivity.this, "Error parsing update response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(VehicleAdEditActivity.this, "Error updating vehicle ad: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("vehicle_id", String.valueOf(adId));
                params.put("vehicle_name", vehicleName);
                params.put("brand", brand);
                params.put("model", model);
                params.put("description", description);
                params.put("capacity", capacity);
                params.put("fuel_type", fuelType);
                params.put("transmission_type", transmissionType);
                params.put("availability_status", availabilityStatus); // Include availability status
                params.put("location", location);
                params.put("price_type", priceType);
                params.put("amount", amount);
                params.put("keywords", keywords);
                params.put("vehicle_images", imageUrl); // Send the image URL
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