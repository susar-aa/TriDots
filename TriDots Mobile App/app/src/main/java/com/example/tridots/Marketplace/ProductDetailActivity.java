// Path: app/src/main/java/com/example/tridots/Marketplace/ProductDetailActivity.java

package com.example.tridots.Marketplace;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.transition.TransitionManager; // IMPORT TransitionManager
import androidx.viewpager2.widget.ViewPager2;

import com.example.tridots.R;
import com.example.tridots.detailed_screens.SellerDetailActivity;
import com.example.tridots.adapters.ProductImageAdapter;
import com.example.tridots.models.Product;
import com.example.tridots.system.VolleySingleton;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity implements ProductVariantSelectionBottomSheet.OnVariantSelectedListener {

    // --- UI Components ---
    private TextView productName, productDescription, productPrice, productStock, textViewBrand,
            textViewProductCondition, textViewWarrantyPolicy, textViewColor, textViewMaterial,
            textViewWeightG, textViewDimensionsCm, textViewSku;
    private ViewPager2 productImageViewPager;
    private Button buttonAddToCart;
    private MaterialButton sellerInfoButton, previewImageButton;
    private Product currentProduct;

    // --- Expandable Section Components ---
    private RelativeLayout specificationsHeader;
    private LinearLayout specificationsLayout;
    private ImageView expandIcon;

    // --- Constants ---
    private static final String API_URL = "https://lionsgoldencircle.com/Tridots/Api/get_product_details.php";
    public static final String EXTRA_PRODUCT_ID = "product_id";
    public static final String EXTRA_FROM_MANAGE_MARKETPLACE = "from_manage_marketplace";

    private boolean isFromManageMarketplace = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        initializeViews();

        int productId = getIntent().getIntExtra(EXTRA_PRODUCT_ID, -1);
        isFromManageMarketplace = getIntent().getBooleanExtra(EXTRA_FROM_MANAGE_MARKETPLACE, false);

        if (productId != -1) {
            fetchProductDetails(productId);
        } else {
            Toast.makeText(this, "Product ID not found.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        productName = findViewById(R.id.textViewProductNameDetail);
        productDescription = findViewById(R.id.textViewProductDescriptionDetail);
        productPrice = findViewById(R.id.textViewProductPriceDetail);
        productStock = findViewById(R.id.textViewProductStockDetail);
        productImageViewPager = findViewById(R.id.productImageViewPager);
        buttonAddToCart = findViewById(R.id.buttonAddToCart);
        textViewBrand = findViewById(R.id.textViewBrand);

        textViewProductCondition = findViewById(R.id.textViewProductCondition);
        textViewWarrantyPolicy = findViewById(R.id.textViewWarrantyPolicy);
        textViewColor = findViewById(R.id.textViewColor);
        textViewMaterial = findViewById(R.id.textViewMaterial);
        textViewWeightG = findViewById(R.id.textViewWeightG);
        textViewDimensionsCm = findViewById(R.id.textViewDimensionsCm);
        textViewSku = findViewById(R.id.textViewSku);

        sellerInfoButton = findViewById(R.id.sellerInfoButton);
        previewImageButton = findViewById(R.id.previewImageButton);

        specificationsHeader = findViewById(R.id.specificationsHeader);
        specificationsLayout = findViewById(R.id.specificationsLayout);
        expandIcon = findViewById(R.id.expandIcon);
    }

    private void fetchProductDetails(int productId) {
        String url = API_URL + "?product_id=" + productId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject productObject = new JSONObject(response);
                        if (productObject.has("error")) {
                            Toast.makeText(this, "Error: " + productObject.getString("error"), Toast.LENGTH_LONG).show();
                            finish();
                            return;
                        }

                        currentProduct = parseProduct(productObject);
                        displayProductDetails(currentProduct);
                        setupListeners();

                    } catch (JSONException e) {
                        Log.e("ProductDetailActivity", "JSON Parsing Error: " + e.getMessage());
                        Toast.makeText(this, "Error parsing product details.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                },
                error -> {
                    Log.e("ProductDetailActivity", "Volley Error: " + error.toString());
                    Toast.makeText(this, "Failed to load product details.", Toast.LENGTH_LONG).show();
                    finish();
                });

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private Product parseProduct(JSONObject productObject) throws JSONException {
        Product product = new Product();
        product.setProductId(productObject.optInt("product_id"));
        product.setSellerId(productObject.optInt("seller_id"));
        product.setProductName(productObject.optString("product_name"));
        product.setProductDescription(productObject.optString("product_description"));
        product.setPrice(parseBigDecimal(productObject.optString("price")));
        product.setStockQuantity(productObject.optInt("stock_quantity"));
        product.setProductCondition(productObject.optString("product_condition"));
        product.setBrand(productObject.optString("brand"));
        product.setWarrantyPolicy(productObject.optString("warranty_policy"));
        product.setColor(productObject.optString("color"));
        product.setMaterial(productObject.optString("material"));
        product.setWeightG(productObject.optInt("weight_g"));
        product.setDimensionsCm(productObject.optString("dimensions_cm"));
        product.setSku(productObject.optString("sku"));

        List<Product.Image> images = new ArrayList<>();
        if (productObject.has("images") && !productObject.isNull("images")) {
            JSONArray imagesArray = productObject.getJSONArray("images");
            for (int i = 0; i < imagesArray.length(); i++) {
                JSONObject imageObj = imagesArray.getJSONObject(i);
                images.add(new Product.Image(
                        imageObj.optInt("image_id"),
                        imageObj.optInt("product_id"),
                        imageObj.optString("image_url"),
                        imageObj.optInt("is_thumbnail") == 1,
                        imageObj.optInt("display_order")
                ));
            }
        }
        product.setImages(images);

        List<Product.Variant> variants = new ArrayList<>();
        if (productObject.has("variants") && !productObject.isNull("variants")) {
            JSONArray variantsArray = productObject.getJSONArray("variants");
            for (int i = 0; i < variantsArray.length(); i++) {
                JSONObject variantObj = variantsArray.getJSONObject(i);
                variants.add(new Product.Variant(
                        variantObj.optInt("variant_id"),
                        variantObj.optInt("product_id"),
                        variantObj.optString("variant_name"),
                        parseBigDecimal(variantObj.optString("variant_price")),
                        variantObj.optInt("variant_stock_quantity"),
                        variantObj.optString("variant_image_url")
                ));
            }
        }
        product.setVariants(variants);

        return product;
    }

    private void displayProductDetails(Product product) {
        productName.setText(product.getProductName());
        productDescription.setText(product.getProductDescription());
        productPrice.setText(String.format(Locale.getDefault(), "LKR %.2f", product.getPrice()));
        productStock.setText(String.format(Locale.getDefault(), "In Stock: %d", product.getStockQuantity()));
        textViewBrand.setText(product.getBrand());

        textViewProductCondition.setText(product.getProductCondition());
        textViewWarrantyPolicy.setText(product.getWarrantyPolicy());
        textViewColor.setText(product.getColor());
        textViewMaterial.setText(product.getMaterial());
        textViewWeightG.setText(String.format(Locale.US, "%d g", product.getWeightG()));
        textViewDimensionsCm.setText(product.getDimensionsCm());
        textViewSku.setText(product.getSku());

        if (isFromManageMarketplace) {
            buttonAddToCart.setVisibility(View.GONE);
        } else {
            buttonAddToCart.setVisibility(View.VISIBLE);
        }

        if (product.getImages() != null && !product.getImages().isEmpty()) {
            ProductImageAdapter imageAdapter = new ProductImageAdapter(this, product.getImages());
            productImageViewPager.setAdapter(imageAdapter);
        }
    }

    private void setupListeners() {
        buttonAddToCart.setOnClickListener(v -> {
            if (currentProduct != null) {
                if (currentProduct.getVariants() != null && !currentProduct.getVariants().isEmpty()) {
                    ProductVariantSelectionBottomSheet.newInstance(currentProduct)
                            .show(getSupportFragmentManager(), "ProductVariantSheet");
                } else {
                    CartManager.getInstance().addProduct(currentProduct, null, 1);
                    Toast.makeText(this, currentProduct.getProductName() + " added to cart!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        sellerInfoButton.setOnClickListener(v -> {
            if (currentProduct != null && currentProduct.getSellerId() > 0) {
                Intent intent = new Intent(this, SellerDetailActivity.class);
                intent.putExtra("user_id", currentProduct.getSellerId());
                startActivity(intent);
            }
        });

        previewImageButton.setOnClickListener(v -> {
            if (currentProduct != null && currentProduct.getImages() != null && !currentProduct.getImages().isEmpty()) {
                showImagePreviewDialog(currentProduct.getImages().get(0).getImageUrl());
            }
        });

        // FIXED: Listener for the expandable section with smooth animation
        specificationsHeader.setOnClickListener(v -> {
            boolean isVisible = specificationsLayout.getVisibility() == View.VISIBLE;
            // This creates a smooth animation for the layout visibility change
            TransitionManager.beginDelayedTransition((ViewGroup) specificationsLayout.getParent());
            specificationsLayout.setVisibility(isVisible ? View.GONE : View.VISIBLE);
            expandIcon.animate().rotation(isVisible ? 0f : 180f).setDuration(300).start();
        });
    }

    private void showImagePreviewDialog(String imageUrl) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_image_preview);

        PhotoView photoView = dialog.findViewById(R.id.photo_view);
        ImageView closeButton = dialog.findViewById(R.id.closeButton);

        Glide.with(this).load(imageUrl).into(photoView);
        closeButton.setOnClickListener(v -> dialog.dismiss());

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        dialog.show();
    }

    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.trim().isEmpty() || value.equalsIgnoreCase("null")) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_cart) {
            startActivity(new Intent(this, CartActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onVariantSelected(Product selectedProduct, Product.Variant selectedVariant, int quantity) {
        if (selectedProduct != null && quantity > 0) {
            CartManager.getInstance().addProduct(selectedProduct, selectedVariant, quantity);
            String confirmationMsg = quantity + " x " + selectedProduct.getProductName();
            if (selectedVariant != null) {
                confirmationMsg += " (" + selectedVariant.getVariantName() + ")";
            }
            confirmationMsg += " added to cart!";
            Toast.makeText(this, confirmationMsg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }
}
