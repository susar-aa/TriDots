// File: app/src/main/java/com/example/tridots/AddProductActivity.java
package com.example.tridots.Marketplace; // Adjust package if you place it in a subfolder like main_screens

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.exifinterface.media.ExifInterface;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tridots.R;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddProductActivity extends AppCompatActivity {

    private static final String TAG = "AddProductActivity";

    // UI Elements
    private Spinner spinnerCategory;
    private TextInputEditText editTextProductName;
    private TextInputEditText editTextProductDescription;
    private TextInputEditText editTextPrice;
    private TextInputEditText editTextStockQuantity;
    private RadioGroup radioGroupProductCondition;
    private RadioButton radioButtonNew, radioButtonUsed;
    private TextInputEditText editTextSku;
    private TextInputEditText editTextBrand;
    private TextInputEditText editTextModel;
    private TextInputEditText editTextColor;
    private TextInputEditText editTextKeywords;
    private TextInputEditText editTextCompatibility;
    private TextInputEditText editTextWarrantyPolicy;
    private TextInputEditText editTextWeight;
    private TextInputEditText editTextDimensions;
    private TextInputEditText editTextMaterial;
    private TextInputEditText editTextManufacturingDate;
    private TextInputEditText editTextExpirationDate;
    private TextInputEditText editTextStoreAddressLine1;
    private TextInputEditText editTextStoreAddressLine2;
    private TextInputEditText editTextStoreCity;
    private TextInputEditText editTextStoreDistrict;
    private TextInputEditText editTextStorePostalCode;
    private TextInputEditText editTextStoreCountry;
    private TextInputEditText editTextDeliveryFee;
    private Button buttonAddVariant;
    private Button buttonAddProduct;
    private LinearLayout linearLayoutVariantsContainer;

    // Main Product Image UI elements
    private ImageView imageViewMainProductPreview;
    private Button buttonSelectMainProductImage;
    private String mainProductImageBase64;


    private RequestQueue requestQueue;
    private SharedPreferences sharedPreferences;
    private int sellerId;
    private List<Category> categoriesList = new ArrayList<>();
    private ArrayAdapter<String> categoryAdapter;

    private List<ProductVariant> productVariants = new ArrayList<>();

    // For Variant Image selection
    private ImageView currentVariantImageView;
    private String currentVariantImageBase64;

    // Date format for displaying and sending to PHP
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);


    // ActivityResultLauncher for picking Main Product Image
    private ActivityResultLauncher<Intent> pickMainImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                            bitmap = rotateBitmap(bitmap, imageUri);
                            imageViewMainProductPreview.setImageBitmap(bitmap);
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
                            byte[] byteArray = byteArrayOutputStream.toByteArray();
                            mainProductImageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                            Log.d(TAG, "Main image converted to Base64. Size: " + mainProductImageBase64.length() + " bytes");
                        } catch (IOException e) {
                            Log.e(TAG, "Error converting main image to Base64: " + e.getMessage());
                            Toast.makeText(this, "Error selecting main image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            mainProductImageBase64 = null;
                        }
                    }
                } else {
                    Toast.makeText(this, "Main image selection cancelled.", Toast.LENGTH_SHORT).show();
                }
            }
    );

    // ActivityResultLauncher for picking Variant Image
    private ActivityResultLauncher<Intent> pickVariantImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                            bitmap = rotateBitmap(bitmap, imageUri);
                            if (currentVariantImageView != null) {
                                currentVariantImageView.setImageBitmap(bitmap);
                            }
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
                            byte[] byteArray = byteArrayOutputStream.toByteArray();
                            currentVariantImageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                            Log.d(TAG, "Variant image converted to Base64. Size: " + currentVariantImageBase64.length() + " bytes");

                        } catch (IOException e) {
                            Log.e(TAG, "Error converting variant image to Base64: " + e.getMessage());
                            Toast.makeText(this, "Error selecting variant image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            currentVariantImageBase64 = null;
                        }
                    }
                } else {
                    Toast.makeText(this, "Variant image selection cancelled.", Toast.LENGTH_SHORT).show();
                }
            }
    );


    // API Endpoints
    private static final String API_BASE_URL = "https://lionsgoldencircle.com/Tridots/Api/";
    private static final String GET_CATEGORIES_URL = API_BASE_URL + "get_categories.php";
    private static final String ADD_PRODUCT_URL = API_BASE_URL + "add_product.php";
    private static final String ADD_PRODUCT_VARIANTS_URL = API_BASE_URL + "add_product_variants.php";
    private static final String UPLOAD_IMAGE_URL = API_BASE_URL + "upload_image.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        sellerId = sharedPreferences.getInt("user_id", -1);

        if (sellerId == -1) {
            Toast.makeText(this, "Seller ID not found. Please log in again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Initialize UI elements (including all new ones)
        spinnerCategory = findViewById(R.id.spinnerCategory);
        editTextProductName = findViewById(R.id.editTextProductName);
        editTextProductDescription = findViewById(R.id.editTextProductDescription);
        editTextPrice = findViewById(R.id.editTextPrice);
        editTextStockQuantity = findViewById(R.id.editTextStockQuantity);
        radioGroupProductCondition = findViewById(R.id.radioGroupProductCondition);
        radioButtonNew = findViewById(R.id.radioButtonNew);
        radioButtonUsed = findViewById(R.id.radioButtonUsed);
        editTextSku = findViewById(R.id.editTextSku);
        editTextBrand = findViewById(R.id.editTextBrand);
        editTextModel = findViewById(R.id.editTextModel);
        editTextColor = findViewById(R.id.editTextColor);
        editTextKeywords = findViewById(R.id.editTextKeywords);
        editTextCompatibility = findViewById(R.id.editTextCompatibility);
        editTextWarrantyPolicy = findViewById(R.id.editTextWarrantyPolicy);
        editTextWeight = findViewById(R.id.editTextWeight);
        editTextDimensions = findViewById(R.id.editTextDimensions);
        editTextMaterial = findViewById(R.id.editTextMaterial);
        editTextManufacturingDate = findViewById(R.id.editTextManufacturingDate);
        editTextExpirationDate = findViewById(R.id.editTextExpirationDate);
        editTextStoreAddressLine1 = findViewById(R.id.editTextStoreAddressLine1);
        editTextStoreAddressLine2 = findViewById(R.id.editTextStoreAddressLine2);
        editTextStoreCity = findViewById(R.id.editTextStoreCity);
        editTextStoreDistrict = findViewById(R.id.editTextStoreDistrict);
        editTextStorePostalCode = findViewById(R.id.editTextStorePostalCode);
        editTextStoreCountry = findViewById(R.id.editTextStoreCountry);
        editTextDeliveryFee = findViewById(R.id.editTextDeliveryFee);
        buttonAddVariant = findViewById(R.id.buttonAddVariant);
        buttonAddProduct = findViewById(R.id.buttonAddProduct);
        linearLayoutVariantsContainer = findViewById(R.id.linearLayoutVariantsContainer);

        imageViewMainProductPreview = findViewById(R.id.imageViewMainProductPreview);
        buttonSelectMainProductImage = findViewById(R.id.buttonSelectMainProductImage);


        requestQueue = Volley.newRequestQueue(this);

        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        fetchCategories();

        // Set click listeners for buttons
        buttonAddProduct.setOnClickListener(v -> addProduct());
        buttonAddVariant.setOnClickListener(v -> showAddVariantDialog());
        buttonSelectMainProductImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickMainImageLauncher.launch(intent);
        });

        // Set click listeners for date fields to show DatePicker
        editTextManufacturingDate.setOnClickListener(v -> showDatePickerDialog(editTextManufacturingDate));
        editTextManufacturingDate.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) showDatePickerDialog(editTextManufacturingDate);
        });
        editTextExpirationDate.setOnClickListener(v -> showDatePickerDialog(editTextExpirationDate));
        editTextExpirationDate.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) showDatePickerDialog(editTextExpirationDate);
        });
    }

    /**
     * Displays a DatePickerDialog and sets the selected date to the given EditText.
     * @param dateEditText The TextInputEditText to set the date to.
     */
    private void showDatePickerDialog(final TextInputEditText dateEditText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    calendar.set(selectedYear, selectedMonth, selectedDay);
                    dateEditText.setText(dateFormat.format(calendar.getTime()));
                }, year, month, day);
        datePickerDialog.show();
    }


    private void fetchCategories() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_CATEGORIES_URL,
                response -> {
                    Log.d(TAG, "Categories Response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        if (success) {
                            JSONArray categoriesArray = jsonResponse.getJSONArray("categories");
                            categoriesList.clear();
                            ArrayList<String> categoryNames = new ArrayList<>();
                            categoryNames.add("Select Category");

                            for (int i = 0; i < categoriesArray.length(); i++) {
                                JSONObject categoryObject = categoriesArray.getJSONObject(i);
                                int categoryId = categoryObject.getInt("category_id");
                                String categoryName = categoryObject.getString("category_name");
                                categoriesList.add(new Category(categoryId, categoryName));
                                categoryNames.add(categoryName);
                            }
                            categoryAdapter.clear();
                            categoryAdapter.addAll(categoryNames);
                            categoryAdapter.notifyDataSetChanged();

                            if (categoriesList.isEmpty()) {
                                Toast.makeText(AddProductActivity.this, "No categories found.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String message = jsonResponse.optString("error", "Failed to fetch categories.");
                            Toast.makeText(AddProductActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error for categories: " + e.getMessage());
                        Toast.makeText(AddProductActivity.this, "Error parsing category data.", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Volley error fetching categories: " + error.getMessage());
                    Toast.makeText(AddProductActivity.this, "Network error fetching categories.", Toast.LENGTH_LONG).show();
                });
        requestQueue.add(stringRequest);
    }

    /**
     * Shows a dialog to add product variants.
     */
    private void showAddVariantDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_variant, null);
        builder.setView(dialogView);
        builder.setTitle("Add Product Variant");

        final EditText variantNameEditText = dialogView.findViewById(R.id.editTextVariantName);
        final EditText variantPriceEditText = dialogView.findViewById(R.id.editTextVariantPrice);
        final EditText variantStockEditText = dialogView.findViewById(R.id.editTextVariantStock);
        final ImageView variantImageView = dialogView.findViewById(R.id.imageViewVariantPreview);
        final Button selectImageButton = dialogView.findViewById(R.id.buttonSelectVariantImage);

        currentVariantImageView = variantImageView;
        currentVariantImageBase64 = null;

        selectImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickVariantImageLauncher.launch(intent);
        });


        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = variantNameEditText.getText().toString().trim();
            String price = variantPriceEditText.getText().toString().trim();
            String stock = variantStockEditText.getText().toString().trim();

            if (name.isEmpty() || price.isEmpty() || stock.isEmpty()) {
                Toast.makeText(this, "Variant fields cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                BigDecimal variantPrice = new BigDecimal(price);
                int variantStock = Integer.parseInt(stock);

                productVariants.add(new ProductVariant(name, variantPrice, variantStock, currentVariantImageBase64));
                updateVariantsContainer();
                Toast.makeText(this, "Variant added: " + name, Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid number format for price or stock.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    /**
     * Dynamically updates the LinearLayout to show currently added variants.
     */
    private void updateVariantsContainer() {
        linearLayoutVariantsContainer.removeAllViews();
        if (productVariants.isEmpty()) {
            TextView noVariantsText = new TextView(this);
            noVariantsText.setText("No variants added yet.");
            noVariantsText.setPadding(0, 8, 0, 8);
            linearLayoutVariantsContainer.addView(noVariantsText);
        } else {
            for (int i = 0; i < productVariants.size(); i++) {
                ProductVariant variant = productVariants.get(i);
                TextView variantTextView = new TextView(this);
                variantTextView.setText(String.format(Locale.getDefault(),
                        "• %s (LKR %.2f, Stock: %d)",
                        variant.getName(), variant.getPrice(), variant.getStockQuantity()));
                if (variant.getImageBase64() != null) {
                    variantTextView.append(" - Has Image");
                }
                variantTextView.setPadding(0, 4, 0, 4);
                linearLayoutVariantsContainer.addView(variantTextView);

                Button removeButton = new Button(this);
                removeButton.setText("Remove");
                removeButton.setTag(i);
                removeButton.setOnClickListener(v -> {
                    int indexToRemove = (int) v.getTag();
                    productVariants.remove(indexToRemove);
                    updateVariantsContainer();
                    Toast.makeText(AddProductActivity.this, "Variant removed.", Toast.LENGTH_SHORT).show();
                });
                linearLayoutVariantsContainer.addView(removeButton);
            }
        }
    }


    /**
     * Helper method to rotate a Bitmap based on EXIF orientation.
     * @param bitmap The original Bitmap.
     * @param imageUri The URI of the image from which EXIF data can be read.
     * @return The rotated Bitmap.
     */
    private Bitmap rotateBitmap(Bitmap bitmap, Uri imageUri) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = getContentResolver().openInputStream(imageUri);
            ExifInterface exifInterface = new ExifInterface(inputStream);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    matrix.preScale(-1.0f, 1.0f);
                    break;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    matrix.preScale(1.0f, -1.0f);
                    break;
                case ExifInterface.ORIENTATION_TRANSPOSE:
                    matrix.postRotate(90);
                    matrix.preScale(-1.0f, 1.0f);
                    break;
                case ExifInterface.ORIENTATION_TRANSVERSE:
                    matrix.postRotate(270);
                    matrix.preScale(-1.0f, 1.0f);
                    break;
                default:
                    return bitmap; // No rotation needed
            }
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    /**
     * Gathers product data from the form fields, validates it, and sends it to the backend.
     * This method will now also handle sending variant data.
     */
    private void addProduct() {
        if (spinnerCategory.getSelectedItemPosition() == 0 || categoriesList.isEmpty()) {
            Toast.makeText(this, "Please select a category.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (editTextProductName.getText().toString().trim().isEmpty()) {
            editTextProductName.setError("Product Name is required.");
            editTextProductName.requestFocus();
            return;
        }
        if (editTextProductDescription.getText().toString().trim().isEmpty()) {
            editTextProductDescription.setError("Product Description is required.");
            editTextProductDescription.requestFocus();
            return;
        }
        if (editTextPrice.getText().toString().trim().isEmpty()) {
            editTextPrice.setError("Price is required.");
            editTextPrice.requestFocus();
            return;
        }
        if (editTextStockQuantity.getText().toString().trim().isEmpty()) {
            editTextStockQuantity.setError("Stock Quantity is required.");
            editTextStockQuantity.requestFocus();
            return;
        }
        int selectedConditionId = radioGroupProductCondition.getCheckedRadioButtonId();
        if (selectedConditionId == -1) {
            Toast.makeText(this, "Please select product condition.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (editTextStoreAddressLine1.getText().toString().trim().isEmpty()) {
            editTextStoreAddressLine1.setError("Store Address Line 1 is required.");
            editTextStoreAddressLine1.requestFocus();
            return;
        }
        if (editTextStoreCity.getText().toString().trim().isEmpty()) {
            editTextStoreCity.setError("Store City is required.");
            editTextStoreCity.requestFocus();
            return;
        }
        if (editTextStoreDistrict.getText().toString().trim().isEmpty()) {
            editTextStoreDistrict.setError("Store District is required.");
            editTextStoreDistrict.requestFocus();
            return;
        }
        if (editTextStorePostalCode.getText().toString().trim().isEmpty()) {
            editTextStorePostalCode.setError("Store Postal Code is required.");
            editTextStorePostalCode.requestFocus();
            return;
        }
        if (editTextStoreCountry.getText().toString().trim().isEmpty()) {
            editTextStoreCountry.setError("Store Country is required.");
            editTextStoreCountry.requestFocus();
            return;
        }

        // Validate main product image is selected (NEW VALIDATION)
        if (mainProductImageBase64 == null || mainProductImageBase64.isEmpty()) {
            Toast.makeText(this, "Please select a main product image.", Toast.LENGTH_SHORT).show();
            return;
        }


        final int category_id = categoriesList.get(spinnerCategory.getSelectedItemPosition() - 1).getId();

        final String productName = editTextProductName.getText().toString().trim();
        final String productDescription = editTextProductDescription.getText().toString().trim();
        final String price = editTextPrice.getText().toString().trim();
        final String stockQuantity = editTextStockQuantity.getText().toString().trim();
        final String productCondition = ((RadioButton) findViewById(selectedConditionId)).getText().toString();

        final String sku = editTextSku.getText().toString().trim();
        final String brand = editTextBrand.getText().toString().trim();
        final String model = editTextModel.getText().toString().trim();
        final String color = editTextColor.getText().toString().trim();
        final String keywords = editTextKeywords.getText().toString().trim();
        final String compatibility = editTextCompatibility.getText().toString().trim();
        final String warrantyPolicy = editTextWarrantyPolicy.getText().toString().trim();
        final String weight = editTextWeight.getText().toString().trim();
        final String dimensions = editTextDimensions.getText().toString().trim();
        final String material = editTextMaterial.getText().toString().trim();
        final String manufacturingDate = editTextManufacturingDate.getText().toString().trim();
        final String expirationDate = editTextExpirationDate.getText().toString().trim();

        final String storeAddressLine1 = editTextStoreAddressLine1.getText().toString().trim();
        final String storeAddressLine2 = editTextStoreAddressLine2.getText().toString().trim();
        final String storeCity = editTextStoreCity.getText().toString().trim();
        final String storeDistrict = editTextStoreDistrict.getText().toString().trim();
        final String storePostalCode = editTextStorePostalCode.getText().toString().trim();
        final String storeCountry = editTextStoreCountry.getText().toString().trim();
        final String deliveryFee = editTextDeliveryFee.getText().toString().trim();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, ADD_PRODUCT_URL,
                response -> {
                    Log.d(TAG, "Add Product Response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        String message = jsonResponse.getString("message");
                        if (success) {
                            Toast.makeText(AddProductActivity.this, message, Toast.LENGTH_LONG).show();
                            int productId = jsonResponse.optInt("product_id", -1);
                            if (productId != -1) {
                                uploadMainProductImage(productId, productName);
                            } else {
                                finish();
                            }
                        } else {
                            Toast.makeText(AddProductActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error for add product response: " + e.getMessage() + " Response: " + response);
                        Toast.makeText(AddProductActivity.this, "Error processing add product response.", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Volley error adding product: " + error.getMessage());
                    Toast.makeText(AddProductActivity.this, "Network error adding product.", Toast.LENGTH_LONG).show();
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("seller_id", String.valueOf(sellerId));
                params.put("category_id", String.valueOf(category_id));
                params.put("product_name", productName);
                params.put("product_description", productDescription);
                params.put("price", price);
                params.put("stock_quantity", stockQuantity);
                params.put("product_condition", productCondition);

                if (!sku.isEmpty()) params.put("sku", sku);
                if (!brand.isEmpty()) params.put("brand", brand);
                if (!model.isEmpty()) params.put("model", model);
                if (!color.isEmpty()) params.put("color", color);
                if (!keywords.isEmpty()) params.put("keywords", keywords);
                if (!compatibility.isEmpty()) params.put("compatibility", compatibility);
                if (!warrantyPolicy.isEmpty()) params.put("warranty_policy", warrantyPolicy);
                if (!weight.isEmpty()) params.put("weight_g", weight);
                if (!dimensions.isEmpty()) params.put("dimensions_cm", dimensions);
                if (!material.isEmpty()) params.put("material", material);
                if (!manufacturingDate.isEmpty()) params.put("manufacturing_date", manufacturingDate);
                if (!expirationDate.isEmpty()) params.put("expiration_date", expirationDate);

                params.put("store_address_line1", storeAddressLine1);
                if (!storeAddressLine2.isEmpty()) params.put("store_address_line2", storeAddressLine2);
                params.put("store_city", storeCity);
                if (!storeDistrict.isEmpty()) params.put("store_district", storeDistrict);
                if (!storePostalCode.isEmpty()) params.put("store_postal_code", storePostalCode);
                if (!storeCountry.isEmpty()) params.put("store_country", storeCountry);
                if (!deliveryFee.isEmpty()) params.put("delivery_fee", deliveryFee);

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    /**
     * Uploads the main product image (if selected) and then proceeds to add variants if any.
     * @param productId The ID of the newly added product.
     * @param productName For naming the image file.
     */
    private void uploadMainProductImage(int productId, final String productName) {
        if (mainProductImageBase64 != null && !mainProductImageBase64.isEmpty()) {
            StringRequest uploadImageRequest = new StringRequest(Request.Method.POST, UPLOAD_IMAGE_URL,
                    response -> {
                        Log.d(TAG, "Main Image Upload Response: " + response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            String message = jsonResponse.getString("message");
                            if (success) {
                                String imageUrl = jsonResponse.getString("image_url");
                                Toast.makeText(AddProductActivity.this, "Main image uploaded!", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e(TAG, "Failed to upload main image: " + message);
                                Toast.makeText(AddProductActivity.this, "Failed to upload main image.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON parsing error for main image upload response: " + e.getMessage());
                            Toast.makeText(AddProductActivity.this, "Error processing main image upload response.", Toast.LENGTH_LONG).show();
                        }
                        addProductVariantsAndImages(productId);
                    },
                    error -> {
                        Log.e(TAG, "Volley error uploading main image: " + error.getMessage());
                        Toast.makeText(AddProductActivity.this, "Network error uploading main image.", Toast.LENGTH_LONG).show();
                        addProductVariantsAndImages(productId);
                    }) {
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("image", mainProductImageBase64);
                    params.put("product_id", String.valueOf(productId));
                    params.put("is_thumbnail", "1");
                    params.put("filename_hint", productName);
                    return params;
                }
            };
            requestQueue.add(uploadImageRequest);
        } else {
            addProductVariantsAndImages(productId);
        }
    }


    /**
     * Handles uploading variant images (if present) and then adding variants.
     * This is an asynchronous process.
     * @param productId The ID of the newly added product.
     */
    private void addProductVariantsAndImages(int productId) {
        if (productVariants.isEmpty()) {
            finish();
            return;
        }

        uploadNextVariantImage(productId, 0);
    }

    private void uploadNextVariantImage(int productId, int currentIndex) {
        if (currentIndex >= productVariants.size()) {
            sendProductVariants(productId);
            return;
        }

        ProductVariant currentVariant = productVariants.get(currentIndex);
        if (currentVariant.getImageBase64() != null && !currentVariant.getImageBase64().isEmpty()) {
            StringRequest uploadImageRequest = new StringRequest(Request.Method.POST, UPLOAD_IMAGE_URL,
                    response -> {
                        Log.d(TAG, "Image Upload Response for variant " + currentVariant.getName() + ": " + response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            String message = jsonResponse.getString("message");
                            if (success) {
                                String imageUrl = jsonResponse.getString("image_url");
                                currentVariant.setImageUrl(imageUrl);
                                Toast.makeText(AddProductActivity.this, "Image uploaded for " + currentVariant.getName(), Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e(TAG, "Failed to upload image for variant " + currentVariant.getName() + ": " + message);
                                Toast.makeText(AddProductActivity.this, "Failed to upload image for " + currentVariant.getName(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON parsing error for image upload response: " + e.getMessage());
                            Toast.makeText(AddProductActivity.this, "Error processing image upload response.", Toast.LENGTH_LONG).show();
                        }
                        uploadNextVariantImage(productId, currentIndex + 1);
                    },
                    error -> {
                        Log.e(TAG, "Volley error uploading image for variant " + currentVariant.getName() + ": " + error.getMessage());
                        Toast.makeText(AddProductActivity.this, "Network error uploading image for " + currentVariant.getName(), Toast.LENGTH_LONG).show();
                        uploadNextVariantImage(productId, currentIndex + 1);
                    }) {
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("image", currentVariant.getImageBase64());
                    params.put("product_id", String.valueOf(productId));
                    params.put("variant_name", currentVariant.getName());
                    params.put("is_thumbnail", "0");
                    return params;
                }
            };
            requestQueue.add(uploadImageRequest);
        } else {
            uploadNextVariantImage(productId, currentIndex + 1);
        }
    }


    /**
     * Sends a request to add all stored product variants for a given product ID.
     * This method will be called after all images (if any) have been uploaded.
     * @param productId The ID of the newly added product.
     */
    private void sendProductVariants(int productId) {
        if (productVariants.isEmpty()) {
            finish();
            return;
        }

        JSONArray variantsJsonArray = new JSONArray();
        for (ProductVariant variant : productVariants) {
            try {
                JSONObject variantObject = new JSONObject();
                variantObject.put("variant_name", variant.getName());
                variantObject.put("variant_price", variant.getPrice().toPlainString());
                variantObject.put("variant_stock_quantity", variant.getStockQuantity());
                if (variant.getImageUrl() != null && !variant.getImageUrl().isEmpty()) {
                    variantObject.put("variant_image_url", variant.getImageUrl());
                } else {
                    variantObject.put("variant_image_url", JSONObject.NULL);
                }
                variantsJsonArray.put(variantObject);
            } catch (JSONException e) {
                Log.e(TAG, "Error creating variant JSON: " + e.getMessage());
            }
        }

        final String finalVariantsJson = variantsJsonArray.toString();
        Log.d(TAG, "Sending variants JSON for product " + productId + ": " + finalVariantsJson);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ADD_PRODUCT_VARIANTS_URL,
                response -> {
                    Log.d(TAG, "Add Variants Response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        String message = jsonResponse.getString("message");
                        if (success) {
                            Toast.makeText(AddProductActivity.this, "Variants added successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AddProductActivity.this, "Failed to add variants: " + message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error for add variants response: " + e.getMessage() + " Response: " + response);
                        Toast.makeText(AddProductActivity.this, "Error processing add variants response.", Toast.LENGTH_LONG).show();
                    }
                    finish();
                },
                error -> {
                    Log.e(TAG, "Volley error adding variants: " + error.getMessage());
                    Toast.makeText(AddProductActivity.this, "Network error adding variants.", Toast.LENGTH_LONG).show();
                    finish();
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("product_id", String.valueOf(productId));
                params.put("variants_json", finalVariantsJson);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }


    // Simple helper class for Category data
    private static class Category {
        private int id;
        private String name;

        public Category(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() { return id; }
        public String getName() { return name; }

        @Override
        public String toString() {
            return name;
        }
    }

    // Simple helper class for Product Variant data (temporarily held in activity)
    private static class ProductVariant {
        private String name;
        private BigDecimal price;
        private int stockQuantity;
        private String imageBase64; // Temporarily holds Base64 for upload
        private String imageUrl; // Holds the URL returned after upload

        public ProductVariant(String name, BigDecimal price, int stockQuantity, String imageBase64) {
            this.name = name;
            this.price = price;
            this.stockQuantity = stockQuantity;
            this.imageBase64 = imageBase64;
        }

        public String getName() { return name; }
        public BigDecimal getPrice() { return price; }
        public int getStockQuantity() { return stockQuantity; }
        public String getImageBase64() { return imageBase64; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    }
}
