package com.example.tridots.category_lists;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.tridots.R;
import com.example.tridots.adapters.CategoryAdapter;
import com.example.tridots.models.Category; // Use Category model

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MachinesCategoryActivity extends AppCompatActivity {

    private ListView categoryListView;
    private List<Category> categoryList; // Use Category model
    private CategoryAdapter adapter;
    private EditText searchEditText;
    private List<Category> originalCategoryList;
    private static final String URL_CATEGORIES = "https://lionsgoldencircle.com/Tridots/Api/get_main_categories.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machines_category);

        categoryListView = findViewById(R.id.categoryListView);
        searchEditText = findViewById(R.id.searchEditText);
        categoryList = new ArrayList<>();
        originalCategoryList = new ArrayList<>();
        adapter = new CategoryAdapter(this, categoryList);
        categoryListView.setAdapter(adapter);

        fetchMainCategories();

        categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Category selectedCategory = categoryList.get(position);
                Intent intent = new Intent(MachinesCategoryActivity.this, SubCategoryActivity.class);
                intent.putExtra("main_category_id", selectedCategory.getMainCategoryId());
                startActivity(intent);
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCategories(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });
    }

    private void fetchMainCategories() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL_CATEGORIES, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            categoryList.clear();
                            originalCategoryList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject categoryObject = response.getJSONObject(i);
                                int mainCategoryId = categoryObject.getInt("main_category_id");
                                String mainCategory = categoryObject.getString("main_category");
                                String categoryDescription = categoryObject.getString("category_description");
                                String categoryIcon = categoryObject.getString("category_icon");

                                Category category = new Category(mainCategoryId, mainCategory, categoryDescription, categoryIcon);
                                categoryList.add(category);
                                originalCategoryList.add(category);
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MachinesCategoryActivity.this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", error.toString());
                Toast.makeText(MachinesCategoryActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });

        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }

    private void filterCategories(String query) {
        query = query.toLowerCase(Locale.getDefault());
        categoryList.clear();
        if (query.isEmpty()) {
            categoryList.addAll(originalCategoryList);
        } else {
            for (Category category : originalCategoryList) {
                if (category.getMainCategory().toLowerCase(Locale.getDefault()).contains(query) ||
                        category.getCategoryDescription().toLowerCase(Locale.getDefault()).contains(query)) {
                    categoryList.add(category);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}