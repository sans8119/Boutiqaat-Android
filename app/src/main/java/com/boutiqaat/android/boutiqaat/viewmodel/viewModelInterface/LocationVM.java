package com.boutiqaat.android.boutiqaat.viewmodel.viewModelInterface;

import android.arch.lifecycle.LiveData;
import android.location.Location;

import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.CustomerLocation;
import com.google.android.gms.location.LocationCallback;

import java.util.LinkedHashMap;

public interface LocationVM extends ViewModelInterface {
    void setLocationCallback(LocationCallback locationCallback);

    void startLocationUpdates();

    void startLocationUpdates(int interval, int fastestInterval);

    void stopLocationUpdates();

    boolean updateLocation(Location endPoint);

    void storeLocationDetails(CustomerLocation userLocation);

    LiveData<LinkedHashMap<String, CustomerLocation>> getLocationsLiveData();

    void getLocationDetails(String email);

}
