package com.boutiqaat.android.boutiqaat.viewmodel.implementation;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.UsersRecentMediaModel;
import com.boutiqaat.android.boutiqaat.repository.Repository;
import com.boutiqaat.android.boutiqaat.viewmodel.viewModelInterface.CategoriesVM;

import java.util.LinkedHashMap;

import javax.inject.Inject;

/**
 * Viewmodel for CatogoriesFragment.
 */
public class CategoriesViewModel extends ViewModel implements CategoriesVM {
    private final Repository repo;

    @Inject
    public CategoriesViewModel(Repository userRepo, Application application) {
        this.repo = userRepo;
        Application application1 = application;
    }

    /**
     * All Rxjava observers are unregistered here.
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        repo.getCompositeDisposable().clear();
    }

    public LiveData<LinkedHashMap<String, UsersRecentMediaModel>> getNetworkOrDbLiveData() {
        return repo.getNetworkOrDbLiveData();
    }

    public void fetchImagesDataFromServer() {
        repo.fetchImagesDataFromServer();
    }

}


