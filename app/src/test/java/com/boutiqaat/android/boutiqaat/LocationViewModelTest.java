package com.boutiqaat.android.boutiqaat;

import android.app.Application;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.annotation.NonNull;

import com.boutiqaat.android.boutiqaat.datasource.api.WebService;
import com.boutiqaat.android.boutiqaat.datasource.db.dao.ResultsDao;
import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.CustomerLocation;
import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.ProfileData;
import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.UsersRecentMediaModel;
import com.boutiqaat.android.boutiqaat.repository.DataRepository;
import com.boutiqaat.android.boutiqaat.viewmodel.implementation.LocationViewModel;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import io.reactivex.Scheduler;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.schedulers.ExecutorScheduler;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.anyString;

public class LocationViewModelTest {
    LocationViewModel lmv;
    @Mock
    WebService webservice;
    @Mock
    ResultsDao resultsDao;
    @Mock
    Application application;
    @Mock
    Executor executor;

    SharedPreferences sharedPrefs;
    @Mock
    Location location;

    public LocationViewModelTest() {
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
        this.sharedPrefs = Mockito.mock(SharedPreferences.class);
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
        Mockito.when(sharedPrefs.getString(anyString(), anyString())).thenReturn("foobar");
        DataRepository repository = new DataRepository(webservice, resultsDao, executor, application);
        // lmv=new LocationViewModel(repository,application);
    }
    // context = Mockito.mock(Context.class);

    @Test
    public void updateLocationTest() {
        // lmv.updateLocation(location);
    }
}
