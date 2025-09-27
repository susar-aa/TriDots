package com.example.tridots.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tridots.fragments.AdsListFragment;
import com.example.tridots.models.Ad;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private final List<String> titles = new ArrayList<>();
    private final List<ArrayList<Ad>> adLists = new ArrayList<>();

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public void addFragment(String title, ArrayList<Ad> adList) {
        titles.add(title);
        adLists.add(adList);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return AdsListFragment.newInstance(adLists.get(position));
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
