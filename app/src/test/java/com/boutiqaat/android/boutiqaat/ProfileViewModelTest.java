package com.boutiqaat.android.boutiqaat;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.boutiqaat.android.boutiqaat.datasource.api.WebService;
import com.boutiqaat.android.boutiqaat.datasource.db.dao.ResultsDao;
import com.boutiqaat.android.boutiqaat.repository.DataRepository;
import com.boutiqaat.android.boutiqaat.utils.Constants;
import com.boutiqaat.android.boutiqaat.viewmodel.implementation.ProfileViewModel;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import io.reactivex.Scheduler;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.schedulers.ExecutorScheduler;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

public class ProfileViewModelTest {

    @Mock
    WebService webservice;
    @Mock
    ResultsDao resultsDao;
    @Mock
    Application application;
    @Mock
    Executor executor;

    DataRepository repository;
    ProfileViewModel pvm;
    SharedPreferences sharedPrefs;
    Context context;

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
        pvm = new ProfileViewModel(repository, application);
        this.sharedPrefs = Mockito.mock(SharedPreferences.class);
        context = Mockito.mock(Context.class);
        Mockito.when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPrefs);

    }

    @Test
    public void currentSavedCameraPicNameTest() {
        Mockito.when(sharedPrefs.getString(anyString(), anyString())).thenReturn("foobar");
        String name = pvm.currentSavedCameraPicName("sans@gmail.com");
        Assert.assertEquals("sans@gmail.com_" + Constants.PROFILE_PIC_FILE_NAME, name);
    }

    @Test
    public void storeCameraPhotoInSDCard() {
        Bitmap icon = Mockito.mock(Bitmap.class);
        String name = pvm.currentSavedCameraPicName("sa@gmail.com");
        File file = new File(name);
        pvm.storeCameraImageInSDCard(icon, "sa@gmail.com");
        name = pvm.currentSavedCameraPicName("sa@gmail.com");
        Assert.assertEquals(false, file.exists());
    }

    @Test
    public void getImageFileFromSDCard() {
        File outputFile = new File(".email_temp_cam_captured_img.jpg.jpg ");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            //bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bm = pvm.getImageFileFromSDCard("email");
        Assert.assertEquals(null, bm);

    }

}
