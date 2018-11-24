package com.boutiqaat.android.boutiqaat.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.graphics.Bitmap;
import android.util.Log;

import com.boutiqaat.android.boutiqaat.datasource.api.WebService;
import com.boutiqaat.android.boutiqaat.datasource.db.dao.ResultsDao;
import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.CustomerLocation;
import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.ProfileData;
import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.ServerData;
import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.UsersRecentMediaModel;
import com.boutiqaat.android.boutiqaat.utils.Constants;
import com.boutiqaat.android.boutiqaat.utils.GlideApp;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.LinkedHashMap;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Main purpose is to handle data from network and database.
 */
@Singleton
public class DataRepository implements Repository {

    private final WebService webservice;
    private final Executor executor;
    private final ResultsDao resultsDao;
    private final Application application;
    private final CompositeDisposable compositeDisposable;
    private final MutableLiveData<LinkedHashMap<String, UsersRecentMediaModel>> resultsFromNetworkOrDb;
    private final MutableLiveData<LinkedHashMap<String, CustomerLocation>> customerLocationLiveData;
    private final MutableLiveData<LinkedHashMap<String, ProfileData>> profileLiveData;
    int count = 0;
    private LinkedHashMap<String, UsersRecentMediaModel> recentMediaMap;

    @Inject
    public DataRepository(WebService webservice, ResultsDao resultsDao, Executor executor, Application application) {
        this.webservice = webservice;
        this.executor = executor;
        this.resultsDao = resultsDao;
        this.application = application;
        resultsFromNetworkOrDb = new MutableLiveData<>();
        customerLocationLiveData = new MutableLiveData<>();
        profileLiveData = new MutableLiveData<>();
        compositeDisposable = new CompositeDisposable();
        recentMediaMap = new LinkedHashMap<String, UsersRecentMediaModel>();
    }

    public LiveData getNetworkOrDbLiveData() {
        return resultsFromNetworkOrDb;
    }

    public LiveData getLocationsLiveData() {
        return customerLocationLiveData;
    }

    public LiveData getProfilesLiveData() {
        return profileLiveData;
    }

    private void setResultsData(LinkedHashMap<String, UsersRecentMediaModel> resultsMap) {
        compositeDisposable.add(Single.just(resultsMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resultMap -> {
                    resultsFromNetworkOrDb.setValue(resultMap);
                    Timber.d("Setting Live data:" + resultsFromNetworkOrDb);
                }));
    }

    public void setLocationData(LinkedHashMap<String, CustomerLocation> customers) {
        compositeDisposable.add(Single.just(customers)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resultMap -> {
                    customerLocationLiveData.setValue(resultMap);
                    Timber.d("Setting Location Live data:" + customerLocationLiveData);
                }));
    }

    public void setProfileData(LinkedHashMap<String, ProfileData> profileData) {
        compositeDisposable.add(Single.just(profileData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resultMap -> {
                    profileLiveData.setValue(resultMap);
                    Timber.d("Setting Location Live data:" + customerLocationLiveData);
                }));
    }

    public void getProfileDetails(String email) {
        executor.execute(() -> {
            getProfileDetailsLogic(email);
        });
    }

    public void getProfileDetailsLogic(String email) {
        LinkedHashMap<String, ProfileData> resultsMap = new LinkedHashMap<String, ProfileData>();
        ProfileData[] profileDatas = resultsDao.loadProfileData(email);
        Timber.d("----getProfileDetails--->" + profileDatas);
        for (ProfileData profileData : profileDatas) {
            resultsMap.put(profileData.email, profileData);
        }
        Timber.d("getLocationDetails>>>" + resultsMap);
        setProfileData(resultsMap);
    }

    public void fetchImagesDataFromServer() {
        executor.execute(() -> {
            webservice.getUsersRecentMedia(Constants.ACCESS_TOKEN)
                    .subscribe(serverData -> {
                        handleServerData(serverData);
                    }, Throwable::printStackTrace);
        });

    }

    public void handleServerData(ServerData serverData) {
        Timber.i("Number of records fetched from server:" + serverData.data.length);
        count = 0;
        //recentMediaMap.clear();
        compositeDisposable.add(Flowable.fromArray(serverData.data)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(usersRecentMediaModel -> {
                    recentMediaMap.put(usersRecentMediaModel.createdTime, usersRecentMediaModel);
                    Single.fromCallable(() -> {
                        return getImageData(usersRecentMediaModel);
                    })
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .observeOn(Schedulers.io()).subscribe();
                    Timber.d("Caching server data in a map;current map size:" + recentMediaMap.size());
                    if (recentMediaMap.size() == serverData.data.length) {
                        setResultsData(recentMediaMap);
                    }

                }));
    }

    public CompositeDisposable getCompositeDisposable() {
        return compositeDisposable;
    }

    private boolean getImageData(UsersRecentMediaModel usersRecentMediaModel) {
        Log.d("trace", "repo:" + usersRecentMediaModel);
        GlideApp.with(application)
                .asBitmap().load(usersRecentMediaModel.images.thumbnail.url).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                Timber.i("Image downloading succeded: " + (++count) + " " + resource.getByteCount() + "  " + resource);
                usersRecentMediaModel.setThumbNailImg(resource);
            }
        });
        return true;
    }

    public void getLocationDetails(String email) {// Move this to asyn and get data in ui through LiveData
        executor.execute(() -> {
            fetchLocationDetails(email);
        });
    }

    public void fetchLocationDetails(String email) {
        LinkedHashMap<String, CustomerLocation> resultsMap = new LinkedHashMap<String, CustomerLocation>();
        CustomerLocation[] customerLocations = resultsDao.loadCustomerLocationData(email);

        for (CustomerLocation location : customerLocations) {
            resultsMap.put(location.locationString, location);
        }
        Timber.d("getLocationDetails>>>" + resultsMap);
        setLocationData(resultsMap);
    }

    public void storeLocationDetails(CustomerLocation location) {
        executor.execute(() -> {
            resultsDao.insertLocationData(location);
        });
    }

    public void storeProfileDetails(ProfileData profileData) {
        executor.execute(() -> {
            resultsDao.insertProfileData(profileData);
        });
    }

    public void deleteProfileData(ProfileData profileData) {
        executor.execute(() -> {
            // resultsDao.deleteProfileData(profileData);
        });
    }

}
