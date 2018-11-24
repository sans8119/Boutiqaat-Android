package com.boutiqaat.android.boutiqaat.ui.fragment;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import com.boutiqaat.android.boutiqaat.R;
import com.boutiqaat.android.boutiqaat.databinding.SettingsBinding;
import com.boutiqaat.android.boutiqaat.ui.FragmentView;
import com.boutiqaat.android.boutiqaat.ui.activity.MainActivity;
import com.boutiqaat.android.boutiqaat.ui.activity.SignInActivity;
import com.boutiqaat.android.boutiqaat.ui.activity.SignupActivity;
import com.boutiqaat.android.boutiqaat.utils.Constants;
import com.boutiqaat.android.boutiqaat.utils.Utils;
import com.boutiqaat.android.boutiqaat.viewmodel.implementation.LocationViewModel;
import com.boutiqaat.android.boutiqaat.viewmodel.implementation.ProfileViewModel;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import timber.log.Timber;

/**
 * This is the user interface class for settings.Here user can change laguage to English, Arabic or French;
 * Save location info radio button will enable location updates or disable the feature of location updates.
 * From here he can sign-up, sign-in or sign-out. Sign-out button will only be visible when the user has signed-in.
 * Sing-up and Sign-in buttons will only be visible when the user has not yet signed in.
 */
public class SettingsFragment extends DaggerFragment implements View.OnClickListener,FragmentView {
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private SettingsBinding binding;
    private ProfileViewModel viewModel;
    private LocationViewModel locationViewModel;
    private SharedPreferences prefs;
    private Utils utils;

    @Override
    public void onResume() {
        super.onResume();
        if (prefs.getString(Constants.LOGGED_IN_USER_EMAIL, Constants.ANON).equals(Constants.ANON)) {
            binding.saveBtn.setText(getString(R.string.common_signin_button_text));
            binding.linkSignup.setVisibility(View.VISIBLE);
        } else {
            binding.saveBtn.setText(getString(R.string.sign_out));
            binding.linkSignup.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        init(inflater, container);
        Timber.d("viewModel:" + viewModel + ", " + viewModelFactory);
        return binding.getRoot();
    }

    /**
     * Initializing all the UI components
     *
     * @param inflater
     * @param container
     */
    public void init(LayoutInflater inflater, ViewGroup container) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.settings_fragment, container, false);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel.class);
        locationViewModel = ViewModelProviders.of(this, viewModelFactory).get(LocationViewModel.class);
        utils = new Utils();
        prefs = getActivity().getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, getActivity().getResources().getStringArray(R.array.language_arrays));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        initLanguageSpinner(dataAdapter);

        binding.saveBtn.setOnClickListener(this);
        binding.linkSignup.setOnClickListener(this);

        binding.locationSwitch.setChecked(prefs.getBoolean(Constants.LOCATION_ON, true));
        binding.locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                handleToggleOfLocationSwitch(b);
            }
        });
    }

    public void initObservers() {}

    /**
     * Handling location update on and off
     *
     * @param b
     */
    private void handleToggleOfLocationSwitch(boolean b) {
        prefs.edit().putBoolean(Constants.LOCATION_ON, b).commit();
        if (b) {
            locationViewModel.startLocationUpdates();
        } else {
            locationViewModel.stopLocationUpdates();
        }
    }

    /**
     * Language selection ui initialization and setup
     * @param dataAdapter
     */
    private void initLanguageSpinner(ArrayAdapter<String> dataAdapter) {
        binding.langSpinner.setAdapter(dataAdapter);
        binding.langSpinner.setSelection(prefs.getInt(Constants.LANGUAGE, Constants.POSTION_ENG), false);
        binding.langSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                prefs.edit().putInt(Constants.LANGUAGE, position).commit();
                String locale = new Utils().getLocale(position);
                utils.setLocale(locale, getActivity());
                Intent refresh = new Intent(getActivity(), MainActivity.class);
                prefs.edit().putBoolean(Constants.REFRESH, true).commit();
                startActivity(refresh);
                getActivity().finish();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    /**
     * Observer of ui button click events
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        if (view.getId() == binding.saveBtn.getId()) {
            handleSaveButtonClick();
        } else if (view.getId() == binding.linkSignup.getId()) {
            handleSigupLinkClick();
        }
    }

    /**
     * Lauching sign-up screen.
     */
    private void handleSigupLinkClick() {
        Intent intent = new Intent(getActivity(), SignupActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    /**
     * Sign-in lauch or handling Sign-out
     */
    private void handleSaveButtonClick() {
        if (binding.saveBtn.getText().equals(getActivity().getString(R.string.sign_out))) {
            prefs.edit().putString(Constants.LOGGED_IN_USER_EMAIL, Constants.ANON).
                    putBoolean(Constants.REFRESH, true).commit();
            Intent refresh = new Intent(getActivity(), MainActivity.class);
            viewModel.setProfileDataOfLoggedInUser(null);
            startActivity(refresh);
            getActivity().finish();
        } else {
            Intent intent = new Intent(getActivity(), SignInActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
    }
}