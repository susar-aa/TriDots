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
import com.example.tridots.adapters.LaborerCategoryAdapter;
import com.example.tridots.models.LaborerCategory;
import com.example.tridots.ad_lists.ServiceProviderListActivity; // Assuming you'll create this

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LaborersCategoryActivity extends AppCompatActivity {

    private ListView laborerCategoryListView;
    private List<LaborerCategory> laborerCategoryList;
    private LaborerCategoryAdapter adapter;
    private EditText searchEditText;
    private List<LaborerCategory> originalLaborerCategoryList;
    private static final String URL_LABORER_CATEGORIES = "https://lionsgoldencircle.com/Tridots/Api/get_service_categories.php"; // Adjust URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laborers_category);

        laborerCategoryListView = findViewById(R.id.laborerCategoryListView);
        searchEditText = findViewById(R.id.searchEditText);
        laborerCategoryList = new ArrayList<>();
        originalLaborerCategoryList = new ArrayList<>();
        adapter = new LaborerCategoryAdapter(this, laborerCategoryList);
        laborerCategoryListView.setAdapter(adapter);

        fetchLaborerCategories();

        laborerCategoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LaborerCategory selectedCategory = laborerCategoryList.get(position);
                Intent intent = new Intent(LaborersCategoryActivity.this, ServiceProviderListActivity.class); // Navigate to the list of service providers
                intent.putExtra("service_category_id", selectedCategory.getServiceCategoryId());
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
                filterLaborerCategories(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });
    }

    private void fetchLaborerCategories() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL_LABORER_CATEGORIES, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("JSON Response", response.toString()); // Log the entire response
                        try {
                            laborerCategoryList.clear();
                            originalLaborerCategoryList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject categoryObject = response.getJSONObject(i);
                                int serviceCategoryId = categoryObject.getInt("service_category_id");
                                String serviceCategoryName = categoryObject.getString("service_category_name");
                                String description = categoryObject.getString("description");
                                String categoryIcon = categoryObject.getString("category_icon");

                                LaborerCategory category = new LaborerCategory(serviceCategoryId, serviceCategoryName, description, categoryIcon);
                                laborerCategoryList.add(category);
                                originalLaborerCategoryList.add(category);
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LaborersCategoryActivity.this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", error.toString());
                Toast.makeText(LaborersCategoryActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });

        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }

    private void filterLaborerCategories(String query) {
        query = query.toLowerCase(Locale.getDefault());
        laborerCategoryList.clear();
        if (query.isEmpty()) {
            laborerCategoryList.addAll(originalLaborerCategoryList);
        } else {
            for (LaborerCategory category : originalLaborerCategoryList) {
                if (category.getServiceCategoryName().toLowerCase(Locale.getDefault()).contains(query) ||
                        category.getDescription().toLowerCase(Locale.getDefault()).contains(query)) {
                    laborerCategoryList.add(category);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}