// File: app/src/main/java/com/example/tridots/adapters/MyOrdersAdapter.java
package com.example.tridots.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Button; // Import Button
import android.util.Log; // Import Log for debugging

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tridots.R;
import com.example.tridots.models.Order;
import com.example.tridots.models.OrderItem;

import java.util.ArrayList;
import java.util.List;
import java.text.DecimalFormat;
import java.util.Locale;

public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.OrderViewHolder> {

    // Interface for item click events (for receipt display)
    public interface OnItemClickListener {
        void onItemClick(Order order);
    }

    // Interface for cancel button click events
    public interface OnCancelClickListener {
        void onCancelClick(Order order);
    }

    private List<Order> orderList;
    private OnItemClickListener itemClickListener;
    private OnCancelClickListener cancelClickListener; // New listener for cancel button
    private static final DecimalFormat currencyFormat = new DecimalFormat("0.00");
    private static final String TAG = "MyOrdersAdapter"; // Tag for logging

    // Constructor now takes both listeners
    public MyOrdersAdapter(List<Order> initialOrderList, OnItemClickListener itemListener, OnCancelClickListener cancelListener) {
        this.orderList = new ArrayList<>(initialOrderList);
        this.itemClickListener = itemListener;
        this.cancelClickListener = cancelListener; // Assign the new listener
        Log.d(TAG, "MyOrdersAdapter initialized with " + initialOrderList.size() + " orders.");
    }

    /**
     * Updates the adapter's internal list with new data and notifies the RecyclerView.
     */
    public void updateOrders(List<Order> newOrderList) {
        this.orderList.clear();
        this.orderList.addAll(newOrderList);
        notifyDataSetChanged();
        Log.d(TAG, "Adapter updated with " + newOrderList.size() + " orders.");
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        Log.d(TAG, "onCreateViewHolder: Inflating item_order.xml");
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.orderIdTextView.setText("Order ID: #" + order.getOrderId());
        holder.orderDateTextView.setText("Date: " + order.getOrderDate());
        holder.orderTotalTextView.setText(String.format(Locale.getDefault(), "Total: LKR %.2f", order.getTotalAmount()));
        holder.orderStatusTextView.setText("Status: " + order.getOrderStatus());
        holder.paymentStatusTextView.setText("Payment: " + order.getPaymentStatus());

        StringBuilder itemsSummary = new StringBuilder();
        List<OrderItem> items = order.getItems();
        if (items != null && !items.isEmpty()) {
            for (int i = 0; i < items.size(); i++) {
                OrderItem item = items.get(i);
                itemsSummary.append(item.getProductId());
                itemsSummary.append(" (Qty: ").append(item.getQuantity()).append(")");
                if (i < items.size() - 1) {
                    itemsSummary.append(", ");
                }
            }
            holder.orderItemsSummaryTextView.setText("Items: " + itemsSummary.toString());
            holder.orderItemsSummaryTextView.setVisibility(View.VISIBLE);
        } else {
            holder.orderItemsSummaryTextView.setText("No items found for this order.");
            holder.orderItemsSummaryTextView.setVisibility(View.GONE);
        }

        // Set the item click listener (for whole order item)
        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(order);
            }
        });

        // Control visibility of the Cancel button based on order status
        // Check if status is "Pending" (case-insensitive and trimmed) AND a cancel listener is provided
        boolean isPending = order.getOrderStatus() != null && order.getOrderStatus().trim().equalsIgnoreCase("Pending");
        boolean hasCancelListener = cancelClickListener != null;

        Log.d(TAG, "Order ID: " + order.getOrderId() +
                ", Status: '" + order.getOrderStatus() +
                "', isPending: " + isPending +
                ", hasCancelListener: " + hasCancelListener);

        if (isPending && hasCancelListener) {
            Log.d(TAG, "Showing cancel button for Order ID: " + order.getOrderId());
            holder.buttonCancelOrder.setVisibility(View.VISIBLE);
            holder.buttonCancelOrder.setOnClickListener(v -> {
                cancelClickListener.onCancelClick(order); // Trigger cancel action
            });
        } else {
            Log.d(TAG, "Hiding cancel button for Order ID: " + order.getOrderId());
            holder.buttonCancelOrder.setVisibility(View.GONE);
            holder.buttonCancelOrder.setOnClickListener(null); // Clear listener to prevent accidental clicks
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTextView;
        TextView orderDateTextView;
        TextView orderStatusTextView;
        TextView orderTotalTextView;
        TextView paymentStatusTextView;
        TextView orderItemsSummaryTextView;
        LinearLayout linearLayoutOrderItems;
        Button buttonCancelOrder; // Reference to the new cancel button

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.textViewOrderId);
            orderDateTextView = itemView.findViewById(R.id.textViewOrderDate);
            orderStatusTextView = itemView.findViewById(R.id.textViewOrderStatus);
            orderTotalTextView = itemView.findViewById(R.id.textViewOrderTotal);
            paymentStatusTextView = itemView.findViewById(R.id.textViewPaymentStatus);
            orderItemsSummaryTextView = itemView.findViewById(R.id.textViewOrderItemsSummary);
            linearLayoutOrderItems = itemView.findViewById(R.id.linearLayoutOrderItems);
            buttonCancelOrder = itemView.findViewById(R.id.buttonCancelOrder);
        }
    }
}
