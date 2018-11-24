package com.boutiqaat.android.boutiqaat.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;

public interface FragmentView {
    void initObservers();
    void init(LayoutInflater inflater, ViewGroup container);
}