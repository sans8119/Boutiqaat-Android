package com.boutiqaat.android.boutiqaat.di.module;


import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;

import com.boutiqaat.android.boutiqaat.datasource.api.WebService;
import com.boutiqaat.android.boutiqaat.datasource.db.ResultsDb;
import com.boutiqaat.android.boutiqaat.datasource.db.dao.ResultsDao;
import com.boutiqaat.android.boutiqaat.repository.DataRepository;
import com.boutiqaat.android.boutiqaat.repository.Repository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This class is used by Dagger to find out the way to instantiate the objects that it will store in its graph.
 */
@Module(includes = {
        AppModule.Declarations.class,
        ActivityBinderModule.class,
        ViewModelModule.class}
)
public final class AppModule {

    private static String BASE_URL = "https://api.instagram.com/v1/";

    /**
     * Executor for threaded operations.
     *
     * @return
     */
    @Provides
    public Executor provideExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    /**
     * Gson for parsing json.
     *
     * @return
     */
    @Provides
    public Gson provideGson() {
        GsonBuilder builder = new GsonBuilder();
        return builder.create();
    }

    /**
     * Retrofit instantiation for network operations.
     *
     * @param gson
     * @return
     */
    @Provides
    public Retrofit provideRetrofit(Gson gson) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();
                Request request = original.newBuilder()
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .header("Accept", "application/json")
                        .method(original.method(), original.body())
                        .build();
                return chain.proceed(request);
            }
        });
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.interceptors().add(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();
        return retrofit;
    }

    @Provides
    @Singleton
    public WebService provideWebservice(Retrofit restAdapter) {
        return restAdapter.create(WebService.class);
    }

    /**
     * Room db instantiation classes.
     *
     * @param application
     * @return
     */
    @Provides
    @Singleton
    public ResultsDb provideDatabase(Application application) {
        return Room.databaseBuilder(application,
                ResultsDb.class, "resultsDB.db")
                .build();
    }

    /**
     * way to create Data access object layer class for storing in object graph of Dagger.
     *
     * @param database
     * @return
     */
    @Provides
    @Singleton
    public ResultsDao provideUserDao(ResultsDb database) {
        return database.getResultsDao();
    }

    /**
     * DataRepository instatiation procedure for Dagger
     *
     * @param webservice
     * @param resultsDao
     * @param executor
     * @param application
     * @return
     */
    @Provides
    @Singleton
    public Repository provideDataRepository(WebService webservice, ResultsDao resultsDao, Executor executor, Application application) {
        return new DataRepository(webservice, resultsDao, executor, application);
    }

    @Module
    public interface Declarations {
        @Binds
        @Singleton
        abstract Context provideContext(Application application);
    }

}