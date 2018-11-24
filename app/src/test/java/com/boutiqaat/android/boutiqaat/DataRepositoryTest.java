package com.boutiqaat.android.boutiqaat;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.boutiqaat.android.boutiqaat.datasource.api.WebService;
import com.boutiqaat.android.boutiqaat.datasource.db.dao.ResultsDao;
import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.CustomerLocation;
import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.ProfileData;
import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.ServerData;
import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.UsersRecentMediaModel;
import com.boutiqaat.android.boutiqaat.repository.DataRepository;
import com.boutiqaat.android.boutiqaat.utils.Utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import io.reactivex.Scheduler;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.schedulers.ExecutorScheduler;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.hamcrest.CoreMatchers.instanceOf;

public class DataRepositoryTest {

    @Mock
    WebService webservice;
    @Mock
    ResultsDao resultsDao;
    @Mock
    Application application;
    @Mock
    Executor executor;
    DataRepository repository;

    public DataRepositoryTest() {
    }

    @BeforeClass
    public static void setUpRxSchedulers() {
        Scheduler immediate = new Scheduler() {
            @Override
            public Disposable scheduleDirect(@NonNull Runnable run, long delay, @NonNull TimeUnit unit) {
                // this prevents StackOverflowErrors when scheduling with a delay
                return super.scheduleDirect(run, 0, unit);
            }

            @Override
            public Worker createWorker() {
                return new ExecutorScheduler.ExecutorWorker(Runnable::run);
            }
        };

        RxJavaPlugins.setInitIoSchedulerHandler(scheduler -> immediate);
        RxJavaPlugins.setInitComputationSchedulerHandler(scheduler -> immediate);
        RxJavaPlugins.setInitNewThreadSchedulerHandler(scheduler -> immediate);
        RxJavaPlugins.setInitSingleSchedulerHandler(scheduler -> immediate);
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> immediate);
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    }

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
    public void getNetworkOrDbLiveDataTest() {
        //WebService webservice, ResultsDao resultsDao, Executor executor, Application application
        repository.getNetworkOrDbLiveData();
        Assert.assertThat(repository.getNetworkOrDbLiveData(), instanceOf(LiveData.class));
    }

    @Test
    public void getLocationsLiveDataTest() {
        repository.getLocationsLiveData();
        Assert.assertThat(repository.getLocationsLiveData(), instanceOf(LiveData.class));
    }

    @Test
    public void getProfilesLiveDataTest() {
        Assert.assertThat(repository.getLocationsLiveData(), instanceOf(LiveData.class));
    }

    @Test
    public void getProfileDetailsLogic() {
        //Mockito.when(repository.setProfileData()).thenReturn(new ProfileData[]{new ProfileData()});
        repository.getProfileDetailsLogic("san@gmail.com");
    }

    @Test
    public void handleServerDataTest() {
        ServerData sd = new ServerData();
        sd.data = new UsersRecentMediaModel[1];
        sd.data[0] = new UsersRecentMediaModel();
        repository.handleServerData(sd);
    }

    @Test
    public void fetchLocationDetails() {
        repository.fetchLocationDetails("san");
    }

    @Test
    public void test() {
        Utils utils = new Utils();
        utils.getAddress(41.9966, -73.8862, application);
    }


}

