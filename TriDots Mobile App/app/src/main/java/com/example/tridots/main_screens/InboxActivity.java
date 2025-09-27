package com.example.tridots.main_screens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tridots.ChatActivity;
import com.example.tridots.R;
import com.example.tridots.adapters.ChatListAdapter;
import com.example.tridots.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InboxActivity extends AppCompatActivity implements View.OnClickListener {

    // --- UI Components ---
    private RecyclerView recyclerViewChatList;
    private TextView textViewNoChats;

    // --- Custom Bottom Navigation Components ---
    private LinearLayout navHome, navDashboard, navMarketplace, navInbox, navProfile;
    private View indicatorHome, indicatorDashboard, indicatorInbox, indicatorProfile;
    private ImageView iconHome, iconDashboard, iconInbox, iconProfile;
    private TextView labelHome, labelDashboard, labelInbox, labelProfile;

    // --- Data & Networking ---
    private ChatListAdapter chatListAdapter;
    private List<User> chatUsers;
    private SharedPreferences sharedPreferences;
    private int currentUserId;
    private DatabaseReference mUserChatsRef;
    private RequestQueue requestQueue;

    private static final String PREF_NAME = "user_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String TAG = "InboxActivity";
    private static final String GET_USERS_API_URL = "https://lionsgoldencircle.com/Tridots/Api/get_users_by_ids.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        requestQueue = Volley.newRequestQueue(this);
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt(KEY_USER_ID, -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_LONG).show();
            // Optional: Redirect to login
            finish();
            return;
        }

        initializeViews();
        setupRecyclerView();
        setupListeners();

        mUserChatsRef = FirebaseDatabase.getInstance().getReference("user_chats").child(String.valueOf(currentUserId));
        loadChatUsers();
    }

    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerViewChatList = findViewById(R.id.recyclerViewChatList);
        textViewNoChats = findViewById(R.id.textViewNoChats);

        // --- Initialize Custom Navigation Bar Views ---
        navHome = findViewById(R.id.navHome);
        navDashboard = findViewById(R.id.navDashboard);
        navMarketplace = findViewById(R.id.navMarketplace);
        navInbox = findViewById(R.id.navInbox);
        navProfile = findViewById(R.id.navProfile);

        indicatorHome = findViewById(R.id.indicatorHome);
        indicatorDashboard = findViewById(R.id.indicatorDashboard);
        indicatorInbox = findViewById(R.id.indicatorInbox);
        indicatorProfile = findViewById(R.id.indicatorProfile);

        iconHome = findViewById(R.id.iconHome);
        iconDashboard = findViewById(R.id.iconDashboard);
        iconInbox = findViewById(R.id.iconInbox);
        iconProfile = findViewById(R.id.iconProfile);

        labelHome = findViewById(R.id.labelHome);
        labelDashboard = findViewById(R.id.labelDashboard);
        labelInbox = findViewById(R.id.labelInbox);
        labelProfile = findViewById(R.id.labelProfile);

        // Set Inbox as the default selected tab
        selectTab(3); // 0=Home, 1=Dashboard, 2=Marketplace, 3=Inbox
    }

    private void setupRecyclerView() {
        recyclerViewChatList.setLayoutManager(new LinearLayoutManager(this));
        chatUsers = new ArrayList<>();
        chatListAdapter = new ChatListAdapter(this, chatUsers, user -> {
            Intent intent = new Intent(InboxActivity.this, ChatActivity.class);
            intent.putExtra("receiver_id", user.getUserId());
            intent.putExtra("receiver_username", user.getUsername());
            startActivity(intent);
        });
        recyclerViewChatList.setAdapter(chatListAdapter);
    }

    private void setupListeners() {
        navHome.setOnClickListener(this);
        navDashboard.setOnClickListener(this);
        navMarketplace.setOnClickListener(this);
        navInbox.setOnClickListener(this);
        navProfile.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        int id = v.getId();
        int tabIndex = -1;

        // Handle Navigation Clicks
        if (id == R.id.navHome) {
            tabIndex = 0;
            intent = new Intent(this, HomeActivity.class);
        } else if (id == R.id.navDashboard) {
            tabIndex = 1;
            intent = new Intent(this, DashboardActivity.class);
        } else if (id == R.id.navMarketplace) {
            tabIndex = 2;
            intent = new Intent(this, MarketplaceActivity.class);
        } else if (id == R.id.navInbox) {
            tabIndex = 3;
            // Already here, do nothing
        } else if (id == R.id.navProfile) {
            tabIndex = 4;
            intent = new Intent(this, ProfileActivity.class);
        }

        if (intent != null) {
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish(); // Finish current activity to prevent stacking
        }

        if (tabIndex != -1) {
            animateTabPress(v);
            selectTab(tabIndex);
        }
    }

    // --- Navigation Bar Animation and Selection Logic ---
    private void selectTab(int tabIndex) {
        resetAllTabs();
        switch (tabIndex) {
            case 0: setTabSelected(iconHome, labelHome, indicatorHome); break;
            case 1: setTabSelected(iconDashboard, labelDashboard, indicatorDashboard); break;
            case 2: break; // Marketplace has no selection state change
            case 3: setTabSelected(iconInbox, labelInbox, indicatorInbox); break;
            case 4: setTabSelected(iconProfile, labelProfile, indicatorProfile); break;
        }
    }

    private void resetAllTabs() {
        int unselectedColor = ContextCompat.getColor(this, R.color.text_secondary);
        iconHome.setColorFilter(unselectedColor);
        labelHome.setTextColor(unselectedColor);
        indicatorHome.setVisibility(View.INVISIBLE);

        iconDashboard.setColorFilter(unselectedColor);
        labelDashboard.setTextColor(unselectedColor);
        indicatorDashboard.setVisibility(View.INVISIBLE);

        iconInbox.setColorFilter(unselectedColor);
        labelInbox.setTextColor(unselectedColor);
        indicatorInbox.setVisibility(View.INVISIBLE);

        iconProfile.setColorFilter(unselectedColor);
        labelProfile.setTextColor(unselectedColor);
        indicatorProfile.setVisibility(View.INVISIBLE);
    }

    private void setTabSelected(ImageView icon, TextView label, View indicator) {
        int selectedColor = ContextCompat.getColor(this, R.color.primary_green);
        icon.setColorFilter(selectedColor);
        label.setTextColor(selectedColor);

        indicator.setVisibility(View.VISIBLE);
        indicator.setAlpha(0f);
        indicator.animate().alpha(1f).setDuration(250).start();

        animateIconBounce(icon);
    }

    private void animateTabPress(View tab) {
        tab.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100)
                .withEndAction(() -> tab.animate().scaleX(1f).scaleY(1f).setDuration(100).start())
                .start();
    }

    private void animateIconBounce(ImageView icon) {
        icon.animate().scaleX(1.2f).scaleY(1.2f).setDuration(150)
                .withEndAction(() -> icon.animate().scaleX(1f).scaleY(1f).setDuration(150).setInterpolator(new BounceInterpolator()).start())
                .start();
    }

    // --- Existing Data Fetching Logic ---

    private void loadChatUsers() {
        mUserChatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Integer> chattedUserIds = new ArrayList<>();
                if (!snapshot.exists()) {
                    updateNoChatsVisibility(true);
                    return;
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    try {
                        chattedUserIds.add(Integer.parseInt(dataSnapshot.getKey()));
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Invalid user ID format in Firebase: " + dataSnapshot.getKey());
                    }
                }
                if (chattedUserIds.isEmpty()) {
                    updateNoChatsVisibility(true);
                } else {
                    fetchUserDetailsFromApi(chattedUserIds);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(InboxActivity.this, "Failed to load inbox.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUserDetailsFromApi(List<Integer> userIdsToFetch) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("user_ids", new JSONArray(userIdsToFetch));
        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON for user IDs", e);
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, GET_USERS_API_URL, postData,
                response -> {
                    try {
                        if ("success".equals(response.getString("status"))) {
                            JSONArray usersArray = response.getJSONArray("users");
                            chatUsers.clear();
                            for (int i = 0; i < usersArray.length(); i++) {
                                JSONObject userObj = usersArray.getJSONObject(i);
                                chatUsers.add(new User(
                                        userObj.getInt("user_id"),
                                        userObj.getString("username"),
                                        userObj.optString("profile_picture", "")
                                ));
                            }
                            updateNoChatsVisibility(chatUsers.isEmpty());
                            chatListAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, "API Error: " + response.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "API JSON parsing error", e);
                    }
                },
                error -> {
                    Log.e(TAG, "Volley error fetching user details", error);
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    private void updateNoChatsVisibility(boolean showNoChats) {
        textViewNoChats.setVisibility(showNoChats ? View.VISIBLE : View.GONE);
        recyclerViewChatList.setVisibility(showNoChats ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectTab(3); // Ensure Inbox is selected
    }
}
