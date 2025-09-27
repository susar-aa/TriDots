package com.example.tridots.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tridots.R;
import com.example.tridots.models.VehicleCategory;
import com.squareup.picasso.Picasso;

import java.util.List;

public class VehicleCategoryAdapter extends ArrayAdapter<VehicleCategory> {

    private Context context;
    private List<VehicleCategory> vehicleCategories;

    public VehicleCategoryAdapter(Context context, List<VehicleCategory> vehicleCategories) {
        super(context, 0, vehicleCategories);
        this.context = context;
        this.vehicleCategories = vehicleCategories;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false); // Assuming you have item_category.xml
        }

        ImageView categoryIconImageView = convertView.findViewById(R.id.categoryIconImageView);
        TextView categoryNameTextView = convertView.findViewById(R.id.categoryNameTextView);
        TextView categoryDescriptionTextView = convertView.findViewById(R.id.categoryDescriptionTextView);

        VehicleCategory currentVehicleCategory = getItem(position);

        if (currentVehicleCategory != null) {
            categoryNameTextView.setText(currentVehicleCategory.getVehicleCategoryName());
            categoryDescriptionTextView.setText(currentVehicleCategory.getDescription());

            if (currentVehicleCategory.getCategoryIcon() != null && !currentVehicleCategory.getCategoryIcon().isEmpty()) {
                Picasso.get().load(currentVehicleCategory.getCategoryIcon()).placeholder(R.drawable.ad_placeholder).error(R.drawable.image_error_placeholder).into(categoryIconImageView);
            } else {
                categoryIconImageView.setImageResource(R.drawable.ad_placeholder);
            }
        }

        return convertView;
    }
}