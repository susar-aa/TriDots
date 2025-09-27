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
import com.example.tridots.ad_lists.VehicleListActivity;
import com.example.tridots.adapters.VehicleCategoryAdapter;
import com.example.tridots.models.VehicleCategory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VehiclesCategoryActivity extends AppCompatActivity {

    private ListView vehicleCategoryListView;
    private List<VehicleCategory> vehicleCategoryList;
    private VehicleCategoryAdapter adapter;
    private EditText searchEditText;
    private List<VehicleCategory> originalVehicleCategoryList;
    private static final String URL_VEHICLE_CATEGORIES = "https://lionsgoldencircle.com/Tridots/Api/get_vehicle_categories.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicles_category);

        vehicleCategoryListView = findViewById(R.id.vehicleCategoryListView);
        searchEditText = findViewById(R.id.searchEditText);
        vehicleCategoryList = new ArrayList<>();
        originalVehicleCategoryList = new ArrayList<>();
        adapter = new VehicleCategoryAdapter(this, vehicleCategoryList);
        vehicleCategoryListView.setAdapter(adapter);

        fetchVehicleCategories();

        vehicleCategoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                VehicleCategory selectedCategory = vehicleCategoryList.get(position);
                Intent intent = new Intent(VehiclesCategoryActivity.this, VehicleListActivity.class);
                intent.putExtra("vehicle_category_id", selectedCategory.getVehicleCategoryId());
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
                filterVehicleCategories(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });
    }

    private void fetchVehicleCategories() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL_VEHICLE_CATEGORIES, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("JSON Response", response.toString()); // Log the entire response
                        try {
                            vehicleCategoryList.clear();
                            originalVehicleCategoryList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject categoryObject = response.getJSONObject(i);
                                int vehicleCategoryId = categoryObject.getInt("vehicle_category_id");
                                String vehicleCategoryName = categoryObject.getString("main_category"); // **Corrected key**
                                String description = categoryObject.getString("category_description"); // **Corrected key**
                                String categoryIcon = categoryObject.getString("category_icon");

                                VehicleCategory category = new VehicleCategory(vehicleCategoryId, vehicleCategoryName, description, categoryIcon);
                                vehicleCategoryList.add(category);
                                originalVehicleCategoryList.add(category);
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(VehiclesCategoryActivity.this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", error.toString());
                Toast.makeText(VehiclesCategoryActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });

        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }

    private void filterVehicleCategories(String query) {
        query = query.toLowerCase(Locale.getDefault());
        vehicleCategoryList.clear();
        if (query.isEmpty()) {
            vehicleCategoryList.addAll(originalVehicleCategoryList);
        } else {
            for (VehicleCategory category : originalVehicleCategoryList) {
                if (category.getVehicleCategoryName().toLowerCase(Locale.getDefault()).contains(query) ||
                        category.getDescription().toLowerCase(Locale.getDefault()).contains(query)) {
                    vehicleCategoryList.add(category);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}