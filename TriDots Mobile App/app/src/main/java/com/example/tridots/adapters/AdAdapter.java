package com.example.tridots.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tridots.R;
import com.example.tridots.edit_screens.RentingAdEditActivity;
import com.example.tridots.edit_screens.ServiceProviderAdEditActivity;
import com.example.tridots.edit_screens.VehicleAdEditActivity;
import com.example.tridots.models.Ad;

import java.util.List;

public class AdAdapter extends RecyclerView.Adapter<AdAdapter.AdViewHolder> {

    private Context context;
    private List<Ad> adList;
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int adId, String adType);
    }

    public AdAdapter(Context context, List<Ad> adList) {
        this.context = context;
        this.adList = adList;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }

    @NonNull
    @Override
    public AdViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_ad, parent, false);
        return new AdViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdViewHolder holder, int position) {
        Ad currentAd = adList.get(position);
        holder.textViewAdTitle.setText(currentAd.getTitle());
        holder.textViewAdCategory.setText("(" + currentAd.getCategory() + ")");
        Glide.with(context)
                .load(currentAd.getImageUrl())
                .placeholder(R.drawable.ad_placeholder)
                .error(R.drawable.image_error_placeholder)
                .into(holder.imageViewAd);

        // Set OnClickListener for the entire item view to navigate to edit
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToEditScreen(currentAd);
            }
        });

        // REMOVE THIS ENTIRE BLOCK
        // holder.imageViewOptions.setOnClickListener(new View.OnClickListener() {
        //     @Override
        //     public void onClick(View v) {
        //         PopupMenu popupMenu = new PopupMenu(context, v);
        //         popupMenu.getMenuInflater().inflate(R.menu.ad_options_menu, popupMenu.getMenu());
        //         popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
        //             @Override
        //             public boolean onMenuItemClick(MenuItem item) {
        //                 int id = item.getItemId();
        //                 if (id == R.id.action_edit) {
        //                     navigateToEditScreen(currentAd);
        //                     return true;
        //                 } else if (id == R.id.action_delete) {
        //                     if (onDeleteClickListener != null) {
        //                         onDeleteClickListener.onDeleteClick(currentAd.getId(), currentAd.getCategory());
        //                     }
        //                     return true;
        //                 } else if (id == R.id.action_availability) {
        //                     Toast.makeText(context, "Change Availability of " + currentAd.getTitle(), Toast.LENGTH_SHORT).show();
        //                     // Implement your API call to change availability
        //                     return true;
        //                 }
        //                 return false;
        //             }
        //         });
        //         popupMenu.show();
        //     }
        // });
    }

    private void navigateToEditScreen(Ad ad) {
        String category = ad.getCategory();
        int adId = ad.getId();
        Intent intent = null;

        if (category.equals("Renting")) {
            intent = new Intent(context, RentingAdEditActivity.class);
        } else if (category.equals("Vehicle")) {
            intent = new Intent(context, VehicleAdEditActivity.class);
        } else if (category.equals("Service")) {
            intent = new Intent(context, ServiceProviderAdEditActivity.class);
        }

        if (intent != null) {
            intent.putExtra("adId", adId);
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "Cannot edit ads of this category yet.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return adList.size();
    }

    public static class AdViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewAdTitle;
        public TextView textViewAdCategory;
        public ImageView imageViewAd;
        // public ImageView imageViewOptions; // REMOVE THIS LINE

        public AdViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewAdTitle = itemView.findViewById(R.id.textViewAdTitle);
            textViewAdCategory = itemView.findViewById(R.id.textViewAdCategory);
            imageViewAd = itemView.findViewById(R.id.imageViewAd);
            // imageViewOptions = itemView.findViewById(R.id.imageViewOptions); // REMOVE THIS LINE
        }
    }
}