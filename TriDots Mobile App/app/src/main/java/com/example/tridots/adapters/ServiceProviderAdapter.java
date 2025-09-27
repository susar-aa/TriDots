package com.example.tridots.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.tridots.R;
import com.example.tridots.detailed_screens.ServiceProviderDetailActivity;
import com.example.tridots.models.ServiceProvider;

import java.util.List;

public class ServiceProviderAdapter extends RecyclerView.Adapter<ServiceProviderAdapter.ServiceProviderViewHolder> {

    private final List<ServiceProvider> serviceProviders;
    private final Context context;

    public ServiceProviderAdapter(List<ServiceProvider> serviceProviders, Context context) {
        this.serviceProviders = serviceProviders;
        this.context = context;
    }

    @NonNull
    @Override
    public ServiceProviderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Use the new reusable layout file
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_featured_ad, parent, false);
        return new ServiceProviderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceProviderViewHolder holder, int position) {
        ServiceProvider provider = serviceProviders.get(position);

        // Set data to the new views
        holder.subtitle.setText(provider.getName());
        holder.title.setText(provider.getServiceCategoryName());

        // Use experience as the "price" field for this card type
        holder.price.setText(provider.getExperienceYears() + "+ Years Exp");
        if (provider.getExperienceYears() == 0) {
            holder.price.setText("New Service");
        }


        // Load image using Glide for consistency
        String imageUrl = provider.getProfilePicture();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ad_placeholder)
                    .error(R.drawable.profile_error_placeholders) // Assuming this is your error placeholder
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.profile_placeholder); // A placeholder for profiles
        }

        // Set OnClickListener to open detail view
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ServiceProviderDetailActivity.class);
            // Pass only the unique ID. The detail activity is responsible for fetching its own data.
            intent.putExtra("service_provider_id", provider.getSellerId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return serviceProviders.size();
    }

    // Updated ViewHolder to match the new item_featured_ad.xml layout
    public static class ServiceProviderViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, subtitle, price;

        public ServiceProviderViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_image);
            title = itemView.findViewById(R.id.item_title);
            subtitle = itemView.findViewById(R.id.item_subtitle);
            price = itemView.findViewById(R.id.item_price);
        }
    }
}
