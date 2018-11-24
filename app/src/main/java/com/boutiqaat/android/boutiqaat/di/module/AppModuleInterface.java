package com.boutiqaat.android.boutiqaat.di.module;

import android.app.Application;

import com.boutiqaat.android.boutiqaat.datasource.api.WebService;
import com.boutiqaat.android.boutiqaat.datasource.db.ResultsDb;
import com.boutiqaat.android.boutiqaat.datasource.db.dao.ResultsDao;
import com.boutiqaat.android.boutiqaat.repository.Repository;
import com.google.gson.Gson;

import java.util.concurrent.Executor;

import retrofit2.Retrofit;

public interface AppModuleInterface {
    Executor provideExecutor();

    Gson provideGson();

    Retrofit provideRetrofit(Gson gson);

    WebService provideWebservice(Retrofit restAdapter);

    ResultsDb provideDatabase(Application application);

    ResultsDao provideUserDao(ResultsDb database);

    Repository provideDataRepository(WebService webservice, ResultsDao resultsDao, Executor executor, Application application);
}
