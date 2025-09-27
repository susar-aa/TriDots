package com.example.tridots.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tridots.R;
import com.example.tridots.models.LaborerCategory;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LaborerCategoryAdapter extends ArrayAdapter<LaborerCategory> {

    private final Context context;
    private final List<LaborerCategory> laborerCategoryList;

    public LaborerCategoryAdapter(Context context, List<LaborerCategory> laborerCategoryList) {
        super(context, R.layout.item_category, laborerCategoryList);
        this.context = context;
        this.laborerCategoryList = laborerCategoryList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = inflater.inflate(R.layout.item_category, parent, false);
        }

        ImageView categoryIconImageView = listItemView.findViewById(R.id.categoryIconImageView);
        TextView categoryNameTextView = listItemView.findViewById(R.id.categoryNameTextView);
        TextView categoryDescriptionTextView = listItemView.findViewById(R.id.categoryDescriptionTextView);

        LaborerCategory currentCategory = laborerCategoryList.get(position);

        categoryNameTextView.setText(currentCategory.getServiceCategoryName());
        categoryDescriptionTextView.setText(currentCategory.getDescription());

        String imageUrl = "https://lionsgoldencircle.com/Tridots/images/ServiceCategoryIcons/" + currentCategory.getCategoryIcon();
        Picasso.get().load(imageUrl)
                .placeholder(R.drawable.ad_placeholder) // Default placeholder
                .error(R.drawable.image_error_placeholder)   // Error placeholder
                .fit()
                .centerCrop()
                .into(categoryIconImageView);

        return listItemView;
    }
}