package com.example.tridots.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.tridots.R;
import com.example.tridots.models.Product;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    public ProductAdapter(Context context, List<Product> productList, OnItemClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the new redesigned layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product, listener);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateProducts(List<Product> newProducts) {
        this.productList.clear();
        this.productList.addAll(newProducts);
        notifyDataSetChanged();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProduct;
        TextView textViewProductName;
        TextView textViewProductPrice;
        TextView textViewProductCity;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
            textViewProductName = itemView.findViewById(R.id.textViewProductName);
            textViewProductPrice = itemView.findViewById(R.id.textViewProductPrice);
            textViewProductCity = itemView.findViewById(R.id.textViewProductCity);
        }

        public void bind(final Product product, final OnItemClickListener listener) {
            textViewProductName.setText(product.getProductName());

            // Format price with currency symbol and commas
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("en", "LK"));
            format.setCurrency(java.util.Currency.getInstance("LKR"));
            textViewProductPrice.setText(format.format(product.getPrice()));

            textViewProductCity.setText(String.format("%s, %s", product.getStoreCity(), product.getStoreDistrict()));

            // Load image using Glide
            Glide.with(context)
                    .load(product.getThumbnailImageUrl())
                    .placeholder(R.drawable.ad_placeholder)
                    .error(R.drawable.image_error_placeholder)
                    .into(imageViewProduct);

            itemView.setOnClickListener(v -> listener.onItemClick(product));
        }
    }
}
