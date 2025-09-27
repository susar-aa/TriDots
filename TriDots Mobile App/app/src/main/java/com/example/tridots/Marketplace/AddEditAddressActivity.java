// Path: app/src/main/java/com/example/tridots/AddEditAddressActivity.java
package com.example.tridots.Marketplace;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tridots.R;
import com.example.tridots.models.Address;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Activity for adding a new address or editing an existing one.
 * It includes functionality for:
 * - Displaying a form for address details.
 * - Using a spinner for predefined address titles (Home, Work, Other).
 * - Showing a custom text input for 'Other' address titles.
 * - Validating required fields like Full Address, City, and Postal Code.
 * - Sending address data to the backend API via Volley.
 * - Handling responses from the API (success/failure).
 * - Retrieving the current user's ID from SharedPreferences.
 */
public class AddEditAddressActivity extends AppCompatActivity {

    private static final String TAG = "AddEditAddressActivity";
    private static final String API_BASE_URL = "https://lionsgoldencircle.com/Tridots/Api/";
    private static final String ADD_EDIT_ADDRESS_URL = API_BASE_URL + "add_edit_address.php";

    // SharedPreferences constants for user session management, consistent with LoginActivity
    private static final String PREF_NAME = "user_session";
    private static final String KEY_USER_ID = "user_id";

    // UI elements
    private Spinner spinnerAddressTitle;
    private TextInputLayout textInputLayoutOtherTitle;
    private TextInputEditText editTextOtherTitle, editTextFullAddress, editTextStreetNumber,
            editTextStreetName, editTextCity, editTextDistrict, editTextProvince,
            editTextPostalCode, editTextCountry;
    private CheckBox checkBoxSetAsDefault;
    private Button buttonSaveAddress;

    private Address existingAddress = null; // Holds the Address object if in edit mode, null if adding new
    private RequestQueue requestQueue; // Volley RequestQueue for network operations
    private int currentUserId; // Stores the ID of the currently logged-in user

    // Predefined options for the address title spinner
    private String[] addressTitleOptions = {"Home", "Work", "Other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_address);

        // Set up the Toolbar for the activity
        Toolbar toolbar = findViewById(R.id.toolbar_add_edit_address);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button
        }

        // Initialize UI elements by finding their IDs in the layout
        spinnerAddressTitle = findViewById(R.id.spinnerAddressTitle);
        textInputLayoutOtherTitle = findViewById(R.id.textInputLayoutOtherTitle);
        editTextOtherTitle = findViewById(R.id.editTextOtherTitle);
        editTextFullAddress = findViewById(R.id.editTextFullAddress);
        editTextStreetNumber = findViewById(R.id.editTextStreetNumber);
        editTextStreetName = findViewById(R.id.editTextStreetName);
        editTextCity = findViewById(R.id.editTextCity);
        editTextDistrict = findViewById(R.id.editTextDistrict);
        editTextProvince = findViewById(R.id.editTextProvince);
        editTextPostalCode = findViewById(R.id.editTextPostalCode);
        editTextCountry = findViewById(R.id.editTextCountry);
        checkBoxSetAsDefault = findViewById(R.id.checkBoxSetAsDefault);
        buttonSaveAddress = findViewById(R.id.buttonSaveAddress);

        // Initialize Volley RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        // Retrieve the current user ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt(KEY_USER_ID, -1);

        // If user ID is not found (e.g., not logged in), show a toast and finish activity
        if (currentUserId == -1) {
            Toast.makeText(this, "User not logged in. Please log in.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Set up the ArrayAdapter for the address title spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, addressTitleOptions);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAddressTitle.setAdapter(spinnerAdapter);

        // Set up an OnItemSelectedListener for the spinner to handle 'Other' selection
        spinnerAddressTitle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                if ("Other".equals(selectedItem)) {
                    // If "Other" is selected, make the custom title input field visible
                    textInputLayoutOtherTitle.setVisibility(View.VISIBLE);
                    editTextOtherTitle.requestFocus(); // Give focus to the custom input
                } else {
                    // Otherwise, hide the custom title input and clear its text
                    textInputLayoutOtherTitle.setVisibility(View.GONE);
                    editTextOtherTitle.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed when nothing is selected
            }
        });


        // Check if the activity was launched to edit an existing address
        if (getIntent().hasExtra("address")) {
            existingAddress = (Address) getIntent().getSerializableExtra("address");
            if (existingAddress != null) {
                // If in edit mode, set toolbar title and populate fields with existing address data
                getSupportActionBar().setTitle("Edit Address");
                populateFields(existingAddress);
            }
        } else {
            // If in add new mode, set toolbar title and set default country
            getSupportActionBar().setTitle("Add New Address");
            editTextCountry.setText("Sri Lanka");
            // Set default selection for spinner to "Home" for new addresses
            spinnerAddressTitle.setSelection(spinnerAdapter.getPosition("Home"));
        }

