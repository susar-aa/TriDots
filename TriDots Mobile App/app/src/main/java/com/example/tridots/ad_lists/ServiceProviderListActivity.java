package com.example.tridots.ad_lists;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tridots.R;
import com.example.tridots.adapters.ServiceProviderAdapter;
import com.example.tridots.models.ServiceProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ServiceProviderListActivity extends AppCompatActivity {

    private RecyclerView serviceProviderRecyclerView;
    private ServiceProviderAdapter adapter;
    private List<ServiceProvider> serviceProviderList;
    private EditText searchEditText;
    private List<ServiceProvider> originalServiceProviderList;
    private int serviceCategoryId;
    private static final String URL_SERVICE_PROVIDERS = "https://lionsgoldencircle.com/Tridots/Api/get_service_providers_by_category.php?service_category_id=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_list);

        serviceProviderRecyclerView = findViewById(R.id.serviceProviderRecyclerView);
        serviceProviderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchEditText = findViewById(R.id.searchEditText); // Initialize the search EditText
        serviceProviderList = new ArrayList<>();
        originalServiceProviderList = new ArrayList<>();
        adapter = new ServiceProviderAdapter(serviceProviderList, this);
        serviceProviderRecyclerView.setAdapter(adapter);

        // Get the service category ID passed from the previous activity
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("service_category_id")) {
            serviceCategoryId = intent.getIntExtra("service_category_id", -1);
            if (serviceCategoryId != -1) {
                fetchServiceProviders(serviceCategoryId);
            } else {
                Toast.makeText(this, "Error: Service category ID not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Error: Intent data missing", Toast.LENGTH_SHORT).show();
        }

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterServiceProviders(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });
    }

    private void fetchServiceProviders(int categoryId) {
        String url = URL_SERVICE_PROVIDERS + categoryId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("JSON Response", response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            serviceProviderList.clear();
                            originalServiceProviderList.clear(); // Initialize the original list here
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject providerObject = jsonArray.getJSONObject(i);
                                int sellerId = providerObject.getInt("seller_id");
                                String name = providerObject.getString("name");
                                String serviceCategoryName = providerObject.getString("service_category_name");
                                String description = providerObject.getString("description");
                                String contactNumber = providerObject.getString("contact_number");
                                String emailAddress = providerObject.getString("email_address");
                                String address = providerObject.getString("address");
                                int experienceYears = providerObject.getInt("experience_years");
                                String qualifications = providerObject.getString("qualifications");
                                String location = providerObject.getString("location");
                                String availabilityStatus = providerObject.getString("availability_status");
                                float reviewsAverage = (float) providerObject.optDouble("reviews_average", 0.0);
                                int reviewCount = providerObject.optInt("review_count", 0);
                                String verificationStatus = providerObject.getString("verification_status");
                                String profilePicture = providerObject.getString("profile_picture");
                                int userId = providerObject.getInt("user_id"); // Get user_id
                                String listerName = providerObject.getString("lister_name"); // Get lister_name

                                ServiceProvider serviceProvider = new ServiceProvider(
                                        sellerId, name, serviceCategoryName, description, contactNumber,
                                        emailAddress, address, experienceYears, qualifications, location,
                                        availabilityStatus, reviewsAverage, reviewCount, verificationStatus,
                                        profilePicture
                                );
                                serviceProvider.setUserId(userId); // Set user_id in the model
                                serviceProvider.setListerName(listerName); // Set lister_name in the model
                                serviceProviderList.add(serviceProvider);
                                originalServiceProviderList.add(serviceProvider); // Populate the original list
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ServiceProviderListActivity.this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                        Toast.makeText(ServiceProviderListActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                });

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void filterServiceProviders(String query) {
        query = query.toLowerCase(Locale.getDefault());
        serviceProviderList.clear();
        if (query.isEmpty()) {
            serviceProviderList.addAll(originalServiceProviderList);
        } else {
            for (ServiceProvider provider : originalServiceProviderList) {
                if (provider.getName().toLowerCase(Locale.getDefault()).contains(query) ||
                        provider.getServiceCategoryName().toLowerCase(Locale.getDefault()).contains(query) ||
                        provider.getDescription().toLowerCase(Locale.getDefault()).contains(query) ||
                        provider.getLocation().toLowerCase(Locale.getDefault()).contains(query)) {
                    serviceProviderList.add(provider);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}