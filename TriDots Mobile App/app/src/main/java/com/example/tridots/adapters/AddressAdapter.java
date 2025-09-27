// Path: app/src/main/java/com/example/tridots/adapters/AddressAdapter.java
package com.example.tridots.adapters;

import android.content.Context;
import android.util.Log; // Import Log
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tridots.R;
import com.example.tridots.models.Address;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private static final String TAG = "AddressAdapter"; // Tag for logging

    private Context context;
    private List<Address> addressList;
    private OnAddressActionListener listener;
    private int selectedAddressPosition = RecyclerView.NO_POSITION; // To track selected address for checkout

    public AddressAdapter(Context context, List<Address> addressList, OnAddressActionListener listener) {
        this.context = context;
        this.addressList = addressList;
        this.listener = listener;
        // Initialize selectedAddressPosition if a default address exists
        for (int i = 0; i < addressList.size(); i++) {
            if (addressList.get(i).isDefault()) {
                selectedAddressPosition = i;
                break;
            }
        }
        Log.d(TAG, "Adapter initialized with " + addressList.size() + " addresses.");
    }

    public interface OnAddressActionListener {
        void onAddressSelected(Address address);
        void onEditAddress(Address address);
        void onDeleteAddress(Address address);
        void onSetDefaultAddress(Address address, boolean isDefault);
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Address address = addressList.get(position);
        holder.textViewAddressTitle.setText(address.getAddressTitle());
        holder.textViewFullAddress.setText(address.getFullAddress());
        // Do not set OnCheckedChangeListener here directly, it can cause loops
        holder.checkBoxIsDefault.setOnCheckedChangeListener(null); // Clear previous listener
        holder.checkBoxIsDefault.setChecked(address.isDefault());

        // Handle selection state
        holder.addressItemLayout.setSelected(position == selectedAddressPosition);
        if (position == selectedAddressPosition) {
            holder.addressItemLayout.setBackgroundResource(R.drawable.selected_address_background); // Custom drawable for selection
        } else {
            // Reset to default background or transparent if not selected
            holder.addressItemLayout.setBackgroundResource(android.R.color.transparent); // Use transparent for default background
        }

        // Set listeners after setting initial checked state
        holder.addressItemLayout.setOnClickListener(v -> {
            int previousSelected = selectedAddressPosition;
            selectedAddressPosition = holder.getAdapterPosition();
            if (previousSelected != RecyclerView.NO_POSITION) {
                notifyItemChanged(previousSelected);
            }
            notifyItemChanged(selectedAddressPosition);
            listener.onAddressSelected(address);
            Log.d(TAG, "Item clicked: " + address.getAddressTitle() + " at position " + position);
        });

        holder.imageViewEditAddress.setOnClickListener(v -> listener.onEditAddress(address));
        holder.imageViewDeleteAddress.setOnClickListener(v -> listener.onDeleteAddress(address));

        // Listener for default checkbox
        holder.checkBoxIsDefault.setOnCheckedChangeListener((buttonView, isChecked) -> {
            listener.onSetDefaultAddress(address, isChecked);
            Log.d(TAG, "Checkbox changed for: " + address.getAddressTitle() + ", isChecked: " + isChecked);
        });
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: returning " + addressList.size());
        return addressList.size();
    }

    public void updateAddresses(List<Address> newAddresses) {
        this.addressList.clear();
        this.addressList.addAll(newAddresses);
        Log.d(TAG, "updateAddresses: List updated. New size = " + this.addressList.size());
        // Re-evaluate selectedAddressPosition if a default address exists
        selectedAddressPosition = RecyclerView.NO_POSITION;
        for (int i = 0; i < addressList.size(); i++) {
            if (addressList.get(i).isDefault()) {
                selectedAddressPosition = i;
                Log.d(TAG, "updateAddresses: Default address found at position " + i);
                break;
            }
        }
        notifyDataSetChanged(); // Notify RecyclerView that data has changed
    }

    public Address getSelectedAddress() {
        if (selectedAddressPosition != RecyclerView.NO_POSITION && selectedAddressPosition < addressList.size()) {
            return addressList.get(selectedAddressPosition);
        }
        return null;
    }


    static class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView textViewAddressTitle;
        TextView textViewFullAddress;
        CheckBox checkBoxIsDefault;
        ImageView imageViewEditAddress;
        ImageView imageViewDeleteAddress;
        LinearLayout addressItemLayout;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewAddressTitle = itemView.findViewById(R.id.textViewAddressTitle);
            textViewFullAddress = itemView.findViewById(R.id.textViewFullAddress);
            checkBoxIsDefault = itemView.findViewById(R.id.checkBoxIsDefault);
            imageViewEditAddress = itemView.findViewById(R.id.imageViewEditAddress);
            imageViewDeleteAddress = itemView.findViewById(R.id.imageViewDeleteAddress);
            addressItemLayout = itemView.findViewById(R.id.address_item_layout);
        }
    }
}
