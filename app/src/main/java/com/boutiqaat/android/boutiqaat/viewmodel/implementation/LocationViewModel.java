package com.boutiqaat.android.boutiqaat.viewmodel.implementation;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Looper;

import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.CustomerLocation;
import com.boutiqaat.android.boutiqaat.repository.Repository;
import com.boutiqaat.android.boutiqaat.utils.Constants;
import com.boutiqaat.android.boutiqaat.utils.Utils;
import com.boutiqaat.android.boutiqaat.viewmodel.viewModelInterface.LocationVM;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

import java.util.LinkedHashMap;

import javax.inject.Inject;

import timber.log.Timber;

import static com.boutiqaat.android.boutiqaat.utils.Constants.FASTEST_INTERVAL;
import static com.boutiqaat.android.boutiqaat.utils.Constants.UPDATE_INTERVAL;

/**
 * Viewmodel for LocationFragment.
 */
public class LocationViewModel extends ViewModel implements LocationVM {
    private final Repository repo;
    public double lat = 0, lon = 0;
    public Location startPoint;
    LocationRequest mLocationRequest;
    private Application application;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback locationCallback;
    private SharedPreferences prefs;
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null || (!prefs.getBoolean(Constants.LOCATION_ON, true))) {
                return;
            }
            Timber.d("LOcVM:" + locationResult.getLocations().size());
            for (Location location : locationResult.getLocations()) {
                updateLocation(location);
            }
        }

        ;
    };

    @Inject
    public LocationViewModel(Repository userRepo, Application application) {
        this.repo = userRepo;
        this.application = application;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(application);
        mLocationRequest = LocationRequest.create();
        prefs = application.getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE);
        lat = Double.parseDouble(prefs.getString(Constants.LAT, "0"));
        lon = Double.parseDouble(prefs.getString(Constants.LON, "0"));
        startPoint = new Location("locationA");
        startPoint.setLatitude(lat);
        startPoint.setLongitude(lon);
    }

    public void setLocationCallback(LocationCallback locationCallback) {
        this.locationCallback = locationCallback;
    }

    public void startLocationUpdates() {
        startLocationUpdates(UPDATE_INTERVAL, FASTEST_INTERVAL);
    }

    /**
     * Observer is registered here for getting platforms Location information.
     *
     * @param interval
     * @param fastestInterval
     */
    public void startLocationUpdates(int interval, int fastestInterval) {
        Timber.d("Loc VM startLocationUpdates 1111" + " >" + prefs.getBoolean(Constants.LOCATION_ON, true));
        Timber.d("Loc VM startLocationUpdates 222 " + interval + " " + fastestInterval);
        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(interval);
        mLocationRequest.setFastestInterval(fastestInterval);
        Timber.d("Loc VM startLocationUpdates 333 ");
        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        //TO DO : do we need to setAlwaysShow(true) when in MainActivity we are checking for wifi and gps settings
        // builder.setAlwaysShow(true);
        LocationSettingsRequest locationSettingsRequest = builder.build();
        Timber.d("Loc VM startLocationUpdates 4444 ");
        SettingsClient settingsClient = LocationServices.getSettingsClient(application);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        locationCallback = (locationCallback == null) ? mLocationCallback : locationCallback;
        Timber.d("Loc VM startLocationUpdates 5555 ");
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback,
                    Looper.myLooper());
            Timber.d("Loc VM startLocationUpdates 666 ");
        } catch (SecurityException s) {
            Timber.e("Exception at startLocationUpdates:" + s.getMessage());
        }

    }

    /**
     * Unregistering the observer for getting platforms location information.
     */
    public void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    /**
     * If the current point is more than 500 meters in radius from earlier recoreded location of user then the the current point
     * information is stored in the database here.
     *
     * @param endPoint
     * @return
     */
    public boolean updateLocation(Location endPoint) {
        Timber.d("updateLocation:" + lat + ", " + lon + "  " + endPoint.getLatitude() + ", " + endPoint.getLongitude() + ", " + startPoint.distanceTo(endPoint));
        if (startPoint.distanceTo(endPoint) >= Constants.DIST_FOR_LOC_UPDATES) {
            String add = new Utils().getAddress(endPoint.getLatitude(), endPoint.getLongitude(), application);
            lat = endPoint.getLatitude();
            lon = endPoint.getLongitude();
            startPoint.setLatitude(lat);
            startPoint.setLongitude(lon);
            if (add.length() > 0) {
                CustomerLocation userLocation = new CustomerLocation();
                userLocation.locationString = add;
                userLocation.email = prefs.getString(Constants.LOGGED_IN_USER_EMAIL, Constants.ANON);
                repo.storeLocationDetails(userLocation);
            }
            return true;
        }
        return false;
    }

    public void storeLocationDetails(CustomerLocation userLocation) {
        repo.storeLocationDetails(userLocation);
    }

    public LiveData<LinkedHashMap<String, CustomerLocation>> getLocationsLiveData() {
        return repo.getLocationsLiveData();
    }

    public void getLocationDetails(String email) {
        repo.getLocationDetails(email);
    }

    /**
     * All Rxjava observers are unregistered here.
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        stopLocationUpdates();
        prefs.edit().putString(Constants.LAT, String.valueOf(lat)).putString(Constants.LON, String.valueOf(lon)).commit();
        repo.getCompositeDisposable().clear();
    }
}
