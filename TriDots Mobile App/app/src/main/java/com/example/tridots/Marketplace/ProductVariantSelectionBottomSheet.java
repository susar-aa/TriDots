package com.example.tridots.Marketplace;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tridots.R;
import com.example.tridots.models.Product;
import com.example.tridots.adapters.VariantAdapter; // NEW IMPORT

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.math.BigDecimal;
import java.util.Locale;

public class ProductVariantSelectionBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_PRODUCT = "product";
    private Product product;
    private Product.Variant selectedVariant; // Tracks the currently selected variant in the dialog
    private int currentQuantity = 1;

    private RecyclerView variantsRecyclerView;
    private VariantAdapter variantAdapter;
    private TextView quantityTextView;
    private TextView priceTextView; // Displays the price of the selected variant
    private ImageView productImageView; // Displays the image of the selected variant
    private TextView variantSelectionTitle; // E.g., "Color" or "Type"

    private OnVariantSelectedListener listener;

    // Interface for communicating selection back to the Activity
    public interface OnVariantSelectedListener {
        void onVariantSelected(Product selectedProduct, Product.Variant selectedVariant, int quantity);
    }

    // Factory method to create a new instance with arguments
    public static ProductVariantSelectionBottomSheet newInstance(Product product) {
        ProductVariantSelectionBottomSheet fragment = new ProductVariantSelectionBottomSheet();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PRODUCT, product); // Product must be Serializable or Parcelable
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Ensure the hosting activity implements the listener interface
        if (context instanceof OnVariantSelectedListener) {
            listener = (OnVariantSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnVariantSelectedListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            product = (Product) getArguments().getSerializable(ARG_PRODUCT);
            // Default selection: If variants exist, select the first one.
            if (product != null && product.getVariants() != null && !product.getVariants().isEmpty()) {
                selectedVariant = product.getVariants().get(0);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_variant_selection, container, false);

        // Initialize UI components
        variantsRecyclerView = view.findViewById(R.id.variantsRecyclerView);
        quantityTextView = view.findViewById(R.id.quantityTextView);
        Button minusButton = view.findViewById(R.id.minusButton);
        Button plusButton = view.findViewById(R.id.plusButton);
        Button confirmButton = view.findViewById(R.id.confirmButton);
        priceTextView = view.findViewById(R.id.priceTextView);
        productImageView = view.findViewById(R.id.productImageView);
        variantSelectionTitle = view.findViewById(R.id.variantSelectionTitle); // Assuming you add this TextView

        // Set initial quantity display
        quantityTextView.setText(String.valueOf(currentQuantity));

        if (product != null) {
            // Setup Variants RecyclerView
            // Use GridLayoutManager for a grid display like the Daraz example
            variantsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 2 columns for variants

            if (product.getVariants() != null && !product.getVariants().isEmpty()) {
                variantSelectionTitle.setVisibility(View.VISIBLE);
                // Initialize adapter, passing initial selected variant and a click listener
                variantAdapter = new VariantAdapter(getContext(), product.getVariants(), selectedVariant, new VariantAdapter.OnVariantClickListener() {
                    @Override
                    public void onVariantClick(Product.Variant variant) {
                        selectedVariant = variant; // Update the selected variant
                        updateDisplayedPriceAndImage(); // Refresh price and image based on new selection
                    }
                });
                variantsRecyclerView.setAdapter(variantAdapter);
                variantsRecyclerView.setVisibility(View.VISIBLE);
            } else {
                // If no variants, hide the variant selection section
                variantSelectionTitle.setVisibility(View.GONE);
                variantsRecyclerView.setVisibility(View.GONE);
                // If no variants, the 'product' itself is the item to be selected
                selectedVariant = null;
            }

            // Update initial price and image display
            updateDisplayedPriceAndImage();

        } else {
            Toast.makeText(getContext(), "Product data not available.", Toast.LENGTH_SHORT).show();
            dismiss(); // Close dialog if no product data
        }

        // Quantity increase listener
        plusButton.setOnClickListener(v -> {
            int maxStock = 0;
            if (selectedVariant != null) {
                maxStock = selectedVariant.getVariantStockQuantity();
            } else if (product != null) {
                maxStock = product.getStockQuantity(); // Use base product stock if no variants
            }

            if (currentQuantity < maxStock) {
                currentQuantity++;
                quantityTextView.setText(String.valueOf(currentQuantity));
                updateDisplayedPriceAndImage();
            } else {
                Toast.makeText(getContext(), "Maximum stock reached.", Toast.LENGTH_SHORT).show();
            }
        });

        // Quantity decrease listener
        minusButton.setOnClickListener(v -> {
            if (currentQuantity > 1) {
                currentQuantity--;
                quantityTextView.setText(String.valueOf(currentQuantity));
                updateDisplayedPriceAndImage();
            }
        });

        // Confirm button logic
        confirmButton.setOnClickListener(v -> {
            if (listener != null) {
                if (product.getVariants() != null && !product.getVariants().isEmpty() && selectedVariant == null) {
                    // Only require variant selection if variants actually exist
                    Toast.makeText(getContext(), "Please select a variant.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (currentQuantity <= 0) {
                    Toast.makeText(getContext(), "Quantity must be at least 1.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Notify the activity with the selected product, variant, and quantity
                listener.onVariantSelected(product, selectedVariant, currentQuantity);
                dismiss(); // Close the bottom sheet after selection
            }
        });

        return view;
    }

    // Helper method to update price and image based on current selection
    private void updateDisplayedPriceAndImage() {
        BigDecimal displayPrice = BigDecimal.ZERO;
        String imageUrlToLoad = null;

        if (selectedVariant != null) {
            displayPrice = selectedVariant.getVariantPrice();
            imageUrlToLoad = selectedVariant.getVariantImageUrl();
        } else if (product != null) {
            // Fallback to base product price if no variant is selected/available
            displayPrice = product.getPrice();
            // Fallback to first product image if no variant image
            if (product.getImages() != null && !product.getImages().isEmpty()) {
                imageUrlToLoad = product.getImages().get(0).getImageUrl();
            }
        }

        priceTextView.setText(String.format(Locale.getDefault(), "Rs. %.2f", displayPrice.multiply(BigDecimal.valueOf(currentQuantity))));

        // Load image using Glide
        if (imageUrlToLoad != null && !imageUrlToLoad.isEmpty()) {
            Glide.with(this)
                    .load(imageUrlToLoad)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_placeholdeers)
                    .into(productImageView);
        } else {
            productImageView.setImageResource(R.drawable.placeholder_image);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null; // Avoid memory leaks
    }
}