package com.example.tridots.main_screens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tridots.InquiryListFragment;
import com.example.tridots.R;
import com.example.tridots.models.Inquiry;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InquiriesActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private InquiryPagerAdapter pagerAdapter;
    private RequestQueue requestQueue;
    private int userId;
    private static final String GET_INQUIRIES_URL = "https://lionsgoldencircle.com/Tridots/Api/get_inquiries.php";
    private SharedPreferences sharedPreferences;
    private ImageButton backButton; // Declare the back button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiries);

        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "User ID not found. Please log in again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        backButton = findViewById(R.id.backButtonInquiries); // Initialize back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to start DashboardActivity
                Intent intent = new Intent(InquiriesActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish(); // Optional: Close this activity to prevent going back to it with back button.
            }
        });

        viewPager = findViewById(R.id.viewPagerInquiries);
        tabLayout = findViewById(R.id.tabLayoutInquiries);
        requestQueue = Volley.newRequestQueue(this);

        pagerAdapter = new InquiryPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        fetchInquiries();
    }

    public void fetchInquiries() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_INQUIRIES_URL,
                response -> {
                    Log.d("Fetch Inquiries - Raw JSON", "Response: " + response);
                    Log.d("Fetch Inquiries", "Response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.has("error")) {
                            Toast.makeText(InquiriesActivity.this, "Error fetching inquiries: " + jsonResponse.getString("error"), Toast.LENGTH_LONG).show();
                            return;
                        }

                        List<Inquiry> pendingList = parseInquiries(jsonResponse.getJSONArray("Pending"));
                        List<Inquiry> repliedList = parseInquiries(jsonResponse.getJSONArray("Replied"));
                        List<Inquiry> confirmedList = parseInquiries(jsonResponse.getJSONArray("Confirmed"));
                        List<Inquiry> closedList = parseInquiries(jsonResponse.getJSONArray("Closed"));

                        pagerAdapter.updateData(pendingList, repliedList, confirmedList, closedList);

                        tabLayout.getTabAt(0).setText(String.format("Pending (%d)", pendingList.size()));
                        tabLayout.getTabAt(1).setText(String.format("Replied (%d)", repliedList.size()));
                        tabLayout.getTabAt(2).setText(String.format("Confirmed (%d)", confirmedList.size()));
                        tabLayout.getTabAt(3).setText(String.format("Closed (%d)", closedList.size()));

                    } catch (JSONException e) {
                        Log.e("Fetch Inquiries", "JSON Parsing Error: " + e.getMessage());
                        Toast.makeText(InquiriesActivity.this, "Error parsing inquiry data", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Log.e("Fetch Inquiries", "Volley Error: " + error.getMessage());
                    Toast.makeText(InquiriesActivity.this, "Failed to fetch inquiries", Toast.LENGTH_LONG).show();
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private List<Inquiry> parseInquiries(JSONArray jsonArray) throws JSONException {
        List<Inquiry> inquiries = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject inquiryObject = jsonArray.getJSONObject(i);
            Inquiry inquiry = new Inquiry();
            inquiry.setInquiryId(inquiryObject.getInt("inquiry_id"));
            inquiry.setUserId(inquiryObject.getInt("user_id"));
            inquiry.setAdType(inquiryObject.getString("ad_type"));
            inquiry.setAdId(inquiryObject.getInt("ad_id"));
            inquiry.setAdOwnerId(inquiryObject.getInt("ad_owner_id"));
            inquiry.setInquirerName(inquiryObject.getString("inquirer_name"));
            inquiry.setInquirerEmail(inquiryObject.getString("inquirer_email"));
            inquiry.setInquirerPhone(inquiryObject.getString("inquirer_phone"));
            inquiry.setInquiryMessage(inquiryObject.getString("inquiry_message"));
            inquiry.setInquiryDate(inquiryObject.getString("inquiry_date"));
            inquiry.setStatus(inquiryObject.getString("status"));
            inquiry.setSellerReply(inquiryObject.optString("seller_reply"));
            inquiry.setEstimatedCost(inquiryObject.optDouble("estimated_cost"));
            inquiry.setAdName(inquiryObject.optString("ad_name"));
            inquiry.setAdImageUrl(inquiryObject.optString("ad_image_url")); // Still parse it
            inquiries.add(inquiry);
        }
        return inquiries;
    }

    private class InquiryPagerAdapter extends FragmentPagerAdapter {
        private final List<InquiryListFragment> fragments = new ArrayList<>();
        private final String[] tabTitles = {"Pending (0)", "Replied (0)", "Confirmed (0)", "Closed (0)"};

        public InquiryPagerAdapter(@NonNull FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            fragments.add(InquiryListFragment.newInstance()); // Pending
            fragments.add(InquiryListFragment.newInstance()); // Replied
            fragments.add(InquiryListFragment.newInstance()); // Confirmed
            fragments.add(InquiryListFragment.newInstance()); // Closed
        }

        public void updateData(List<Inquiry> pendingList, List<Inquiry> repliedList,
                               List<Inquiry> confirmedList, List<Inquiry> closedList) {
            if (fragments.size() > 0) {
                fragments.get(0).updateData(pendingList);
                fragments.get(1).updateData(repliedList);
                fragments.get(2).updateData(confirmedList);
                fragments.get(3).updateData(closedList);
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }
}

