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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.tridots.R;
import com.example.tridots.ad_lists.RentingActivity;
import com.example.tridots.adapters.SubCategoryAdapter;
import com.example.tridots.models.SubCategory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SubCategoryActivity extends AppCompatActivity {

    private TextView headerTextView;
    private ListView subCategoryListView;
    private List<SubCategory> subCategoryList;
    private SubCategoryAdapter adapter;
    private EditText searchEditText;
    private List<SubCategory> originalSubCategoryList; // To hold the original full list
    private int mainCategoryId;
    private static final String URL_SUB_CATEGORIES = "https://lionsgoldencircle.com/Tridots/Api/get_sub_categories.php?main_category_id=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);

        headerTextView = findViewById(R.id.headerTextView);
        subCategoryListView = findViewById(R.id.subCategoryListView);
        searchEditText = findViewById(R.id.searchEditText);
        subCategoryList = new ArrayList<>();
        originalSubCategoryList = new ArrayList<>(); // Initialize the original list
        adapter = new SubCategoryAdapter(this, subCategoryList);
        subCategoryListView.setAdapter(adapter);

        mainCategoryId = getIntent().getIntExtra("main_category_id", -1);

        if (mainCategoryId != -1) {
            fetchSubCategories(mainCategoryId);
            // Optionally set header text based on main category
            // headerTextView.setText("Subcategories for Category ID: " + mainCategoryId);
        } else {
            Toast.makeText(this, "Error: Main Category ID not provided", Toast.LENGTH_SHORT).show();
            finish();
        }

        subCategoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SubCategory selectedSubCategory = subCategoryList.get(position);
                Intent intent = new Intent(SubCategoryActivity.this, RentingActivity.class);
                intent.putExtra("sub_category_id", selectedSubCategory.getSubCategoryId());
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
                filterSubCategories(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });
    }

    private void fetchSubCategories(int mainCategoryId) {
        String url = URL_SUB_CATEGORIES + mainCategoryId;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            subCategoryList.clear();
                            originalSubCategoryList.clear(); // Clear the original list as well
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject subCategoryObject = response.getJSONObject(i);
                                int subCategoryId = subCategoryObject.getInt("sub_category_id");
                                String subCategory = subCategoryObject.getString("sub_category");
                                String categoryDescription = subCategoryObject.getString("category_description");
                                String categoryIcon = subCategoryObject.getString("category_icon");
                                int mainCategoryId = subCategoryObject.getInt("main_category_id");

                                SubCategory sub = new SubCategory(subCategoryId, subCategory, categoryDescription, categoryIcon, mainCategoryId);
                                subCategoryList.add(sub);
                                originalSubCategoryList.add(sub); // Add to the original list
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SubCategoryActivity.this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", error.toString());
                Toast.makeText(SubCategoryActivity.this, "Error fetching subcategories", Toast.LENGTH_SHORT).show();
            }
        });

        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }

    private void filterSubCategories(String query) {
        query = query.toLowerCase(Locale.getDefault());
        subCategoryList.clear();
        if (query.isEmpty()) {
            subCategoryList.addAll(originalSubCategoryList);
        } else {
            for (SubCategory subCategory : originalSubCategoryList) {
                if (subCategory.getSubCategory().toLowerCase(Locale.getDefault()).contains(query) ||
                        subCategory.getCategoryDescription().toLowerCase(Locale.getDefault()).contains(query)) {
                    subCategoryList.add(subCategory);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}