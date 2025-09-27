package com.example.tridots.ad_lists;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.tridots.R;
import com.example.tridots.adapters.VehicleAdapter;
import com.example.tridots.models.VehicleAd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VehicleListActivity extends AppCompatActivity {

    private RecyclerView vehicleListView;
    private List<VehicleAd> vehicleList;
    private VehicleAdapter adapter;
    private EditText searchEditText;
    private List<VehicleAd> originalVehicleList;
    private int vehicleCategoryId;
    private static final String URL_VEHICLES_BY_CATEGORY = "https://lionsgoldencircle.com/Tridots/Api/get_vehicles_by_category.php?vehicle_category_id=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_list);

        vehicleListView = findViewById(R.id.vehicleListView);
        vehicleListView.setLayoutManager(new LinearLayoutManager(this));
        searchEditText = findViewById(R.id.searchEditText);
        vehicleList = new ArrayList<>();
        originalVehicleList = new ArrayList<>();
        adapter = new VehicleAdapter(vehicleList, this);
        vehicleListView.setAdapter(adapter);

        vehicleCategoryId = getIntent().getIntExtra("vehicle_category_id", -1);

        if (vehicleCategoryId != -1) {
            fetchVehiclesByCategory(vehicleCategoryId);
        } else {
            Toast.makeText(this, "Error: Vehicle Category ID not provided", Toast.LENGTH_SHORT).show();
            finish();
        }

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterVehicles(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });
    }

    private void fetchVehiclesByCategory(int vehicleCategoryId) {
        String url = URL_VEHICLES_BY_CATEGORY + vehicleCategoryId;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("JSON Response", response.toString());
                        try {
                            vehicleList.clear();
                            originalVehicleList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject vehicleObject = response.getJSONObject(i);
                                int vehicleId = vehicleObject.getInt("vehicle_id");
                                String vehicleName = vehicleObject.getString("vehicle_name");
                                String brand = vehicleObject.getString("brand");
                                String model = vehicleObject.getString("model");
                                String priceType = vehicleObject.getString("price_type");
                                double amount = vehicleObject.getDouble("amount");
                                String vehicleImages = vehicleObject.getString("vehicle_images");
                                String location = vehicleObject.getString("location");
                                String description = vehicleObject.getString("description");
                                String capacity = vehicleObject.getString("capacity");
                                String fuelType = vehicleObject.getString("fuel_type");
                                String transmissionType = vehicleObject.getString("transmission_type");

                                VehicleAd vehicleAd = new VehicleAd(vehicleId, vehicleName, brand, model, priceType, amount, vehicleImages, location, description, capacity, fuelType, transmissionType);
                                vehicleList.add(vehicleAd);
                                originalVehicleList.add(vehicleAd);
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(VehicleListActivity.this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", error.toString());
                Toast.makeText(VehicleListActivity.this, "Error fetching vehicles", Toast.LENGTH_SHORT).show();
            }
        });

        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }

    private void filterVehicles(String query) {
        query = query.toLowerCase(Locale.getDefault());
        vehicleList.clear();
        if (query.isEmpty()) {
            vehicleList.addAll(originalVehicleList);
        } else {
            for (VehicleAd ad : originalVehicleList) {
                if (ad.getVehicleName().toLowerCase(Locale.getDefault()).contains(query) ||
                        ad.getBrand().toLowerCase(Locale.getDefault()).contains(query) ||
                        ad.getModel().toLowerCase(Locale.getDefault()).contains(query) ||
                        ad.getDescription().toLowerCase(Locale.getDefault()).contains(query) ||
                        ad.getLocation().toLowerCase(Locale.getDefault()).contains(query)) { // Added location here
                    vehicleList.add(ad);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}