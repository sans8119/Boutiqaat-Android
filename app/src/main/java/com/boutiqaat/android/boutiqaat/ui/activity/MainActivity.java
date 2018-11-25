package com.boutiqaat.android.boutiqaat.ui.activity;


import android.Manifest;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.boutiqaat.android.boutiqaat.R;
import com.boutiqaat.android.boutiqaat.databinding.ActivityMainBinding;
import com.boutiqaat.android.boutiqaat.ui.ActivityView;
import com.boutiqaat.android.boutiqaat.ui.fragment.CategoriesFragment;
import com.boutiqaat.android.boutiqaat.ui.fragment.LocationsFragment;
import com.boutiqaat.android.boutiqaat.ui.fragment.ProfilesFragment;
import com.boutiqaat.android.boutiqaat.ui.fragment.SettingsFragment;
import com.boutiqaat.android.boutiqaat.ui.utils.BoutiqaatViewPager;
import com.boutiqaat.android.boutiqaat.ui.utils.TabPaneAdapter;
import com.boutiqaat.android.boutiqaat.utils.Constants;
import com.boutiqaat.android.boutiqaat.utils.Utils;
import com.boutiqaat.android.boutiqaat.viewmodel.implementation.ProfileViewModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import timber.log.Timber;

/**
 * This class is the parent container which houses the tabs for showing catogories, Location updates, profile information and Settings.
 */
@RuntimePermissions
public class MainActivity extends DaggerAppCompatActivity implements AHBottomNavigation.OnTabSelectedListener, ActivityView {
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private ActivityMainBinding binding;
    private Toolbar toolbar;
    private ProfileViewModel viewModel;
    private BoutiqaatViewPager viewPager;
    private SharedPreferences prefs;
    private TabPaneAdapter pagerAdapter;
    private Utils utils;

    @Override
    protected void attachBaseContext(Context base) {
        int langPos = base.getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE).getInt(Constants.LANGUAGE, Constants.POSTION_ENG);
        String locale = new Utils().getLocale(langPos);
        super.attachBaseContext(new Utils().setLocale(
                locale,
                base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        prefs = getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel.class);
        setupViewPager();
        setupBottomNavBehaviors();
        setupBottomNavStyle();
        addBottomNavigationItems();
        utils = new Utils();
        if (prefs.getBoolean(Constants.REFRESH, false)) {
            binding.bottomNavigation.setCurrentItem(Constants.POSTION_SETTINGS);
            prefs.edit().putBoolean(Constants.REFRESH, false).commit();
        } else {
            binding.bottomNavigation.setCurrentItem(Constants.POSTION_CATOGORIES);
        }
        if (!prefs.contains(Constants.LOCATION_ON)) {
            prefs.edit().putBoolean(Constants.LOCATION_ON, true).commit();
        }
        isGooglePlayServicesAvailable();
        MainActivityPermissionsDispatcher.setUpPermissionsForLocationUpdatesWithPermissionCheck(this);

    }


    @Override
    public void onResume() {
        super.onResume();
        checkLocationSettings();
    }

    /**
     * All platform level permissions like read/write from memory, location fetch permissions, camera usage persmissions have to be
     * allowed by the user of the app; this method declares those permissions and throws a dialog to the user on the first time
     * usage of the app. If he denies any of the permissions then the respective features will not work.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void setUpPermissionsForLocationUpdates() {
    }

    @OnShowRationale({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            , Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void showRationaleForLocationPermissions(PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.permission__rationale)
                .setPositiveButton(R.string.button_allow, (dialog, button) -> request.proceed())
                .setNegativeButton(R.string.button_deny, (dialog, button) -> request.cancel())
                .show();
    }

    @OnNeverAskAgain({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void showNeverAskForForLocationPermissions() {
        showDlg(R.string.permission_call_neverask);
    }

    // Annotate a method which is invoked if the user doesn't grant the permissions
    @OnPermissionDenied({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showDeniedForLocationPermissions() {
        showDlg(R.string.permission_call_denied);
    }

    private void showDlg(int stringId) {
        new AlertDialog.Builder(this)
                .setMessage(stringId)
                .setPositiveButton(R.string.ok, (dialog, button) -> dialog.dismiss())
                .show();
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Timber.d("This device is not supported for location updates.");
                showDlg(R.string.device_not_supported);
            }
            return false;
        } else {
            setUpPermissionsForLocationUpdates();
        }
        return true;
    }

    private void setupBottomNavStyle() {
        binding.bottomNavigation.setDefaultBackgroundColor(Color.WHITE);
        binding.bottomNavigation.setAccentColor(fetchColor(R.color.bottomtab_0));
        binding.bottomNavigation.setInactiveColor(fetchColor(R.color.bottomtab_item_resting));

        // Colors of active tab and non-active tab.
        binding.bottomNavigation.setColoredModeColors(Color.WHITE,
                fetchColor(R.color.bottomtab_item_resting));

        //  Enables Reveal effect
        binding.bottomNavigation.setColored(true);

        //  Displays item Title always (for selected and non-selected items)
        binding.bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
    }

    private int fetchColor(@ColorRes int color) {
        return ContextCompat.getColor(this, color);
    }

    //Setting up bottom navigation tabs.
    public void setupBottomNavBehaviors() {
        binding.bottomNavigation.setTranslucentNavigationEnabled(true);
    }

    private void setupViewPager() {
        viewPager = binding.viewpager;
        viewPager.setPagingEnabled(false);
        pagerAdapter = new TabPaneAdapter(getSupportFragmentManager());

        pagerAdapter.addFragments(new CategoriesFragment());
        pagerAdapter.addFragments(new LocationsFragment());
        pagerAdapter.addFragments(new ProfilesFragment());
        pagerAdapter.addFragments(new SettingsFragment());

        viewPager.setAdapter(pagerAdapter);
    }


    public void addBottomNavigationItems() {
        AHBottomNavigationItem catogoriesItem =
                new AHBottomNavigationItem(getString(R.string.catogories),
                        android.R.drawable.ic_menu_today);
        binding.bottomNavigation.addItem(catogoriesItem);

        AHBottomNavigationItem locationsItem =
                new AHBottomNavigationItem(getString(R.string.locations),
                        android.R.drawable.ic_menu_mylocation);
        binding.bottomNavigation.addItem(locationsItem);

        AHBottomNavigationItem profilesItem =
                new AHBottomNavigationItem(getString(R.string.profiles),
                        R.drawable.badge_no_image);
        binding.bottomNavigation.addItem(profilesItem);

        AHBottomNavigationItem settingsItem =
                new AHBottomNavigationItem(getString(R.string.settings),
                        android.R.drawable.ic_menu_set_as);
        binding.bottomNavigation.addItem(settingsItem);

        binding.bottomNavigation.setOnTabSelectedListener(this);

        binding.bottomNavigation.setCurrentItem(Constants.POSTION_CATOGORIES);
    }

    @Override
    public boolean onTabSelected(int position, boolean wasSelected) {
        if (!wasSelected)
            viewPager.setCurrentItem(position);
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
        } catch (Exception ex) {
        }
    }

    //To turn on wifi and gps from settings.
    public void checkLocationSettings() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false, network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }
        if (!gps_enabled && !network_enabled) {
            if (!gps_enabled) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setMessage(getResources().getString(R.string.gps_network_not_enabled));
                dialog.setPositiveButton(getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                        paramDialogInterface.dismiss();
                    }
                });
                dialog.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        paramDialogInterface.dismiss();

                    }
                });
                dialog.show();
            }
        }

    }

}