        buttonSaveAddress.setOnClickListener(v -> saveAddress());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Simulate back button press
        return true;
    }

    /**
     * Populates the form fields with data from an existing Address object when in edit mode.
     * @param address The Address object containing the data to populate.
     */
    private void populateFields(Address address) {
        // Determine the address title for the spinner
        String addressTitle = address.getAddressTitle();
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerAddressTitle.getAdapter();
        int spinnerPosition = adapter.getPosition(addressTitle);

        if (spinnerPosition == -1) {
            // If the address title is not in predefined options, select "Other" and fill custom field
            spinnerAddressTitle.setSelection(adapter.getPosition("Other"));
            editTextOtherTitle.setText(addressTitle);
            textInputLayoutOtherTitle.setVisibility(View.VISIBLE);
        } else {
            // If the address title is predefined, select it in the spinner
            spinnerAddressTitle.setSelection(spinnerPosition);
            textInputLayoutOtherTitle.setVisibility(View.GONE);
        }

        // Populate other address fields
        editTextFullAddress.setText(address.getFullAddress());
        editTextStreetNumber.setText(address.getStreetNumber());
        editTextStreetName.setText(address.getStreetName());
        editTextCity.setText(address.getCity());
        editTextDistrict.setText(address.getDistrict());
        editTextProvince.setText(address.getProvince());
        editTextPostalCode.setText(address.getPostalCode());
        editTextCountry.setText(address.getCountry());
        checkBoxSetAsDefault.setChecked(address.isDefault());
    }

    /**
     * Collects data from the form fields, performs validation, and sends it to the backend API.
     */
    private void saveAddress() {
        String addressTitle;
        String selectedSpinnerTitle = spinnerAddressTitle.getSelectedItem().toString();

        // Determine the final address title based on spinner selection
        if ("Other".equals(selectedSpinnerTitle)) {
            addressTitle = editTextOtherTitle.getText().toString().trim();
            if (addressTitle.isEmpty()) {
                Toast.makeText(this, "Please enter a custom address title.", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            addressTitle = selectedSpinnerTitle;
        }

        // Retrieve other field values
        String fullAddress = editTextFullAddress.getText().toString().trim();
        String streetNumber = editTextStreetNumber.getText().toString().trim();
        String streetName = editTextStreetName.getText().toString().trim();
        String city = editTextCity.getText().toString().trim();
        String district = editTextDistrict.getText().toString().trim();
        String province = editTextProvince.getText().toString().trim();
        String postalCode = editTextPostalCode.getText().toString().trim();
        String country = editTextCountry.getText().toString().trim();
        boolean isDefault = checkBoxSetAsDefault.isChecked();

        // Validate required fields: Full Address, City, Postal Code
        if (fullAddress.isEmpty() || city.isEmpty() || postalCode.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields (Full Address, City, Postal Code).", Toast.LENGTH_LONG).show();
            return;
        }

        // Create a StringRequest for sending data to the API
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ADD_EDIT_ADDRESS_URL,
                response -> {
                    // Success listener: handle the API response
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        String message = jsonResponse.getString("message");
                        Toast.makeText(AddEditAddressActivity.this, message, Toast.LENGTH_SHORT).show();
                        if (success) {
                            setResult(RESULT_OK); // Notify calling activity (AddressSelectionActivity) of success
                            finish(); // Close this activity
                        }
                    } catch (JSONException e) {
                        // Log and toast if JSON parsing fails
                        Log.e(TAG, "JSON parsing error on save: " + e.getMessage() + " Response: " + response);
                        Toast.makeText(AddEditAddressActivity.this, "Error processing save response.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Error listener: handle network or server errors
                    String errorMessage = "Failed to save address. Check network.";
                    if (error.networkResponse != null) {
                        // Attempt to get status code and error data from network response
                        errorMessage += " Status: " + error.networkResponse.statusCode;
                        try {
                            String errorData = new String(error.networkResponse.data);
                            Log.e(TAG, "Volley error data: " + errorData);
                            // Append a snippet of the error data to the toast message
                            errorMessage += " Data: " + errorData.substring(0, Math.min(errorData.length(), 100));
                        } catch (Exception e) {
                            Log.e(TAG, "Error getting error data: " + e.getMessage());
                        }
                    } else if (error.getMessage() != null) {
                        errorMessage += " Error: " + error.getMessage();
                    }
                    Log.e(TAG, "Volley error saving address: " + error.toString());
                    Toast.makeText(AddEditAddressActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Prepare POST parameters to send to the API
                Map<String, String> params = new HashMap<>();
                if (existingAddress != null) {
                    // Include address_id if updating an existing address
                    params.put("address_id", String.valueOf(existingAddress.getAddressId()));
                }
                params.put("user_id", String.valueOf(currentUserId));
                params.put("address_title", addressTitle); // Use the resolved address title
                params.put("full_address", fullAddress);
                params.put("street_number", streetNumber);
                params.put("street_name", streetName);
                params.put("city", city);
                params.put("district", district);
                params.put("province", province);
                params.put("postal_code", postalCode);
                params.put("country", country);
                params.put("is_default", isDefault ? "1" : "0"); // Convert boolean to "1" or "0"
                Log.d(TAG, "Sending params: " + params.toString()); // Log the parameters being sent
                return params;
            }
        };
        requestQueue.add(stringRequest); // Add the request to the Volley queue
    }
}
