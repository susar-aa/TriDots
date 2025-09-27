package com.example.tridots.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Or Picasso
import com.example.tridots.R;
import com.example.tridots.models.SoldProductOrder;

import java.util.List;

public class ProductsSoldAdapter extends RecyclerView.Adapter<ProductsSoldAdapter.ViewHolder> {

    private List<SoldProductOrder> soldProductOrdersList;
    private Context context;

    public ProductsSoldAdapter(List<SoldProductOrder> soldProductOrdersList, Context context) {
        this.soldProductOrdersList = soldProductOrdersList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sold_product_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SoldProductOrder item = soldProductOrdersList.get(position);

        holder.productName.setText(item.getProductName());
        holder.orderId.setText("Order ID: " + item.getOrderId());
        holder.quantity.setText("Qty: " + item.getQuantity());
        holder.unitPrice.setText(String.format("Unit Price: LKR %.2f", item.getUnitPrice()));
        holder.buyerInfo.setText("Buyer: " + item.getBuyerUsername());
        holder.orderDate.setText("Order Date: " + item.getOrderDate());
        holder.orderStatus.setText("Status: " + item.getOrderStatus());
        holder.paymentStatus.setText("Payment: " + item.getPaymentStatus());

        // Load image using Glide
        if (item.getAdImageUrl() != null && !item.getAdImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(item.getAdImageUrl())
                    .placeholder(R.drawable.placeholder_image) // Add a placeholder image
                    .error(R.drawable.error_placeholdeers) // Add an error image
                    .into(holder.productImage);
        } else {
            holder.productImage.setImageResource(R.drawable.ad_placeholder); // Default no image
        }
    }

    @Override
    public int getItemCount() {
        return soldProductOrdersList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, orderId, quantity, unitPrice, buyerInfo, orderDate, orderStatus, paymentStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            orderId = itemView.findViewById(R.id.orderId);
            quantity = itemView.findViewById(R.id.quantity);
            unitPrice = itemView.findViewById(R.id.unitPrice);
            buyerInfo = itemView.findViewById(R.id.buyerInfo);
            orderDate = itemView.findViewById(R.id.orderDate);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            paymentStatus = itemView.findViewById(R.id.paymentStatus);
        }
    }
}