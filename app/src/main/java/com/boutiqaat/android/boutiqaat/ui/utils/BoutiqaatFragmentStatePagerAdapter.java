package com.boutiqaat.android.boutiqaat.ui.utils;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

/**
 * Adapter for showing Fragments in ViewPager.
 */
public abstract class BoutiqaatFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
    // All registered fragments will be kept in memory using Sparse array
    private SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    public BoutiqaatFragmentStatePagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    // Fragment is registered here.
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    // Fragment is unregistered here
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    // Fragment instance is returned based on position.
    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}
