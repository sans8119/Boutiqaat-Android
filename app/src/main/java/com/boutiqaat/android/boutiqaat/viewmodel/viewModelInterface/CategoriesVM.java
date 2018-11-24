package com.boutiqaat.android.boutiqaat.viewmodel.viewModelInterface;

import android.arch.lifecycle.LiveData;

import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.UsersRecentMediaModel;

import java.util.LinkedHashMap;

public interface CategoriesVM extends ViewModelInterface {
    LiveData<LinkedHashMap<String, UsersRecentMediaModel>> getNetworkOrDbLiveData();

    void fetchImagesDataFromServer();
}
