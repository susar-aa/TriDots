package com.example.tridots.adapters; // Ensure this package declaration is correct for your project structure

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tridots.models.SoldOrderHeader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// REQUIRED IMPORT: This line was likely missing or incorrect
import com.example.tridots.SoldProductsStatusFragment;

public class SoldProductsPagerAdapter extends FragmentStateAdapter {

    private final String[] tabTitles;
    private final Map<String, ArrayList<SoldOrderHeader>> groupedOrderHeaders;

    // A map to keep track of fragment instances for updating
    private final Map<String, SoldProductsStatusFragment> fragmentMap = new HashMap<>();

    public SoldProductsPagerAdapter(@NonNull FragmentActivity fragmentActivity, String[] tabTitles, Map<String, ArrayList<SoldOrderHeader>> groupedOrderHeaders) {
        super(fragmentActivity);
        this.tabTitles = tabTitles;
        this.groupedOrderHeaders = groupedOrderHeaders;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        String status = tabTitles[position];
        // Ensure that the list retrieved from groupedOrderHeaders is always an ArrayList
        // as newInstance expects ArrayList<SoldOrderHeader>
        ArrayList<SoldOrderHeader> ordersForStatus = groupedOrderHeaders.getOrDefault(status, new ArrayList<>());

        // Create new fragment instance
        SoldProductsStatusFragment fragment = SoldProductsStatusFragment.newInstance(ordersForStatus);

        // Store fragment instance in the map for potential future updates
        fragmentMap.put(status, fragment);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return tabTitles.length;
    }

    public String getTabTitle(int position) {
        return tabTitles[position];
    }

    /**
     * Method to update a specific fragment's data.
     * This is useful when the underlying data changes after the adapter is initially set up.
     * @param status The status key (tab title) for which to update the fragment data.
     * @param newOrders The new list of SoldOrderHeader objects for this status.
     */
    public void updateFragmentData(String status, List<SoldOrderHeader> newOrders) {
        SoldProductsStatusFragment fragment = fragmentMap.get(status);
        if (fragment != null) {
            fragment.updateData(newOrders);
        }
    }
}