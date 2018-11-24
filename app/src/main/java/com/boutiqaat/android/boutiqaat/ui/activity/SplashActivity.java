package com.boutiqaat.android.boutiqaat.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.boutiqaat.android.boutiqaat.R;
import com.boutiqaat.android.boutiqaat.ui.ActivityView;
import com.boutiqaat.android.boutiqaat.utils.Constants;
import com.boutiqaat.android.boutiqaat.utils.Utils;

import timber.log.Timber;

/**
 * This is the user interface class for splash. Splash screen is shown for 2 seconds.
 */
public class SplashActivity extends AppCompatActivity implements ActivityView {
    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    protected void attachBaseContext(Context base) {
        int langPos = base.getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE).getInt(Constants.LANGUAGE, Constants.POSTION_ENG);
        String locale = new Utils().getLocale(langPos);
        super.attachBaseContext(new Utils().setLocale(
                locale,
                base));
        Timber.d("attachBaseContext");
    }

}