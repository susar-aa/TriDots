package com.example.tridots;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tridots.R;
import com.example.tridots.adapters.SoldOrdersAdapter;
import com.example.tridots.models.SoldOrderHeader;

import java.util.ArrayList;
import java.util.List;

public class SoldProductsStatusFragment extends Fragment {

    private static final String ARG_ORDER_HEADERS = "order_headers";

    private RecyclerView recyclerView;
    private TextView emptyTextView;
    private SoldOrdersAdapter adapter;
    private List<SoldOrderHeader> orderHeaders; // This needs to be reliably initialized

    public SoldProductsStatusFragment() {
        // Required empty public constructor
        // Ensure orderHeaders is never null, even before arguments are parsed
        orderHeaders = new ArrayList<>();
    }

    public static SoldProductsStatusFragment newInstance(ArrayList<SoldOrderHeader> orderHeaders) {
        SoldProductsStatusFragment fragment = new SoldProductsStatusFragment();
        Bundle args = new Bundle();
        // Defensive copy to prevent concurrent modification issues if original list changes
        args.putSerializable(ARG_ORDER_HEADERS, new ArrayList<>(orderHeaders));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize from arguments. If arguments are null, it remains an empty ArrayList from constructor.
        if (getArguments() != null) {
            List<SoldOrderHeader> listFromArgs = (List<SoldOrderHeader>) getArguments().getSerializable(ARG_ORDER_HEADERS);
            if (listFromArgs != null) {
                orderHeaders.addAll(listFromArgs);
            }
        }
        // At this point, orderHeaders should be a valid (possibly empty) ArrayList.
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sold_products_status, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewFragmentSoldProducts);
        emptyTextView = view.findViewById(R.id.emptyTextView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Pass the already initialized orderHeaders list to the adapter
        adapter = new SoldOrdersAdapter(orderHeaders, getContext());

        if (getActivity() instanceof SoldOrdersAdapter.OnItemStatusChangeListener) {
            adapter.setOnItemStatusChangeListener((SoldOrdersAdapter.OnItemStatusChangeListener) getActivity());
            Log.d("SoldStatusFragment", "Listener set on adapter.");
        } else {
            Log.e("SoldStatusFragment", "Host Activity does not implement OnItemStatusChangeListener!");
        }

        recyclerView.setAdapter(adapter);

        updateEmptyView();

        return view;
    }

    // Call this method if data changes after fragment creation (e.g., on a refresh)
    public void updateData(List<SoldOrderHeader> newOrderHeaders) {
        // Ensure orderHeaders is never null before attempting to clear/add
        if (orderHeaders == null) {
            orderHeaders = new ArrayList<>(); // Defensive initialization
        }
        orderHeaders.clear(); // Line 83 (or close to it)
        if (newOrderHeaders != null) { // Also check newOrderHeaders for null
            orderHeaders.addAll(newOrderHeaders);
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
            updateEmptyView();
        }
    }

    private void updateEmptyView() {
        // Ensure orderHeaders is not null here either
        if (orderHeaders == null || orderHeaders.isEmpty()) {
            emptyTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}