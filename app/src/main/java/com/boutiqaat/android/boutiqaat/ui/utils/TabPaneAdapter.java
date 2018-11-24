package com.boutiqaat.android.boutiqaat.ui.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for data of tabs shown in the MainActivity.
 */
public class TabPaneAdapter extends BoutiqaatFragmentStatePagerAdapter {
    private final List<Fragment> fragments = new ArrayList<>();

    public TabPaneAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    public void addFragments(Fragment fragment) {
        fragments.add(fragment);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}