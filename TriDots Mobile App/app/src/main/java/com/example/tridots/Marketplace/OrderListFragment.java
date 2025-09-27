// File: app/src/main/java/com/example/tridots/main_screens/OrderListFragment.java
package com.example.tridots.Marketplace; // Corrected: Ensure this package matches your file path

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.tridots.R;
import com.example.tridots.adapters.MyOrdersAdapter;
import com.example.tridots.models.Order;
import java.util.ArrayList;
import java.util.List;
import android.util.Log; // Import Log for debugging

public class OrderListFragment extends Fragment {

    private static final String ARG_ORDER_LIST = "order_list";
    private static final String TAG = "OrderListFragment"; // Tag for logging

    private RecyclerView recyclerView;
    private MyOrdersAdapter adapter;
    private List<Order> orderList;
    private MyOrdersAdapter.OnItemClickListener itemClickListener; // From activity
    private MyOrdersAdapter.OnCancelClickListener cancelClickListener; // New: From activity

    public OrderListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param orders List of orders for this fragment to display.
     * @return A new instance of fragment OrderListFragment.
     */
    public static OrderListFragment newInstance(ArrayList<Order> orders) {
        OrderListFragment fragment = new OrderListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ORDER_LIST, orders); // Pass list as Serializable
        fragment.setArguments(args);
        Log.d(TAG, "newInstance: Created fragment with " + (orders != null ? orders.size() : 0) + " orders.");
        return fragment;
    }

    // Setters for the item click and cancel click listeners
    public void setOnItemClickListener(MyOrdersAdapter.OnItemClickListener listener) {
        this.itemClickListener = listener;
        Log.d(TAG, "setOnItemClickListener: Listener set.");
    }

    public void setOnCancelClickListener(MyOrdersAdapter.OnCancelClickListener listener) {
        this.cancelClickListener = listener;
        Log.d(TAG, "setOnCancelClickListener: Listener set.");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            orderList = (List<Order>) getArguments().getSerializable(ARG_ORDER_LIST);
            if (orderList == null) {
                orderList = new ArrayList<>();
            }
        } else {
            orderList = new ArrayList<>();
        }
        Log.d(TAG, "onCreate: Fragment orderList size: " + orderList.size());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);
        Log.d(TAG, "onCreateView: Layout inflated.");

        recyclerView = view.findViewById(R.id.recyclerViewOrderList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize adapter passing both listeners: itemClickListener and cancelClickListener
        // These listeners are set by the hosting Activity (MyOrdersActivity) after fragment creation.
        Log.d(TAG, "onCreateView: Initializing MyOrdersAdapter with itemListener=" + (itemClickListener != null) + " and cancelListener=" + (cancelClickListener != null));
        adapter = new MyOrdersAdapter(orderList, itemClickListener, cancelClickListener); // Corrected: Pass cancelClickListener
        recyclerView.setAdapter(adapter);

        return view;
    }

    /**
     * Updates the data in the RecyclerView adapter.
     */
    public void updateData(List<Order> newOrders) {
        if (adapter != null) {
            adapter.updateOrders(newOrders);
            Log.d(TAG, "updateData: Adapter updated with " + newOrders.size() + " new orders.");
        } else {
            orderList.clear();
            orderList.addAll(newOrders);
            Log.d(TAG, "updateData: Adapter not yet created, updating internal list. Size: " + orderList.size());
        }
    }
}
