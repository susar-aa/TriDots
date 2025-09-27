// File: app/src/main/java/com/example/tridots/main_screens/MarketplaceListFragment.java
package com.example.tridots.Marketplace;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.util.Log;

import com.example.tridots.R;
import com.example.tridots.adapters.MarketplaceProductAdapter; // Import MarketplaceProductAdapter
import com.example.tridots.models.Product; // Import the Product model

import java.io.Serializable; // Needed for passing ArrayList<Product>
import java.util.ArrayList;
import java.util.List;


public class MarketplaceListFragment extends Fragment {

    private static final String ARG_PRODUCT_LIST = "product_list";
    private static final String TAG = "MarketplaceListFrag";

    private RecyclerView recyclerView;
    private MarketplaceProductAdapter adapter;
    private List<Product> productList;
    private TextView textViewEmptyList;

    // Declared: If you need click listeners, declare them here and pass via constructor or setter
    private MarketplaceProductAdapter.OnItemClickListener itemClickListener; // Now properly declared


    public MarketplaceListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param products List of products for this fragment to display.
     * @return A new instance of fragment MarketplaceListFragment.
     */
    public static MarketplaceListFragment newInstance(ArrayList<Product> products) {
        MarketplaceListFragment fragment = new MarketplaceListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PRODUCT_LIST, products); // Pass list as Serializable
        fragment.setArguments(args);
        Log.d(TAG, "newInstance: Created fragment with " + (products != null ? products.size() : 0) + " products.");
        return fragment;
    }

    // Setter for the item click listener (called by hosting activity)
    public void setOnItemClickListener(MarketplaceProductAdapter.OnItemClickListener listener) {
        this.itemClickListener = listener;
        Log.d(TAG, "setOnItemClickListener: Listener set.");
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Retrieve the product list from arguments. Cast as ArrayList<Product> since it was passed as Serializable.
            productList = (List<Product>) getArguments().getSerializable(ARG_PRODUCT_LIST);
            if (productList == null) {
                productList = new ArrayList<>(); // Ensure it's not null
            }
        } else {
            productList = new ArrayList<>();
        }
        Log.d(TAG, "onCreate: Fragment productList size: " + productList.size());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_marketplace_list, container, false);
        Log.d(TAG, "onCreateView: Layout inflated.");

        recyclerView = view.findViewById(R.id.recyclerViewMarketplaceProductList);
        textViewEmptyList = view.findViewById(R.id.textViewMarketplaceEmptyList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize adapter with the productList and the itemClickListener
        // Now passing itemClickListener as required by the adapter's constructor
        Log.d(TAG, "onCreateView: Initializing MarketplaceProductAdapter with listener=" + (itemClickListener != null));
        adapter = new MarketplaceProductAdapter(productList, itemClickListener); // Corrected: Pass itemClickListener
        recyclerView.setAdapter(adapter);

        updateEmptyListMessage(); // Update message visibility initially

        return view;
    }

    /**
     * Updates the data in the RecyclerView adapter and refreshes the empty list message.
     * @param newProducts The new list of products to display.
     */
    public void updateData(List<Product> newProducts) {
        if (adapter != null) {
            adapter.updateProducts(newProducts);
            Log.d(TAG, "updateData: Adapter updated with " + newProducts.size() + " new products.");
            updateEmptyListMessage(); // Update message visibility after data changes
        } else {
            productList.clear();
            productList.addAll(newProducts);
            Log.d(TAG, "updateData: Adapter not yet created, updating internal list. Size: " + productList.size());
        }
    }

    private void updateEmptyListMessage() {
        if (productList.isEmpty()) {
            textViewEmptyList.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            textViewEmptyList.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
