package com.example.tridots.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tridots.R;
import com.example.tridots.models.SoldOrderHeader;
import com.example.tridots.models.SoldOrderItem;

import java.util.List;

public class SoldOrdersAdapter extends RecyclerView.Adapter<SoldOrdersAdapter.SoldOrderHeaderViewHolder> {

    private List<SoldOrderHeader> soldOrderHeaders;
    private Context context;
    private OnItemStatusChangeListener statusChangeListener;

    public interface OnItemStatusChangeListener {
        void onStatusChangeRequested(int orderItemId, String newStatus);
    }

    public SoldOrdersAdapter(List<SoldOrderHeader> soldOrderHeaders, Context context) {
        this.soldOrderHeaders = soldOrderHeaders;
        this.context = context;
        // The listener is typically set by the hosting fragment/activity
    }

    public void setOnItemStatusChangeListener(OnItemStatusChangeListener listener) {
        this.statusChangeListener = listener;
    }


    @NonNull
    @Override
    public SoldOrderHeaderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sold_order_header, parent, false);
        return new SoldOrderHeaderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SoldOrderHeaderViewHolder holder, int position) {
        SoldOrderHeader header = soldOrderHeaders.get(position);

        holder.orderIdHeader.setText("Order #" + header.getOrderId());
        holder.buyerInfoHeader.setText("Buyer: " + header.getBuyerUsername());
        holder.orderDateHeader.setText("Order Date: " + header.getOrderDate());
        holder.orderStatusHeader.setText("Status: " + header.getOrderStatus());
        holder.paymentStatusHeader.setText("Payment: " + header.getPaymentStatus());
        holder.totalAmountHeader.setText(String.format("Total Amount: LKR %.2f", header.getTotalAmount()));

        // Manage expand/collapse state
        if (header.isExpanded()) {
            holder.itemsLayout.setVisibility(View.VISIBLE);
            holder.expandCollapseIcon.setImageResource(R.drawable.ic_arrow_up);
            populateItemsLayout(holder.itemsLayout, header.getItems());
        } else {
            holder.itemsLayout.setVisibility(View.GONE);
            holder.expandCollapseIcon.setImageResource(R.drawable.ic_arrow_down);
        }

        holder.itemView.setOnClickListener(v -> {
            header.setExpanded(!header.isExpanded());
            notifyItemChanged(position); // Notify adapter to rebind this item
        });
    }

    @Override
    public int getItemCount() {
        return soldOrderHeaders.size();
    }

    // This method dynamically adds item views to the LinearLayout
    private void populateItemsLayout(LinearLayout parentLayout, List<SoldOrderItem> items) {
        parentLayout.removeAllViews(); // Clear previous views
        LayoutInflater inflater = LayoutInflater.from(context);

        if (items.isEmpty()) {
            TextView noItemsText = new TextView(context);
            noItemsText.setText("No items found for this order.");
            noItemsText.setPadding(0, 16, 0, 0);
            parentLayout.addView(noItemsText);
            return;
        }

        for (SoldOrderItem item : items) {
            View itemView = inflater.inflate(R.layout.item_sold_order_item, parentLayout, false);

            ImageView productImage = itemView.findViewById(R.id.productImage);
            TextView productName = itemView.findViewById(R.id.productName);
            TextView variantName = itemView.findViewById(R.id.variantName);
            TextView quantity = itemView.findViewById(R.id.quantity);
            TextView unitPrice = itemView.findViewById(R.id.unitPrice);
            TextView itemDeliveryStatus = itemView.findViewById(R.id.itemDeliveryStatus);

            LinearLayout pendingActionsLayout = itemView.findViewById(R.id.pendingActionsLayout);
            Button btnReadyToPickUp = itemView.findViewById(R.id.btnReadyToPickUp);
            Button btnOutofStock = itemView.findViewById(R.id.btnOutofStock);
            Button btnCancelItem = itemView.findViewById(R.id.btnCancelItem);


            productName.setText(item.getProductName());

            if (item.getVariantName() != null && !item.getVariantName().isEmpty()) {
                variantName.setText("Variant: " + item.getVariantName());
                variantName.setVisibility(View.VISIBLE);
            } else {
                variantName.setVisibility(View.GONE);
            }

            quantity.setText("Qty: " + item.getQuantity());
            unitPrice.setText(String.format("Unit Price: LKR %.2f", item.getUnitPrice()));
            itemDeliveryStatus.setText("Item Status: " + item.getDeliveryStatus());

            // --- IMPORTANT CHANGE HERE: Control visibility based on ITEM'S deliveryStatus ---
            if ("Pending".equalsIgnoreCase(item.getDeliveryStatus())) {
                pendingActionsLayout.setVisibility(View.VISIBLE);

                // Set click listeners for the buttons
                // The listener will tell the Activity/Fragment which item and new status to set
                btnReadyToPickUp.setOnClickListener(v -> {
                    if (statusChangeListener != null) {
                        statusChangeListener.onStatusChangeRequested(item.getOrderItemId(), "Ready to Pick Up");
                    }
                });

                btnOutofStock.setOnClickListener(v -> {
                    if (statusChangeListener != null) {
                        statusChangeListener.onStatusChangeRequested(item.getOrderItemId(), "Out of Stock");
                    }
                });

                btnCancelItem.setOnClickListener(v -> {
                    if (statusChangeListener != null) {
                        statusChangeListener.onStatusChangeRequested(item.getOrderItemId(), "Cancelled");
                    }
                });

            } else {
                // If not "Pending", hide the action buttons
                pendingActionsLayout.setVisibility(View.GONE);
            }

            parentLayout.addView(itemView);
        }
    }


    public static class SoldOrderHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdHeader, buyerInfoHeader, orderDateHeader, orderStatusHeader, paymentStatusHeader, totalAmountHeader;
        LinearLayout itemsLayout;
        ImageView expandCollapseIcon;

        public SoldOrderHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdHeader = itemView.findViewById(R.id.orderIdHeader);
            buyerInfoHeader = itemView.findViewById(R.id.buyerInfoHeader);
            orderDateHeader = itemView.findViewById(R.id.orderDateHeader);
            orderStatusHeader = itemView.findViewById(R.id.orderStatusHeader);
            paymentStatusHeader = itemView.findViewById(R.id.paymentStatusHeader);
            totalAmountHeader = itemView.findViewById(R.id.totalAmountHeader);
            itemsLayout = itemView.findViewById(R.id.itemsLayout);
            expandCollapseIcon = itemView.findViewById(R.id.expandCollapseIcon);
        }
    }
}