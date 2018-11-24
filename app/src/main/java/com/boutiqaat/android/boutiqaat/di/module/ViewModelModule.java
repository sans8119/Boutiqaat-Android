package com.boutiqaat.android.boutiqaat.di.module;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.boutiqaat.android.boutiqaat.di.ViewModelKey;
import com.boutiqaat.android.boutiqaat.viewmodel.implementation.CategoriesViewModel;
import com.boutiqaat.android.boutiqaat.viewmodel.implementation.FactoryViewModel;
import com.boutiqaat.android.boutiqaat.viewmodel.implementation.LocationViewModel;
import com.boutiqaat.android.boutiqaat.viewmodel.implementation.ProfileViewModel;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(CategoriesViewModel.class)
    abstract ViewModel bindMainActivityViewModel(CategoriesViewModel mainViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(FactoryViewModel factory);

    @Binds
    @IntoMap
    @ViewModelKey(LocationViewModel.class)
    abstract ViewModel bindLocationViewModel(LocationViewModel locationViewModel);

    @Binds
    @IntoMap
    @Singleton
    @ViewModelKey(ProfileViewModel.class)
    abstract ViewModel bindProfileiewModel(ProfileViewModel profileViewModel);

}
