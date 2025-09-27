// Path: app/src/main/java/com/example/tridots/AddressSelectionActivity.java
package com.example.tridots.Marketplace;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tridots.R;
import com.example.tridots.adapters.AddressAdapter;
import com.example.tridots.models.Address;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal; // Import BigDecimal
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddressSelectionActivity extends AppCompatActivity implements AddressAdapter.OnAddressActionListener {

    private static final String TAG = "AddressSelectionActivity";
    private static final String API_BASE_URL = "https://lionsgoldencircle.com/Tridots/Api/";
    private static final String GET_ADDRESSES_URL = API_BASE_URL + "get_user_addresses.php";
    private static final String DELETE_ADDRESS_URL = API_BASE_URL + "delete_address.php";
    private static final String SET_DEFAULT_ADDRESS_URL = API_BASE_URL + "set_default_address.php";

    private static final int REQUEST_CODE_ADD_EDIT_ADDRESS = 1001;

    private static final String PREF_NAME = "user_session";
    private static final String KEY_USER_ID = "user_id";

    private RecyclerView recyclerViewAddresses;
    private AddressAdapter addressAdapter;
    private List<Address> addressList; // This list is initialized once and passed to adapter
    private RequestQueue requestQueue;
    private Button buttonAddNewAddress;
    private Button buttonProceedToCheckoutFinal;
    private TextView textViewNoAddresses;

    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_selection);

        Toolbar toolbar = findViewById(R.id.toolbar_address_selection);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Select Delivery Address");
        }

        recyclerViewAddresses = findViewById(R.id.recyclerViewAddresses);
        buttonAddNewAddress = findViewById(R.id.buttonAddNewAddress);
        buttonProceedToCheckoutFinal = findViewById(R.id.buttonProceedToCheckoutFinal);
        textViewNoAddresses = findViewById(R.id.textViewNoAddresses);

        recyclerViewAddresses.setLayoutManager(new LinearLayoutManager(this));
        addressList = new ArrayList<>(); // Initialize the list for the adapter
        addressAdapter = new AddressAdapter(this, addressList, this); // Pass this reference to the adapter
        recyclerViewAddresses.setAdapter(addressAdapter);

        requestQueue = Volley.newRequestQueue(this);

        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt(KEY_USER_ID, -1);

        Log.d(TAG, "Current User ID from SharedPreferences: " + currentUserId);

        if (currentUserId == -1) {
            Toast.makeText(this, "User ID not found. Please log in.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        fetchUserAddresses();

        buttonAddNewAddress.setOnClickListener(v -> {
            Intent intent = new Intent(AddressSelectionActivity.this, AddEditAddressActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_EDIT_ADDRESS);
        });

        buttonProceedToCheckoutFinal.setOnClickListener(v -> {
            Address selectedAddress = addressAdapter.getSelectedAddress();
            if (selectedAddress != null) {
                Intent intent = new Intent(AddressSelectionActivity.this, CheckoutActivity.class);
                intent.putExtra("selected_address", selectedAddress);
                startActivity(intent);
            } else {
                Toast.makeText(AddressSelectionActivity.this, "Please select an address first.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void fetchUserAddresses() {
        if (currentUserId == -1) {
            Log.e(TAG, "User ID not set. Cannot fetch addresses.");
            return;
        }

        String url = GET_ADDRESSES_URL + "?user_id=" + currentUserId;
        Log.d(TAG, "Fetching addresses from URL: " + url);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    Log.d(TAG, "API Response for addresses: " + response.toString());
                    List<Address> fetchedAddressesFromApi = new ArrayList<>(); // Use a new temporary list
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject addressObject = response.getJSONObject(i);
                            Address address = new Address();
                            address.setAddressId(addressObject.getInt("address_id"));
                            address.setUserId(addressObject.getInt("user_id"));
                            address.setAddressTitle(addressObject.getString("address_title"));
                            address.setFullAddress(addressObject.getString("full_address"));
                            address.setStreetNumber(addressObject.optString("street_number"));
                            address.setStreetName(addressObject.optString("street_name"));
                            address.setCity(addressObject.getString("city"));
                            address.setDistrict(addressObject.optString("district"));
                            address.setProvince(addressObject.optString("province"));
                            address.setPostalCode(addressObject.optString("postal_code"));
                            address.setCountry(addressObject.optString("country"));
                            address.setDefault(addressObject.getInt("is_default") == 1);

                            if (addressObject.has("delivery_fee") && !addressObject.isNull("delivery_fee")) {
                                try {
                                    address.setDelivery_fee(new BigDecimal(addressObject.getString("delivery_fee")));
                                } catch (NumberFormatException e) {
                                    Log.e(TAG, "Error parsing delivery_fee to BigDecimal for address_id " + address.getAddressId() + ": " + addressObject.optString("delivery_fee") + " - " + e.getMessage());
                                    address.setDelivery_fee(BigDecimal.ZERO);
                                }
                            } else {
                                address.setDelivery_fee(BigDecimal.ZERO);
                            }

                            fetchedAddressesFromApi.add(address);
                        }
                        // Now, pass this newly populated temporary list to the adapter
                        addressAdapter.updateAddresses(fetchedAddressesFromApi);
                        Log.d(TAG, "fetchUserAddresses: Fetched " + fetchedAddressesFromApi.size() + " addresses. Adapter updated.");
                        updateUIBasedOnAddresses();

                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error: " + e.getMessage() + " Response: " + response.toString());
                        Toast.makeText(AddressSelectionActivity.this, "Error parsing address data.", Toast.LENGTH_SHORT).show();
                        // Even on parsing error, try to update UI based on what we have (likely empty)
                        addressAdapter.updateAddresses(new ArrayList<>()); // Clear adapter if parsing fails entirely
                        updateUIBasedOnAddresses();
                    }
                },
                error -> {
                    String errorMessage = "Failed to load addresses. Check network.";
                    if (error.networkResponse != null) {
                        errorMessage += " Status: " + error.networkResponse.statusCode;
                        try {
                            String errorData = new String(error.networkResponse.data);
                            Log.e(TAG, "Volley error data: " + errorData);
                            errorMessage += " Data: " + errorData.substring(0, Math.min(errorData.length(), 100));
                        } catch (Exception e) {
                            Log.e(TAG, "Error getting Volley error data: " + e.getMessage());
                        }
                    } else if (error.getMessage() != null) {
                        errorMessage += " Error: " + error.getMessage();
                    }
                    Log.e(TAG, "Volley error fetching addresses: " + error.toString());
                    Toast.makeText(AddressSelectionActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    // On network error, clear adapter and update UI to show 'No addresses'
                    addressAdapter.updateAddresses(new ArrayList<>());
                    updateUIBasedOnAddresses();
                }
        );
        requestQueue.add(jsonArrayRequest);
    }

    private void updateUIBasedOnAddresses() {
        Log.d(TAG, "updateUIBasedOnAddresses: Adapter item count = " + addressAdapter.getItemCount());

        if (addressAdapter.getItemCount() == 0) {
            textViewNoAddresses.setVisibility(View.VISIBLE);
            recyclerViewAddresses.setVisibility(View.GONE);
            buttonProceedToCheckoutFinal.setEnabled(false);
            buttonProceedToCheckoutFinal.setAlpha(0.5f);
            Log.d(TAG, "updateUIBasedOnAddresses: UI set to show 'No addresses found'. RecyclerView visibility: " + recyclerViewAddresses.getVisibility());
        } else {
            textViewNoAddresses.setVisibility(View.GONE);
            recyclerViewAddresses.setVisibility(View.VISIBLE);
            buttonProceedToCheckoutFinal.setEnabled(true);
            buttonProceedToCheckoutFinal.setAlpha(1.0f);
            Log.d(TAG, "updateUIBasedOnAddresses: UI set to show RecyclerView. TextView visibility: " + textViewNoAddresses.getVisibility());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_EDIT_ADDRESS && resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult: Address added/edited, refetching addresses.");
            fetchUserAddresses();
        }
    }

    @Override
    public void onAddressSelected(Address address) {
        Log.d(TAG, "Address selected: " + address.getAddressTitle());
    }

    @Override
    public void onEditAddress(Address address) {
        Log.d(TAG, "Editing address: " + address.getAddressTitle());
        Intent intent = new Intent(AddressSelectionActivity.this, AddEditAddressActivity.class);
        intent.putExtra("address", address);
        startActivityForResult(intent, REQUEST_CODE_ADD_EDIT_ADDRESS);
    }

    @Override
    public void onDeleteAddress(Address address) {
        Log.d(TAG, "Attempting to delete address: " + address.getAddressTitle());
        new AlertDialog.Builder(this)
                .setTitle("Delete Address")
                .setMessage("Are you sure you want to delete this address?")
                .setPositiveButton("Delete", (dialog, which) -> confirmDeleteAddress(address))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmDeleteAddress(Address address) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DELETE_ADDRESS_URL,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        String message = jsonResponse.getString("message");
                        Toast.makeText(AddressSelectionActivity.this, message, Toast.LENGTH_SHORT).show();
                        if (success) {
                            Log.d(TAG, "confirmDeleteAddress: Delete successful, refetching addresses.");
                            fetchUserAddresses();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error on delete: " + e.getMessage() + " Response: " + response);
                        Toast.makeText(AddressSelectionActivity.this, "Error processing delete response.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String errorMessage = "Failed to delete address. Check network.";
                    if (error.networkResponse != null) {
                        errorMessage += " Status: " + error.networkResponse.statusCode;
                        try {
                            String errorData = new String(error.networkResponse.data);
                            Log.e(TAG, "Volley error data: " + errorData);
                            errorMessage += " Data: " + errorData.substring(0, Math.min(errorData.length(), 100));
                        } catch (Exception e) {
                            Log.e(TAG, "Error getting error data: " + e.getMessage());
                        }
                    } else if (error.getMessage() != null) {
                        errorMessage += " Error: " + error.getMessage();
                    }
                    Log.e(TAG, "Volley error deleting address: " + error.toString());
                    Toast.makeText(AddressSelectionActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("address_id", String.valueOf(address.getAddressId()));
                params.put("user_id", String.valueOf(currentUserId));
                Log.d(TAG, "Delete params: " + params.toString());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    @Override
    public void onSetDefaultAddress(Address address, boolean isChecked) {
        Log.d(TAG, "Attempting to set default address: " + address.getAddressTitle() + ", isChecked: " + isChecked);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SET_DEFAULT_ADDRESS_URL,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        String message = jsonResponse.getString("message");
                        Toast.makeText(AddressSelectionActivity.this, message, Toast.LENGTH_SHORT).show();
                        if (success) {
                            Log.d(TAG, "onSetDefaultAddress: Set default successful, refetching addresses.");
                            fetchUserAddresses();
                        } else {
                            fetchUserAddresses(); // Refetch even on failure to revert UI checkbox if necessary
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error on set default: " + e.getMessage() + " Response: " + response);
                        Toast.makeText(AddressSelectionActivity.this, "Error processing set default response.", Toast.LENGTH_SHORT).show();
                        fetchUserAddresses(); // Revert UI on JSON error
                    }
                },
                error -> {
                    String errorMessage = "Failed to set default address. Check network.";
                    if (error.networkResponse != null) {
                        errorMessage += " Status: " + error.networkResponse.statusCode;
                        try {
                            String errorData = new String(error.networkResponse.data);
                            Log.e(TAG, "Volley error data: " + errorData);
                            errorMessage += " Data: " + errorData.substring(0, Math.min(errorData.length(), 100));
                        } catch (Exception e) {
                            Log.e(TAG, "Error getting error data: " + e.getMessage());
                        }
                    } else if (error.getMessage() != null) {
                        errorMessage += " Error: " + error.getMessage();
                    }
                    Log.e(TAG, "Volley error setting default address: " + error.toString());
                    Toast.makeText(AddressSelectionActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    fetchUserAddresses(); // Revert UI on network error
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("address_id", String.valueOf(address.getAddressId()));
                params.put("user_id", String.valueOf(currentUserId));
                params.put("is_default", isChecked ? "1" : "0");
                Log.d(TAG, "Set default params: " + params.toString());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}
