package com.boutiqaat.android.boutiqaat.ui.fragment;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.boutiqaat.android.boutiqaat.R;
import com.boutiqaat.android.boutiqaat.databinding.LocationsBinding;
import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.CustomerLocation;
import com.boutiqaat.android.boutiqaat.ui.FragmentView;
import com.boutiqaat.android.boutiqaat.ui.utils.LocationsAdapter;
import com.boutiqaat.android.boutiqaat.utils.Constants;
import com.boutiqaat.android.boutiqaat.utils.GridLayoutManagerExtn;
import com.boutiqaat.android.boutiqaat.utils.Utils;
import com.boutiqaat.android.boutiqaat.viewmodel.implementation.LocationViewModel;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

import java.util.LinkedHashMap;
import java.util.Objects;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import timber.log.Timber;

/**
 * This is the user interface class for showing location updates of the user. A location update will be shown on the user interace
 * for every 500 m movement from current position ; users current location update can also be shown instantaneously when 'Trigger Location Update'
 * button is clicked.
 */
public class LocationsFragment extends DaggerFragment implements LocationsAdapter.AdapterListener, View.OnClickListener,FragmentView {
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private LocationsBinding binding;
    private LocationViewModel viewModel;
    private RecyclerView recyclerView;
    private LocationsAdapter mAdapter;
    private boolean getCurrentLocation;
    private SharedPreferences prefs;

    //The Observer for location updates.
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null || (!prefs.getBoolean(Constants.LOCATION_ON, true))) {
                return;
            }
            Timber.d("LOcFrag * :" + getCurrentLocation + "    " + locationResult.getLocations().size() +
                    new Utils().getAddress(locationResult.getLocations().get(0).getLatitude(), locationResult.getLocations().get(0).getLongitude(), getActivity()));
            for (Location location : locationResult.getLocations()) {
                if (viewModel.updateLocation(location) || getCurrentLocation) {
                    if (getCurrentLocation) {
                        Timber.d(" 0 getCurrentLocation:" + getCurrentLocation);
                        getCurrentLocation = false;
                        viewModel.stopLocationUpdates();
                        viewModel.startLocationUpdates();
                    }
                    String add = new Utils().getAddress(location.getLatitude(), location.getLongitude(), getActivity());
                    if (add.length() > 0) {
                        CustomerLocation userLocation = new CustomerLocation();
                        userLocation.locationString = add;
                        userLocation.email = getActivity().getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE)
                                .getString(Constants.LOGGED_IN_USER_EMAIL, Constants.ANON);
                        Timber.d("---***>" + userLocation);
                        if (recyclerView.getAdapter() == null)
                            recyclerView.setAdapter(mAdapter);
                        if (mAdapter != null) {
                            if (mAdapter.getResultsMap() != null) {
                                int size = mAdapter.getResultsMap().size();
                                LinkedHashMap<String, CustomerLocation> map
                                        = new LinkedHashMap<String, CustomerLocation>();
                                map.put(userLocation.locationString, userLocation);
                                map.putAll(mAdapter.getResultsMap());
                                mAdapter.setResultsMap(map);
                                //mAdapter.getResultsMap().put(userLocation.locationString, userLocation);
                                if (size == mAdapter.getResultsMap().size())
                                    mAdapter.notifyDataSetChanged();
                                else
                                    //mAdapter.notifyItemRangeInserted(0, 1);
                                    mAdapter.notifyDataSetChanged();
                            } else {
                                LinkedHashMap<String, CustomerLocation> map = new LinkedHashMap<String, CustomerLocation>();
                                map.put(userLocation.locationString, userLocation);
                                mAdapter.setResultsMap(map);
                                recyclerView.setAdapter(mAdapter);
                            }
                        } else {
                            LinkedHashMap<String, CustomerLocation> map = new LinkedHashMap<String, CustomerLocation>();
                            map.put(userLocation.locationString, userLocation);
                            mAdapter = new LocationsAdapter(map, LocationsFragment.this);
                            recyclerView.setAdapter(mAdapter);
                        }
                    }
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        init(inflater, container);
        return binding.getRoot();
    }

    public void init(LayoutInflater inflater, ViewGroup container) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.locations_activity, container, false);
        prefs = getActivity().getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE);
        binding.getCurrentLocation.setOnClickListener(this);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(LocationViewModel.class);
        Timber.d("viewModelFactory:" + viewModelFactory + ", " + viewModel);
        initRecyclerView();
        initObservers();
        viewModel.getLocationDetails(prefs
                .getString(Constants.LOGGED_IN_USER_EMAIL, Constants.ANON));
        Timber.d("----onCreate----");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity().getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE).getBoolean(Constants.LOCATION_ON, true)) {
            viewModel.stopLocationUpdates();
            viewModel.setLocationCallback(mLocationCallback);
            viewModel.startLocationUpdates();
        }
    }

    /**
     * Instantiating list and grid user interface.
     */
    private void initRecyclerView() {
        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new GridLayoutManagerExtn(this.getActivity(), 1));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        LinkedHashMap<String, CustomerLocation> map = new LinkedHashMap<String, CustomerLocation>();
    }

    /**
     * Registering observers for recieving location update data obtained from platform ,captured and published by LocationViewModel.
     */
    public void initObservers() {
        Timber.d("--out--initObservers----");
        viewModel.getLocationsLiveData().observe(this, map -> {
            binding.pb.setVisibility(View.GONE);
            if (Objects.requireNonNull(map).size() == 0) {
            } else {
                Timber.d("--in--initObservers----" + map);
                if (mAdapter == null || recyclerView.getAdapter() == null) {
                    mAdapter = new LocationsAdapter(map, LocationsFragment.this);
                    recyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                } else {
                    Timber.d("---->recyclerView.getAdapter:" + recyclerView.getAdapter());
                    int mapSize = mAdapter.getResultsMap().keySet().size();

                    mAdapter.setResultsMap(map);
                    //mAdapter.notifyItemRangeInserted(mapSize, map.size() - 1);
                    mAdapter.notifyDataSetChanged();
                }
            }
            binding.pb.setVisibility(View.GONE);
        });
    }

    @Override
    public void onPostClicked(CustomerLocation customerLocation, int viewId, int position) {

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == binding.getCurrentLocation.getId()) {
            viewModel.stopLocationUpdates();
            viewModel.setLocationCallback(mLocationCallback);
            viewModel.startLocationUpdates(1000, 1000);
            getCurrentLocation = true;
            Timber.d("1 getCurrentLocation:" + getCurrentLocation);
        }
    }
}
