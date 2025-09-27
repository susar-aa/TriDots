package com.example.tridots;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tridots.adapters.InquiryAdapter;
import com.example.tridots.main_screens.InquiriesActivity;
import com.example.tridots.models.Inquiry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InquiryListFragment extends Fragment implements InquiryAdapter.OnInquiryActionListener {
    private RecyclerView recyclerView;
    private InquiryAdapter adapter;
    private List<Inquiry> inquiryList = new ArrayList<>();
    private TextView emptyTextView;
    private RequestQueue requestQueue;

    public InquiryListFragment() {
        // Required empty public constructor
    }

    public static InquiryListFragment newInstance() {
        return new InquiryListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new InquiryAdapter(inquiryList);
        adapter.setOnInquiryActionListener(this); // Set the listener
        requestQueue = Volley.newRequestQueue(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inquiry_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewInquiries);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        emptyTextView = view.findViewById(R.id.emptyTextView);
        updateEmptyState();
        return view;
    }

    public void updateData(List<Inquiry> newInquiryList) {inquiryList.clear();
        inquiryList.addAll(newInquiryList);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (emptyTextView != null) {
            emptyTextView.setVisibility(inquiryList.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onCancelClicked(Inquiry inquiry) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Cancel Inquiry")
                .setMessage("Are you sure you want to cancel this inquiry?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    cancelInquiry(inquiry.getInquiryId());
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onConfirmClicked(Inquiry inquiry) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirm Inquiry")
                .setMessage("Are you sure you want to confirm this inquiry?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    confirmInquiry(inquiry.getInquiryId());
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void cancelInquiry(int inquiryId) {
        String CANCEL_INQUIRY_URL = "https://lionsgoldencircle.com/Tridots/Api/cancel_inquiry.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CANCEL_INQUIRY_URL,
                response -> {
                    Log.d("Cancel Inquiry", "Response: " + response);
                    if (response.trim().equals("success")) {
                        Toast.makeText(requireContext(), "Inquiry cancelled successfully", Toast.LENGTH_SHORT).show();
                        ((InquiriesActivity) requireActivity()).fetchInquiries();
                    } else {
                        Toast.makeText(requireContext(), "Failed to cancel inquiry: " + response, Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Log.e("Cancel Inquiry", "Volley Error: " + error.getMessage());
                    Toast.makeText(requireContext(), "Failed to cancel inquiry", Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("inquiry_id", String.valueOf(inquiryId));
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void confirmInquiry(int inquiryId) {
        String CONFIRM_INQUIRY_URL = "https://lionsgoldencircle.com/Tridots/Api/confirm_inquiry.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CONFIRM_INQUIRY_URL,
                response -> {
                    Log.d("Confirm Inquiry", "Response: " + response);
                    if (response.trim().equals("success")) {
                        Toast.makeText(requireContext(), "Inquiry confirmed successfully", Toast.LENGTH_SHORT).show();
                        ((InquiriesActivity) requireActivity()).fetchInquiries();
                    } else {
                        Toast.makeText(requireContext(), "Failed to confirm inquiry: " + response, Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Log.e("Confirm Inquiry", "Volley Error: " + error.getMessage());
                    Toast.makeText(requireContext(), "Failed to confirm inquiry", Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("inquiry_id", String.valueOf(inquiryId));
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}