package com.example.tridots.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tridots.R;
import com.example.tridots.adapters.AdAdapter;
import com.example.tridots.models.Ad;

import java.util.ArrayList;

public class AdsListFragment extends Fragment {

    private static final String ARG_ADS = "ads_list";
    private ArrayList<Ad> adList;
    private AdAdapter.OnDeleteClickListener deleteClickListener;

    public static AdsListFragment newInstance(ArrayList<Ad> adList) {
        AdsListFragment fragment = new AdsListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_ADS, adList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            adList = getArguments().getParcelableArrayList(ARG_ADS);
        }
        // Ensure the hosting activity implements the listener
        if (getActivity() instanceof AdAdapter.OnDeleteClickListener) {
            deleteClickListener = (AdAdapter.OnDeleteClickListener) getActivity();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ads_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewFragment);
        TextView textViewNoAds = view.findViewById(R.id.textViewNoAdsFragment);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        AdAdapter adAdapter = new AdAdapter(getContext(), adList);
        if (deleteClickListener != null) {
            adAdapter.setOnDeleteClickListener(deleteClickListener);
        }
        recyclerView.setAdapter(adAdapter);

        if (adList == null || adList.isEmpty()) {
            textViewNoAds.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            textViewNoAds.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        return view;
    }
}
