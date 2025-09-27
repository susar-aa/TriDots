package com.example.tridots;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tridots.adapters.AdAdapter;
import com.example.tridots.adapters.ViewPagerAdapter;
import com.example.tridots.models.Ad;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ManageAdsActivity extends AppCompatActivity implements AdAdapter.OnDeleteClickListener {

    private Toolbar toolbarManageAds;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ProgressBar progressBarLoading;
    private ViewPagerAdapter viewPagerAdapter;

    private RequestQueue requestQueue;
    private SharedPreferences sharedPreferences;
    private int loggedInUserId;

    private static final String PREF_NAME = "user_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String GET_MY_ADS_URL = "https://lionsgoldencircle.com/Tridots/Api/get_my_ads.php";
    private static final String DELETE_AD_URL = "https://lionsgoldencircle.com/Tridots/Api/delete_ad.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_ads);

        toolbarManageAds = findViewById(R.id.toolbarManageAds);
        setSupportActionBar(toolbarManageAds);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Ads");
        }

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        progressBarLoading = findViewById(R.id.progressBarLoading);

        requestQueue = Volley.newRequestQueue(this);
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        loggedInUserId = sharedPreferences.getInt(KEY_USER_ID, -1);

        if (loggedInUserId != -1) {
            fetchMyAds(loggedInUserId);
        } else {
            Toast.makeText(this, "User ID not found. Please log in again.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.manage_ads_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_add_ad) {
            startActivity(new Intent(ManageAdsActivity.this, ChooseAdTypeActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchMyAds(final int userId) {
        progressBarLoading.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_MY_ADS_URL,
                response -> {
                    progressBarLoading.setVisibility(View.GONE);
                    Log.d("Fetch My Ads Response", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if ("success".equals(jsonObject.getString("status"))) {
                            ArrayList<Ad> allAds = new ArrayList<>();
                            parseAds(jsonObject.getJSONArray("renting_ads"), "Renting", allAds);
                            parseAds(jsonObject.getJSONArray("vehicle_ads"), "Vehicle", allAds);
                            parseAds(jsonObject.getJSONArray("service_ads"), "Service", allAds);
                            setupViewPager(allAds);
                        } else {
                            Toast.makeText(ManageAdsActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ManageAdsActivity.this, "Error parsing JSON", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    progressBarLoading.setVisibility(View.GONE);
                    Toast.makeText(ManageAdsActivity.this, "Error fetching ads: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void parseAds(JSONArray jsonArray, String category, ArrayList<Ad> adList) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject adObject = jsonArray.getJSONObject(i);
            int id = 0;
            String title = "", imageUrl = "", description = "";

            switch (category) {
                case "Renting":
                    id = adObject.getInt("rent_id");
                    title = adObject.getString("product_name");
                    imageUrl = adObject.getString("product_images").split(",")[0].trim();
                    description = adObject.getString("product_description");
                    break;
                case "Vehicle":
                    id = adObject.getInt("vehicle_id");
                    title = adObject.getString("vehicle_name");
                    imageUrl = adObject.getString("vehicle_images").split(",")[0].trim();
                    description = adObject.getString("description");
                    break;
                case "Service":
                    id = adObject.getInt("seller_id");
                    title = adObject.getString("name");
                    imageUrl = adObject.getString("profile_picture");
                    description = adObject.getString("description");
                    break;
            }
            String approvalStatus = adObject.optString("approval_status", "Under Review");
            adList.add(new Ad(id, title, imageUrl, description, category, approvalStatus));
        }
    }

    private void setupViewPager(ArrayList<Ad> allAds) {
        viewPagerAdapter = new ViewPagerAdapter(this);

        ArrayList<Ad> approvedAds = new ArrayList<>();
        ArrayList<Ad> underReviewAds = new ArrayList<>();
        ArrayList<Ad> notApprovedAds = new ArrayList<>();
        ArrayList<Ad> blockedAds = new ArrayList<>();

        for (Ad ad : allAds) {
            switch (ad.getApprovalStatus()) {
                case "Approved":
                    approvedAds.add(ad);
                    break;
                case "Not Approved":
                    notApprovedAds.add(ad);
                    break;
                case "Blocked":
                    blockedAds.add(ad);
                    break;
                case "Under Review":
                default:
                    underReviewAds.add(ad);
                    break;
            }
        }

        viewPagerAdapter.addFragment("Under Review (" + underReviewAds.size() + ")", underReviewAds);
        viewPagerAdapter.addFragment("Approved (" + approvedAds.size() + ")", approvedAds);
        viewPagerAdapter.addFragment("Not Approved (" + notApprovedAds.size() + ")", notApprovedAds);
        viewPagerAdapter.addFragment("Blocked (" + blockedAds.size() + ")", blockedAds);

        viewPager.setAdapter(viewPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(viewPagerAdapter.getPageTitle(position))
        ).attach();
    }

    @Override
    public void onDeleteClick(int adId, String adType) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this " + adType + " ad?")
                .setPositiveButton("Delete", (dialog, which) -> deleteAd(adId, adType))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void deleteAd(final int adIdToDelete, final String adType) {
        progressBarLoading.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DELETE_AD_URL,
                response -> {
                    progressBarLoading.setVisibility(View.GONE);
                    Log.d("Delete Ad Response", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        Toast.makeText(ManageAdsActivity.this, message, Toast.LENGTH_SHORT).show();
                        if ("success".equals(status)) {
                            // Refresh the ad list after successful deletion
                            fetchMyAds(loggedInUserId);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ManageAdsActivity.this, "Error parsing delete response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBarLoading.setVisibility(View.GONE);
                    Toast.makeText(ManageAdsActivity.this, "Error deleting ad: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String idKey = "";
                if (adType.equals("Renting")) {
                    idKey = "rent_id";
                } else if (adType.equals("Vehicle")) {
                    idKey = "vehicle_id";
                } else if (adType.equals("Service")) {
                    idKey = "seller_id";
                }
                params.put(idKey, String.valueOf(adIdToDelete));
                params.put("ad_type", adType);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}
