package com.boutiqaat.android.boutiqaat.di.module;


import com.boutiqaat.android.boutiqaat.ui.activity.MainActivity;
import com.boutiqaat.android.boutiqaat.ui.activity.SignInActivity;
import com.boutiqaat.android.boutiqaat.ui.activity.SignupActivity;
import com.boutiqaat.android.boutiqaat.ui.fragment.CategoriesFragment;
import com.boutiqaat.android.boutiqaat.ui.fragment.LocationsFragment;
import com.boutiqaat.android.boutiqaat.ui.fragment.ProfilesFragment;
import com.boutiqaat.android.boutiqaat.ui.fragment.SettingsFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * This class defines the Android components where the object injection is possible.
 */
@Module
public abstract class ActivityBinderModule {
    @ContributesAndroidInjector(modules = ViewModelModule.class)
    abstract MainActivity bindMainActivity();

    @ContributesAndroidInjector(modules = ViewModelModule.class)
    abstract SignInActivity bindSignInActivity();

    @ContributesAndroidInjector(modules = ViewModelModule.class)
    abstract SignupActivity bindSignUpctivity();

    @ContributesAndroidInjector(modules = {ViewModelModule.class})
    abstract CategoriesFragment bindCategoriesFragment();

    @ContributesAndroidInjector(modules = {ViewModelModule.class})
    abstract LocationsFragment bindLocationFragment();

    @ContributesAndroidInjector(modules = {ViewModelModule.class})
    abstract ProfilesFragment bindProfileFragment();

    @ContributesAndroidInjector(modules = {ViewModelModule.class})
    abstract SettingsFragment bindSettingsFragment();

}
