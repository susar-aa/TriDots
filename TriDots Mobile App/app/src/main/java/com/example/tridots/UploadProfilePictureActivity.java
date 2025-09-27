package com.example.tridots;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UploadProfilePictureActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 100;
    private ImageView imageViewProfile;
    private TextView textViewUsername;
    private Button buttonUpload, buttonChooseImage;
    private Uri imageUri;
    private Bitmap bitmap;
    private RequestQueue requestQueue;
    private String username;
    private static final String UPLOAD_URL = "https://lionsgoldencircle.com/Tridots/Api/upload_profile_picture.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile_picture);

        initializeViews();
        setupRequestQueue();
        getUsernameFromIntent();
        setupClickListeners();
    }

    private void initializeViews() {
        imageViewProfile = findViewById(R.id.imageViewProfile);
        textViewUsername = findViewById(R.id.textViewUsername);
        buttonUpload = findViewById(R.id.buttonUpload);
        buttonChooseImage = findViewById(R.id.buttonChooseImage);
    }

    private void setupRequestQueue() {
        requestQueue = Volley.newRequestQueue(this);
    }

    private void getUsernameFromIntent() {
        username = getIntent().getStringExtra("username");
        textViewUsername.setText(username != null ?
                username : "(No username received)");
    }

    private void setupClickListeners() {
        imageViewProfile.setOnClickListener(v -> checkPermissionAndOpenFileChooser());
        buttonChooseImage.setOnClickListener(v -> checkPermissionAndOpenFileChooser());

        buttonUpload.setOnClickListener(v -> {
            if (bitmap == null) {
                Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        });
    }


    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                // Free up previous bitmap memory if exists
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                Glide.with(this)
                        .load(imageUri)
                        .circleCrop()
                        .into(imageViewProfile);
            } catch (IOException e) {
                Log.e("ImageLoad", "Error loading image", e);
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImage() {
        buttonUpload.setEnabled(false);
        buttonUpload.setText("Uploading...");

        // First validate the image
        if (bitmap == null || bitmap.isRecycled()) {
            showError("No valid image selected");
            return;
        }

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            // Reduce quality to 75% to prevent large files
            if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream)) {
                showError("Failed to compress image");
                return;
            }

            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            if (imageBytes.length == 0) {
                showError("Empty image data");
                return;
            }

            final String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            if (encodedImage == null || encodedImage.isEmpty()) {
                showError("Failed to encode image");
                return;
            }

            StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                    response -> {
                        Log.d("Upload Response", "Full response: " + response);
                        buttonUpload.setEnabled(true);
                        buttonUpload.setText("Upload");

                        if (response != null && response.trim().equalsIgnoreCase("success")) {
                            showSuccess("Profile picture uploaded successfully!");
                            // Navigate to TutorialActivity here
                            Intent intent = new Intent(UploadProfilePictureActivity.this, TutorialActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            showError("Server error: " + (response != null ? response : "null response"));
                        }
                    },
                    error -> {
                        Log.e("Upload Error", "Volley error: ", error);
                        String errorMsg = "Upload failed: ";
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            errorMsg += new String(error.networkResponse.data);
                        } else {
                            errorMsg += error.getMessage();
                        }
                        showError(errorMsg);
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", username);
                    params.put("image", encodedImage);
                    params.put("action", "upload_profile_picture");
                    Log.d("Upload Params", "Params: " + params.toString());
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/x-www-form-urlencoded");
                    return headers;
                }
            };

            requestQueue.add(stringRequest);

        } catch (OutOfMemoryError e) {
            Log.e("Upload", "Out of memory", e);
            showError("Image is too large. Please choose a smaller image.");
        } catch (Exception e) {
            Log.e("Upload", "Unexpected error", e);
            showError("Unexpected error: " + e.getMessage());
        }
    }

    private void showError(String message) {
        runOnUiThread(() -> {
            buttonUpload.setEnabled(true);
            buttonUpload.setText("Upload");
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        });
    }

    private void showSuccess(String message) {
        runOnUiThread(() -> {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        });
    }

    private void checkPermissionAndOpenFileChooser() {
        // For Android 10+, we don't need READ_EXTERNAL_STORAGE for image picker
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
            openFileChooser();
        } else {
            // Explain why we need permission first
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(this)
                        .setTitle("Permission needed")
                        .setMessage("This permission is needed to access your photos")
                        .setPositiveButton("OK", (dialog, which) ->
                                requestStoragePermission())
                        .setNegativeButton("Cancel", (dialog, which) ->
                                dialog.dismiss())
                        .create().show();
            } else {
                requestStoragePermission();
            }
        }
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFileChooser();
            } else {
                Toast.makeText(this,
                        "Permission denied. You can enable it in app settings.",
                        Toast.LENGTH_LONG).show();
                // Optionally open app settings
                openAppSettings();
            }
        }
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private String getVolleyErrorMessage(VolleyError error) {
        if (error.networkResponse != null && error.networkResponse.data != null) {
            return new String(error.networkResponse.data);
        }
        return error.getMessage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up bitmap memory
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        if (requestQueue != null) {
            requestQueue.cancelAll(this);
        }
    }
}