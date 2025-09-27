package com.example.tridots.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tridots.R;
import com.example.tridots.models.Category;
import com.squareup.picasso.Picasso; // Make sure to add this dependency to your build.gradle (app level)

import java.util.List;

public class CategoryAdapter extends ArrayAdapter<Category> {

    private Context context;
    private List<Category> categories;

    public CategoryAdapter(Context context, List<Category> categories) {
        super(context, 0, categories);
        this.context = context;
        this.categories = categories;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        }

        ImageView categoryIconImageView = convertView.findViewById(R.id.categoryIconImageView);
        TextView categoryNameTextView = convertView.findViewById(R.id.categoryNameTextView);
        TextView categoryDescriptionTextView = convertView.findViewById(R.id.categoryDescriptionTextView);

        Category currentCategory = getItem(position);

        if (currentCategory != null) {
            categoryNameTextView.setText(currentCategory.getMainCategory());
            categoryDescriptionTextView.setText(currentCategory.getCategoryDescription());

            // Load image using Picasso library (add dependency in build.gradle)
            if (currentCategory.getCategoryIcon() != null && !currentCategory.getCategoryIcon().isEmpty()) {
                Picasso.get().load(currentCategory.getCategoryIcon()).placeholder(R.drawable.ad_placeholder).error(R.drawable.image_error_placeholder).into(categoryIconImageView);
            } else {
                categoryIconImageView.setImageResource(R.drawable.ad_placeholder); // Default placeholder
            }
        }

        return convertView;
    }
}