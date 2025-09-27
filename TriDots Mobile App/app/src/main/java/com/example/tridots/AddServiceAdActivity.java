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
import java.util.Map;

public class AddServiceAdActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String ADD_SERVICE_AD_URL = "https://lionsgoldencircle.com/Tridots/Api/add_service_ad.php";
    private static final String GET_SERVICE_CATEGORIES_URL = "https://lionsgoldencircle.com/Tridots/Api/get_service_category.php";

    private EditText editTextName, editTextServiceDescription, editTextContactNumber,
            editTextEmailAddress, editTextAddress, editTextExperience, editTextQualifications, editTextServiceLocation;
    private Spinner spinnerServiceAvailability, spinnerServiceCategory;
    private ImageView imageViewProfile;
    private Button buttonSelectProfileImage, buttonSubmitServiceAd;
    private ProgressBar progressBar;
    private Bitmap bitmap;
    private int loggedInUserId;

    private ArrayList<String> categoryNameList = new ArrayList<>();
    private HashMap<String, Integer> categoryMap = new HashMap<>();
    private int selectedServiceCategoryId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service_ad);

        Toolbar toolbar = findViewById(R.id.toolbarAddServiceAd);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize UI components
        editTextName = findViewById(R.id.editTextName);
        spinnerServiceCategory = findViewById(R.id.spinnerServiceCategory);
        editTextServiceDescription = findViewById(R.id.editTextServiceDescription);
        editTextContactNumber = findViewById(R.id.editTextContactNumber);
        editTextEmailAddress = findViewById(R.id.editTextEmailAddress);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextExperience = findViewById(R.id.editTextExperience);
        editTextQualifications = findViewById(R.id.editTextQualifications);
        editTextServiceLocation = findViewById(R.id.editTextServiceLocation);
        spinnerServiceAvailability = findViewById(R.id.spinnerServiceAvailability);
        imageViewProfile = findViewById(R.id.imageViewProfile);
        buttonSelectProfileImage = findViewById(R.id.buttonSelectProfileImage);
        buttonSubmitServiceAd = findViewById(R.id.buttonSubmitServiceAd);
        progressBar = findViewById(R.id.progressBarAddService);

        SharedPreferences sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        loggedInUserId = sharedPreferences.getInt("user_id", -1);
        if (loggedInUserId == -1) {
            Toast.makeText(this, "You must be logged in to post an ad.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        fetchServiceCategories();

        spinnerServiceCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategoryName = parent.getItemAtPosition(position).toString();
                if (categoryMap.containsKey(selectedCategoryName)) {
                    selectedServiceCategoryId = categoryMap.get(selectedCategoryName);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedServiceCategoryId = -1;
            }
        });

        buttonSelectProfileImage.setOnClickListener(v -> openFileChooser());
        buttonSubmitServiceAd.setOnClickListener(v -> submitAd());
    }

    private void fetchServiceCategories() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_SERVICE_CATEGORIES_URL,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if ("success".equals(jsonObject.getString("status"))) {
                            JSONArray categories = jsonObject.getJSONArray("categories");
                            categoryNameList.clear();
                            categoryMap.clear();
                            for (int i = 0; i < categories.length(); i++) {
                                JSONObject category = categories.getJSONObject(i);
                                String name = category.getString("service_category_name");
                                int id = category.getInt("service_category_id");
                                categoryNameList.add(name);
                                categoryMap.put(name, id);
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNameList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerServiceCategory.setAdapter(adapter);
                        } else {
                            Toast.makeText(this, "Failed to load categories", Toast.LENGTH_SHORT).show();
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
                imageViewProfile.setImageBitmap(bitmap);
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
        if (selectedServiceCategoryId == -1 ||
                editTextName.getText().toString().trim().isEmpty() ||
                editTextServiceDescription.getText().toString().trim().isEmpty() ||
                editTextContactNumber.getText().toString().trim().isEmpty() ||
                editTextEmailAddress.getText().toString().trim().isEmpty() ||
                editTextAddress.getText().toString().trim().isEmpty() ||
                editTextServiceLocation.getText().toString().trim().isEmpty() ||
                bitmap == null) {
            Toast.makeText(this, "Please fill all required fields and select a category and profile picture.", Toast.LENGTH_LONG).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        buttonSubmitServiceAd.setEnabled(false);

        Map<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(loggedInUserId));
        params.put("service_category_id", String.valueOf(selectedServiceCategoryId));
        params.put("name", editTextName.getText().toString().trim());
        params.put("description", editTextServiceDescription.getText().toString().trim());
        params.put("contact_number", editTextContactNumber.getText().toString().trim());
        params.put("email_address", editTextEmailAddress.getText().toString().trim());
        params.put("address", editTextAddress.getText().toString().trim());
        params.put("experience_years", editTextExperience.getText().toString().trim());
        params.put("qualifications", editTextQualifications.getText().toString().trim());
        params.put("location", editTextServiceLocation.getText().toString().trim());
        params.put("availability_status", spinnerServiceAvailability.getSelectedItem().toString());

        Map<String, VolleyMultipartRequest.DataPart> byteData = new HashMap<>();
        byteData.put("profile_picture", new VolleyMultipartRequest.DataPart("profile_pic.png", getFileDataFromDrawable(bitmap)));

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, ADD_SERVICE_AD_URL, params, byteData,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    buttonSubmitServiceAd.setEnabled(true);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        Toast.makeText(AddServiceAdActivity.this, message, Toast.LENGTH_LONG).show();
                        if ("success".equals(status)) {
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(AddServiceAdActivity.this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    buttonSubmitServiceAd.setEnabled(true);
                    Toast.makeText(AddServiceAdActivity.this, "Upload failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
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
