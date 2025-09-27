// Path: app/src/main/java/com/example/tridots/fragments/PaymentMethodFragment.java

package com.example.tridots.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tridots.Marketplace.CheckoutActivity;
import com.example.tridots.R;
// import com.stripe.android.view.CardInputWidget; // Uncomment if you add Stripe CardInputWidget

public class PaymentMethodFragment extends Fragment {

    private RadioGroup radioGroupPaymentMethod;
    private RadioButton radioStripe, radioCOD;
    private Button buttonConfirmPayment;
    // private CardInputWidget cardInputWidget; // Uncomment if you add Stripe CardInputWidget

    private String selectedPaymentMethod = "stripe"; // Default

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment_method, container, false);

        radioGroupPaymentMethod = view.findViewById(R.id.radioGroupPaymentMethod);
        radioStripe = view.findViewById(R.id.radioStripe);
        radioCOD = view.findViewById(R.id.radioCOD);
        buttonConfirmPayment = view.findViewById(R.id.buttonConfirmPayment);
        // cardInputWidget = view.findViewById(R.id.cardInputWidget); // Uncomment if you add Stripe CardInputWidget

        radioGroupPaymentMethod.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioStripe) {
                selectedPaymentMethod = "stripe";
                // if (cardInputWidget != null) cardInputWidget.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.radioCOD) {
                selectedPaymentMethod = "cod";
                // if (cardInputWidget != null) cardInputWidget.setVisibility(View.GONE);
            }
        });

        // Initial visibility for card input widget
        // if (cardInputWidget != null) cardInputWidget.setVisibility(selectedPaymentMethod.equals("stripe") ? View.VISIBLE : View.GONE);


        buttonConfirmPayment.setOnClickListener(v -> {
            if (validatePaymentInputs()) {
                if (getActivity() instanceof CheckoutActivity) {
                    CheckoutActivity activity = (CheckoutActivity) getActivity();
                    // Inform the activity about the selected payment method
                    activity.setSelectedPaymentMethod(selectedPaymentMethod);

                    activity.placeOrderFromFragment();
                }
            }
        });

        return view;
    }

    private boolean validatePaymentInputs() {
        if (selectedPaymentMethod.equals("stripe")) {
            // Validate Stripe card input (if you add CardInputWidget)
            // if (cardInputWidget == null || !cardInputWidget.getCard().validateCard()) {
            //     Toast.makeText(getContext(), "Please enter valid card details.", Toast.LENGTH_SHORT).show();
            //     return false;
            // }
            Toast.makeText(getContext(), "Stripe validation placeholder.", Toast.LENGTH_SHORT).show(); // Placeholder
            return true; // Assume valid for now
        } else if (selectedPaymentMethod.equals("cod")) {
            // No specific validation needed for COD
            return true;
        }
        return false;
    }

    // Public getter for selected payment method
    public String getSelectedPaymentMethod() {
        return selectedPaymentMethod;
    }

    // Public getter for card details (if you use CardInputWidget)
    // public Card getCardDetails() {
    //     return cardInputWidget.getCard();
    // }
}