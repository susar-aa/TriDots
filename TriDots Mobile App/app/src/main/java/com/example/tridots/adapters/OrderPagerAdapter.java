// File: app/src/main/java/com/example/tridots/adapters/OrderPagerAdapter.java
package com.example.tridots.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tridots.Marketplace.OrderListFragment; // Ensure correct import
import com.example.tridots.models.Order;

import java.util.ArrayList;
import java.util.LinkedHashMap; // To maintain insertion order of statuses
import java.util.List;
import android.util.Log; // Import Log for debugging

public class OrderPagerAdapter extends FragmentStateAdapter {

    private final LinkedHashMap<String, ArrayList<Order>> ordersByStatus = new LinkedHashMap<>();
    private final List<String> tabTitles = new ArrayList<>();

    private MyOrdersAdapter.OnItemClickListener itemClickListener;
    private MyOrdersAdapter.OnCancelClickListener cancelClickListener; // Corrected: Reference to the cancel listener
    private static final String TAG = "OrderPagerAdapter"; // Tag for logging

    // Constructor now takes both listeners
    public OrderPagerAdapter(@NonNull FragmentActivity fragmentActivity,
                             MyOrdersAdapter.OnItemClickListener itemListener,
                             MyOrdersAdapter.OnCancelClickListener cancelListener) { // Corrected: Receive cancelListener
        super(fragmentActivity);
        this.itemClickListener = itemListener;
        this.cancelClickListener = cancelListener; // Assign the new listener
        Log.d(TAG, "OrderPagerAdapter initialized.");

        ordersByStatus.put("All", new ArrayList<>());
        ordersByStatus.put("Pending", new ArrayList<>());
        ordersByStatus.put("Processing", new ArrayList<>());
        ordersByStatus.put("Shipped", new ArrayList<>());
        ordersByStatus.put("Delivered", new ArrayList<>());
        ordersByStatus.put("Cancelled", new ArrayList<>());
        ordersByStatus.put("Refunded", new ArrayList<>());

        tabTitles.addAll(ordersByStatus.keySet());
        Log.d(TAG, "Tab titles: " + tabTitles);
    }

    /**
     * Updates the data for all tabs. Filters the main list of orders into status-specific lists.
     */
    public void updateAllOrders(List<Order> allOrders) {
        Log.d(TAG, "Updating all orders in PagerAdapter. Total orders: " + allOrders.size());
        for (ArrayList<Order> list : ordersByStatus.values()) {
            list.clear();
        }

        ordersByStatus.get("All").addAll(allOrders);

        for (Order order : allOrders) {
            String status = order.getOrderStatus();
            if (ordersByStatus.containsKey(status)) {
                ordersByStatus.get(status).add(order);
            } else {
                Log.w(TAG, "Unknown order status encountered: " + status + " for order ID: " + order.getOrderId());
            }
        }
        notifyDataSetChanged();
        Log.d(TAG, "PagerAdapter data updated. Number of tabs: " + getItemCount());
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        String status = tabTitles.get(position);
        ArrayList<Order> fragmentOrders = ordersByStatus.get(status);
        Log.d(TAG, "createFragment for status '" + status + "' at position " + position + ". Orders count: " + (fragmentOrders != null ? fragmentOrders.size() : 0));

        OrderListFragment fragment = OrderListFragment.newInstance(fragmentOrders);
        fragment.setOnItemClickListener(itemClickListener); // Pass item click listener
        fragment.setOnCancelClickListener(cancelClickListener); // Corrected: Pass cancel click listener
        return fragment;
    }

    @Override
    public int getItemCount() {
        return tabTitles.size();
    }

    public CharSequence getPageTitle(int position) {
        return tabTitles.get(position);
    }
}
