package com.example.tridots;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tridots.R;
import com.example.tridots.system.VolleyMultipartRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddVehicleAdActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String ADD_VEHICLE_AD_URL = "https://lionsgoldencircle.com/Tridots/Api/add_vehicle_ad.php";
    // Corrected the URL based on your previous instruction
    private static final String GET_VEHICLE_CATEGORIES_URL = "https://lionsgoldencircle.com/Tridots/Api/get_vehicle_category.php";

    private EditText editTextVehicleName, editTextVehicleBrand, editTextVehicleModel,
            editTextVehicleDescription, editTextCapacity, editTextFuelType, editTextTransmissionType,
            editTextLocation, editTextAmount, editTextVehicleKeywords;
    private Spinner spinnerAvailability, spinnerPriceType, spinnerVehicleCategory;
    private ImageView imageViewVehicle;
    private Button buttonSelectVehicleImage, buttonSubmitVehicleAd;
    private ProgressBar progressBar;
    private Bitmap bitmap;
    private int loggedInUserId;

    private ArrayList<String> categoryNameList = new ArrayList<>();
    private HashMap<String, Integer> categoryMap = new HashMap<>();
    private int selectedVehicleCategoryId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle_ad);

        Toolbar toolbar = findViewById(R.id.toolbarAddVehicleAd);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize UI components
        editTextVehicleName = findViewById(R.id.editTextVehicleName);
        spinnerVehicleCategory = findViewById(R.id.spinnerVehicleCategory);
        editTextVehicleBrand = findViewById(R.id.editTextVehicleBrand);
        editTextVehicleModel = findViewById(R.id.editTextVehicleModel);
        editTextVehicleDescription = findViewById(R.id.editTextVehicleDescription);
        editTextCapacity = findViewById(R.id.editTextCapacity);
        editTextFuelType = findViewById(R.id.editTextFuelType);
        editTextTransmissionType = findViewById(R.id.editTextTransmissionType);
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextAmount = findViewById(R.id.editTextAmount);
        editTextVehicleKeywords = findViewById(R.id.editTextVehicleKeywords);
        spinnerAvailability = findViewById(R.id.spinnerAvailability);
        spinnerPriceType = findViewById(R.id.spinnerPriceType);
        imageViewVehicle = findViewById(R.id.imageViewVehicle);
        buttonSelectVehicleImage = findViewById(R.id.buttonSelectVehicleImage);
        buttonSubmitVehicleAd = findViewById(R.id.buttonSubmitVehicleAd);
        progressBar = findViewById(R.id.progressBarAddVehicle);

        SharedPreferences sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        loggedInUserId = sharedPreferences.getInt("user_id", -1);
        if (loggedInUserId == -1) {
            Toast.makeText(this, "You must be logged in to post an ad.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        fetchVehicleCategories();

        spinnerVehicleCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategoryName = parent.getItemAtPosition(position).toString();
                if (categoryMap.containsKey(selectedCategoryName)) {
                    selectedVehicleCategoryId = categoryMap.get(selectedCategoryName);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedVehicleCategoryId = -1;
            }
        });

        buttonSelectVehicleImage.setOnClickListener(v -> openFileChooser());
        buttonSubmitVehicleAd.setOnClickListener(v -> submitAd());
    }

    private void fetchVehicleCategories() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_VEHICLE_CATEGORIES_URL,
                response -> {
                    try {
                        // **FIX: Parse the response as a JSONObject**
                        JSONObject jsonObject = new JSONObject(response);
                        if ("success".equals(jsonObject.getString("status"))) {
                            // **FIX: Get the "categories" JSONArray from the JSONObject**
                            JSONArray categories = jsonObject.getJSONArray("categories");
                            categoryNameList.clear();
                            categoryMap.clear();
                            for (int i = 0; i < categories.length(); i++) {
                                JSONObject category = categories.getJSONObject(i);
                                // Ensure the key matches the column name from your VehicleCategory table
                                String name = category.getString("vehicle_category_name");
                                int id = category.getInt("vehicle_category_id");
                                categoryNameList.add(name);
                                categoryMap.put(name, id);
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNameList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerVehicleCategory.setAdapter(adapter);
                        } else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(this, "Failed to load categories: " + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing categories", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Error fetching categories: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(stringRequest);
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
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageViewVehicle.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void submitAd() {
        if (selectedVehicleCategoryId == -1 ||
                editTextVehicleName.getText().toString().trim().isEmpty() ||
                editTextVehicleBrand.getText().toString().trim().isEmpty() ||
                editTextVehicleModel.getText().toString().trim().isEmpty() ||
                editTextVehicleDescription.getText().toString().trim().isEmpty() ||
                editTextCapacity.getText().toString().trim().isEmpty() ||
                editTextFuelType.getText().toString().trim().isEmpty() ||
                editTextTransmissionType.getText().toString().trim().isEmpty() ||
                editTextLocation.getText().toString().trim().isEmpty() ||
                editTextAmount.getText().toString().trim().isEmpty() ||
                editTextVehicleKeywords.getText().toString().trim().isEmpty() ||
                bitmap == null) {
            Toast.makeText(this, "Please fill all required fields and select an image and category.", Toast.LENGTH_LONG).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        buttonSubmitVehicleAd.setEnabled(false);

        // Prepare parameters
        Map<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(loggedInUserId));
        params.put("vehicle_category_id", String.valueOf(selectedVehicleCategoryId));
        params.put("vehicle_name", editTextVehicleName.getText().toString().trim());
        params.put("brand", editTextVehicleBrand.getText().toString().trim());
        params.put("model", editTextVehicleModel.getText().toString().trim());
        params.put("description", editTextVehicleDescription.getText().toString().trim());
        params.put("capacity", editTextCapacity.getText().toString().trim());
        params.put("fuel_type", editTextFuelType.getText().toString().trim());
        params.put("transmission_type", editTextTransmissionType.getText().toString().trim());
        params.put("location", editTextLocation.getText().toString().trim());
        params.put("amount", editTextAmount.getText().toString().trim());
        params.put("keywords", editTextVehicleKeywords.getText().toString().trim());
        params.put("availability_status", spinnerAvailability.getSelectedItem().toString());
        params.put("price_type", spinnerPriceType.getSelectedItem().toString());

        // Prepare file part
        Map<String, VolleyMultipartRequest.DataPart> byteData = new HashMap<>();
        byteData.put("vehicle_images", new VolleyMultipartRequest.DataPart("vehicle_image.png", getFileDataFromDrawable(bitmap)));

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, ADD_VEHICLE_AD_URL, params, byteData,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    buttonSubmitVehicleAd.setEnabled(true);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        Toast.makeText(AddVehicleAdActivity.this, message, Toast.LENGTH_LONG).show();
                        if ("success".equals(status)) {
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(AddVehicleAdActivity.this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    buttonSubmitVehicleAd.setEnabled(true);
                    Toast.makeText(AddVehicleAdActivity.this, "Upload failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("VolleyError", error.toString());
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multipartRequest);
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
