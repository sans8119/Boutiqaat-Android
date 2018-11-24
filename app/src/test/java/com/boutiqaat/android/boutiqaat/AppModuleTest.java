package com.boutiqaat.android.boutiqaat;

import android.app.Application;

import com.boutiqaat.android.boutiqaat.datasource.api.WebService;
import com.boutiqaat.android.boutiqaat.datasource.db.dao.ResultsDao;
import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.CustomerLocation;
import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.ProfileData;
import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.UsersRecentMediaModel;
import com.boutiqaat.android.boutiqaat.di.module.AppModule;
import com.boutiqaat.android.boutiqaat.repository.DataRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.concurrent.Executor;

public class AppModuleTest {
    @Mock
    WebService webservice;
    @Mock
    ResultsDao resultsDao;
    @Mock
    Application application;
    @Mock
    Executor executor;
    DataRepository repository;

    @Before
    public void setUp() throws Exception {

        resultsDao = new ResultsDao() {
            @Override
            public void insertImagesData(UsersRecentMediaModel usersRecentMediaModel) {

            }

            @Override
            public long insertCustomerLocation(CustomerLocation customerLocation) {
                return 0;
            }

            @Override
            public long insertLocationData(CustomerLocation customerLocation) {
                return 0;
            }

            @Override
            public long insertProfileData(ProfileData profileData) {
                return 0;
            }

            @Override
            public CustomerLocation[] loadCustomerLocationData(String email) {
                return new CustomerLocation[0];
            }

            @Override
            public ProfileData[] loadProfileData(String email) {
                return new ProfileData[0];
            }
        };
        repository = new DataRepository(webservice, resultsDao, executor, application);
    }

    @Test
    public void test() {
        AppModule module = new AppModule();
        module.provideDataRepository(webservice, resultsDao, executor, application);
        module.provideExecutor();
        module.provideGson();

    }
}
