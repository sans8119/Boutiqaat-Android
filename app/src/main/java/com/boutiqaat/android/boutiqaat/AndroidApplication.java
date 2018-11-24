package com.boutiqaat.android.boutiqaat;

import android.content.Context;
import android.content.res.Configuration;

import com.boutiqaat.android.boutiqaat.di.component.AppComponent;
import com.boutiqaat.android.boutiqaat.di.component.DaggerAppComponent;
import com.boutiqaat.android.boutiqaat.utils.Constants;
import com.boutiqaat.android.boutiqaat.utils.Utils;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import timber.log.Timber;

public class AndroidApplication extends DaggerApplication {
    Utils utils;

    @Override
    public void onCreate() {
        super.onCreate();
        utils = new Utils();
        Timber.plant(new Timber.DebugTree());
    }

    @Override
    protected void attachBaseContext(Context base) {
        if (utils == null)
            utils = new Utils();
        String locale = utils.getLocale(base.getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE).getInt(Constants.LANGUAGE, Constants.POSTION_ENG));
        Timber.d("context:" + base + " " + locale);
        super.attachBaseContext(new Utils().setLocale(locale, base));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        String locale = utils.getLocale(getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE).getInt(Constants.LANGUAGE, Constants.POSTION_ENG));
        utils.setLocale(locale, this);
    }

    /**
     * Daggers gets the instance of application context from this method.
     *
     * @return
     */
    protected AndroidInjector<DaggerApplication> applicationInjector() {
        AppComponent appComponent = DaggerAppComponent.builder().application(this).build();
        appComponent.inject(this);
        return appComponent;
    }

}
