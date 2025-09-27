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

public class AddRentingAdActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String ADD_RENTING_AD_URL = "https://lionsgoldencircle.com/Tridots/Api/add_renting_ad.php";
    private static final String GET_RENTING_CATEGORIES_URL = "https://lionsgoldencircle.com/Tridots/Api/get_renting_categories.php";

    private EditText editTextProductName, editTextBrand, editTextModel,
            editTextProductDescription, editTextKeywords, editTextPricePerDay, editTextPricePerHour, editTextProductLocation;
    private Spinner spinnerMainCategory, spinnerSubCategory;
    private ImageView imageViewProduct;
    private Button buttonSelectImage, buttonSubmitRentingAd;
    private ProgressBar progressBar;
    private Bitmap bitmap;
    private int loggedInUserId;

    private HashMap<String, Integer> mainCategoryMap = new HashMap<>();
    private HashMap<Integer, ArrayList<String>> subCategoryNameMap = new HashMap<>();
    private HashMap<String, Integer> subCategoryIdMap = new HashMap<>();

    private int selectedMainCategoryId = -1;
    private int selectedSubCategoryId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_renting_ad);

        Toolbar toolbar = findViewById(R.id.toolbarAddRentingAd);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize UI components
        editTextProductName = findViewById(R.id.editTextProductName);
        editTextBrand = findViewById(R.id.editTextBrand);
        editTextModel = findViewById(R.id.editTextModel);
        spinnerMainCategory = findViewById(R.id.spinnerMainCategory);
        spinnerSubCategory = findViewById(R.id.spinnerSubCategory);
        editTextProductDescription = findViewById(R.id.editTextProductDescription);
        editTextKeywords = findViewById(R.id.editTextKeywords);
        editTextPricePerDay = findViewById(R.id.editTextPricePerDay);
        editTextPricePerHour = findViewById(R.id.editTextPricePerHour);
        editTextProductLocation = findViewById(R.id.editTextProductLocation);
        imageViewProduct = findViewById(R.id.imageViewProduct);
        buttonSelectImage = findViewById(R.id.buttonSelectImage);
        buttonSubmitRentingAd = findViewById(R.id.buttonSubmitRentingAd);
        progressBar = findViewById(R.id.progressBarAddRenting);

        SharedPreferences sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        loggedInUserId = sharedPreferences.getInt("user_id", -1);
        if (loggedInUserId == -1) {
            Toast.makeText(this, "You must be logged in to post an ad.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        fetchRentingCategories();

        spinnerMainCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMainCategoryName = parent.getItemAtPosition(position).toString();
                if (mainCategoryMap.containsKey(selectedMainCategoryName)) {
                    selectedMainCategoryId = mainCategoryMap.get(selectedMainCategoryName);
                    updateSubCategorySpinner(selectedMainCategoryId);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedMainCategoryId = -1;
            }
        });

        spinnerSubCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSubCategoryName = parent.getItemAtPosition(position).toString();
                if (subCategoryIdMap.containsKey(selectedSubCategoryName)) {
                    selectedSubCategoryId = subCategoryIdMap.get(selectedSubCategoryName);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedSubCategoryId = -1;
            }
        });


        buttonSelectImage.setOnClickListener(v -> openFileChooser());
        buttonSubmitRentingAd.setOnClickListener(v -> submitAd());
    }

    private void fetchRentingCategories() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_RENTING_CATEGORIES_URL,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if ("success".equals(jsonObject.getString("status"))) {
                            // Parse Main Categories
                            JSONArray mainCategories = jsonObject.getJSONArray("main_categories");
                            ArrayList<String> mainCategoryNameList = new ArrayList<>();
                            for (int i = 0; i < mainCategories.length(); i++) {
                                JSONObject category = mainCategories.getJSONObject(i);
                                String name = category.getString("main_category");
                                int id = category.getInt("main_category_id");
                                mainCategoryNameList.add(name);
                                mainCategoryMap.put(name, id);
                            }
                            ArrayAdapter<String> mainAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mainCategoryNameList);
                            mainAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerMainCategory.setAdapter(mainAdapter);

                            // Parse Sub Categories
                            JSONArray subCategories = jsonObject.getJSONArray("sub_categories");
                            for (int i = 0; i < subCategories.length(); i++) {
                                JSONObject category = subCategories.getJSONObject(i);
                                int mainId = category.getInt("main_category_id");
                                String subName = category.getString("sub_category");
                                int subId = category.getInt("sub_category_id");

                                if (!subCategoryNameMap.containsKey(mainId)) {
                                    subCategoryNameMap.put(mainId, new ArrayList<>());
                                }
                                subCategoryNameMap.get(mainId).add(subName);
                                subCategoryIdMap.put(subName, subId);
                            }
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

    private void updateSubCategorySpinner(int mainCategoryId) {
        ArrayList<String> subList = subCategoryNameMap.getOrDefault(mainCategoryId, new ArrayList<>());
        ArrayAdapter<String> subAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subList);
        subAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubCategory.setAdapter(subAdapter);
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
                imageViewProduct.setImageBitmap(bitmap);
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
        if (selectedMainCategoryId == -1 || selectedSubCategoryId == -1 ||
                editTextProductName.getText().toString().trim().isEmpty() ||
                editTextBrand.getText().toString().trim().isEmpty() ||
                editTextProductDescription.getText().toString().trim().isEmpty() ||
                editTextKeywords.getText().toString().trim().isEmpty() ||
                editTextPricePerDay.getText().toString().trim().isEmpty() ||
                editTextProductLocation.getText().toString().trim().isEmpty() ||
                bitmap == null) {
            Toast.makeText(this, "Please fill all required fields and select categories and an image.", Toast.LENGTH_LONG).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        buttonSubmitRentingAd.setEnabled(false);

        Map<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(loggedInUserId));
        params.put("main_category_id", String.valueOf(selectedMainCategoryId));
        params.put("sub_category_id", String.valueOf(selectedSubCategoryId));
        params.put("product_name", editTextProductName.getText().toString().trim());
        params.put("brand", editTextBrand.getText().toString().trim());
        params.put("model", editTextModel.getText().toString().trim());
        params.put("product_description", editTextProductDescription.getText().toString().trim());
        params.put("keywords", editTextKeywords.getText().toString().trim());
        params.put("price_per_day", editTextPricePerDay.getText().toString().trim());
        params.put("price_per_hour", editTextPricePerHour.getText().toString().trim());
        params.put("product_location", editTextProductLocation.getText().toString().trim());

        Map<String, VolleyMultipartRequest.DataPart> byteData = new HashMap<>();
        byteData.put("product_images", new VolleyMultipartRequest.DataPart("renting_item.png", getFileDataFromDrawable(bitmap)));

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, ADD_RENTING_AD_URL, params, byteData,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    buttonSubmitRentingAd.setEnabled(true);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        Toast.makeText(AddRentingAdActivity.this, message, Toast.LENGTH_LONG).show();
                        if ("success".equals(status)) {
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(AddRentingAdActivity.this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    buttonSubmitRentingAd.setEnabled(true);
                    Toast.makeText(AddRentingAdActivity.this, "Upload failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
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
