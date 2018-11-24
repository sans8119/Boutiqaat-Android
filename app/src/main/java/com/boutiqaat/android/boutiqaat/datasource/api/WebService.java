package com.boutiqaat.android.boutiqaat.datasource.api;

import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.ServerData;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofit uses this for network operations.
 */
public interface WebService {
    /**
     * This is the rest api of the intagram for downloading pictures from a account. Right now it downloads all pictures in the internet
     * which were uploaded to my instagram account
     *
     * @param accessToken
     * @return
     */
    @GET("users/self/media/recent")
    Single<ServerData> getUsersRecentMedia(@Query("access_token") String accessToken);
}
