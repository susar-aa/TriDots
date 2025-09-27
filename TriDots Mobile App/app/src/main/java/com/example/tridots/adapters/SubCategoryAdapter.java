package com.example.tridots.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.tridots.models.SubCategory; // Changed import
import com.example.tridots.R;

import com.squareup.picasso.Picasso; // Make sure to add this dependency to your build.gradle (app level)

import java.util.List;

public class SubCategoryAdapter extends ArrayAdapter<SubCategory> { // Changed ArrayAdapter type

    private Context context;
    private List<SubCategory> subCategories; // Changed List type

    public SubCategoryAdapter(Context context, List<SubCategory> subCategories) { // Changed constructor parameter type
        super(context, 0, subCategories);
        this.context = context;
        this.subCategories = subCategories;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        }

        ImageView categoryIconImageView = convertView.findViewById(R.id.categoryIconImageView);
        TextView categoryNameTextView = convertView.findViewById(R.id.categoryNameTextView);
        TextView categoryDescriptionTextView = convertView.findViewById(R.id.categoryDescriptionTextView);

        SubCategory currentSubCategory = getItem(position); // Changed getItem type

        if (currentSubCategory != null) {
            categoryNameTextView.setText(currentSubCategory.getSubCategory()); // Use SubCategory getter
            categoryDescriptionTextView.setText(currentSubCategory.getCategoryDescription()); // Use SubCategory getter

            // Load image using Picasso library (add dependency in build.gradle)
            if (currentSubCategory.getCategoryIcon() != null && !currentSubCategory.getCategoryIcon().isEmpty()) {
                Picasso.get().load(currentSubCategory.getCategoryIcon()).placeholder(R.drawable.ad_placeholder).error(R.drawable.image_error_placeholder).into(categoryIconImageView);
            } else {
                categoryIconImageView.setImageResource(R.drawable.ad_placeholder); // Default placeholder
            }
        }

        return convertView;
    }

    @Override
    public SubCategory getItem(int position) { // Override getItem to return SubCategory
        return subCategories.get(position);
    }
}