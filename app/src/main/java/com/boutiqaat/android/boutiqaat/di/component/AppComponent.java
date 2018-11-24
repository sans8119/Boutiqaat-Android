package com.boutiqaat.android.boutiqaat.di.component;

import android.app.Application;

import com.boutiqaat.android.boutiqaat.AndroidApplication;
import com.boutiqaat.android.boutiqaat.di.module.ActivityBinderModule;
import com.boutiqaat.android.boutiqaat.di.module.AppModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * Daggers main class to store objects in its graph.Finds all objects from the modules specified here.
 */
@Singleton
@Component(modules = {
        AndroidSupportInjectionModule.class,
        ActivityBinderModule.class,
        AppModule.class,
})
public interface AppComponent extends AndroidInjector<DaggerApplication> {

    void inject(AndroidApplication app);

    @Override
    void inject(DaggerApplication instance);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }
}
