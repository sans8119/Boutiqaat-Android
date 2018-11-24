package com.boutiqaat.android.boutiqaat.repository;

import android.arch.lifecycle.LiveData;

import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.CustomerLocation;
import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.ProfileData;

import java.util.LinkedHashMap;

import io.reactivex.disposables.CompositeDisposable;

public interface Repository {
    LiveData getNetworkOrDbLiveData();

    LiveData getLocationsLiveData();

    LiveData getProfilesLiveData();

    void setLocationData(LinkedHashMap<String, CustomerLocation> customers);

    void setProfileData(LinkedHashMap<String, ProfileData> profileData);

    void getProfileDetails(String email);

    void fetchImagesDataFromServer();

    CompositeDisposable getCompositeDisposable();

    void getLocationDetails(String email);

    void fetchLocationDetails(String email);

    void storeLocationDetails(CustomerLocation location);

    void storeProfileDetails(ProfileData profileData);
}
