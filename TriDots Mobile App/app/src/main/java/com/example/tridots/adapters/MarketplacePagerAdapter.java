// File: app/src/main/java/com/example/tridots/adapters/MarketplacePagerAdapter.java
package com.example.tridots.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tridots.Marketplace.MarketplaceListFragment; // Ensure correct import
import com.example.tridots.models.Product; // Import Product model

import java.util.ArrayList;
import java.util.LinkedHashMap; // To maintain insertion order of statuses
import java.util.List;
import android.util.Log;

public class MarketplacePagerAdapter extends FragmentStateAdapter {

    private static final String TAG = "MarketplacePagerAdapt";

    // Map to hold products, key = approved_status, value = list of products for that status
    private final LinkedHashMap<String, ArrayList<Product>> productsByStatus = new LinkedHashMap<>();
    private final List<String> tabTitles = new ArrayList<>(); // List of tab titles (statuses)

    // New: Declare the item click listener
    private MarketplaceProductAdapter.OnItemClickListener itemClickListener;

    // Constructor now accepts the OnItemClickListener
    public MarketplacePagerAdapter(@NonNull FragmentActivity fragmentActivity, MarketplaceProductAdapter.OnItemClickListener listener) {
        super(fragmentActivity);
        this.itemClickListener = listener; // Assign the listener
        Log.d(TAG, "MarketplacePagerAdapter initialized.");

        // Define tabs based on approved_status and 'All'
        productsByStatus.put("All", new ArrayList<>());
        productsByStatus.put("Pending", new ArrayList<>());
        productsByStatus.put("Approved", new ArrayList<>());
        productsByStatus.put("Rejected", new ArrayList<>());

        tabTitles.addAll(productsByStatus.keySet()); // Populate tab titles based on map keys
        Log.d(TAG, "Tab titles: " + tabTitles);
    }

    /**
     * Updates the data for all tabs. Filters the main list of products into status-specific lists.
     * @param allProducts The complete list of products fetched from the API.
     */
    public void updateAllProducts(List<Product> allProducts) {
        Log.d(TAG, "Updating all products in PagerAdapter. Total products: " + allProducts.size());
        // Clear all existing status-specific lists
        for (ArrayList<Product> list : productsByStatus.values()) {
            list.clear();
        }

        // Populate the "All" tab
        productsByStatus.get("All").addAll(allProducts);

        // Filter products into their respective status categories
        for (Product product : allProducts) {
            String status = product.getApprovedStatus(); // Use getApproved_status() from your Product model
            if (productsByStatus.containsKey(status)) {
                productsByStatus.get(status).add(product);
            } else {
                Log.w(TAG, "Unknown approved_status encountered: " + status + " for product ID: " + product.getProductId());
            }
        }
        notifyDataSetChanged(); // Notify ViewPager2 that data has changed
        Log.d(TAG, "Marketplace PagerAdapter data updated. Number of tabs: " + getItemCount());
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        String status = tabTitles.get(position);
        ArrayList<Product> fragmentProducts = productsByStatus.get(status);
        Log.d(TAG, "createFragment for status '" + status + "' at position " + position + ". Products count: " + (fragmentProducts != null ? fragmentProducts.size() : 0));

        // Create new fragment instance and pass the relevant product list
        MarketplaceListFragment fragment = MarketplaceListFragment.newInstance(fragmentProducts);
        // Pass the item click listener to the fragment
        fragment.setOnItemClickListener(itemClickListener); // Pass listener
        return fragment;
    }

    @Override
    public int getItemCount() {
        return tabTitles.size(); // Number of tabs
    }

    // Method to get the title for each tab
    public CharSequence getPageTitle(int position) {
        return tabTitles.get(position);
    }
}
