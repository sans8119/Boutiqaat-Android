package com.boutiqaat.android.boutiqaat.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import timber.log.Timber;

public class Utils {

    /**
     * Provides the address based on latitude, longitude of a place.
     *
     * @param lat
     * @param lng
     * @param context
     * @return
     */
    public String getAddress(double lat, double lng, Context context) {
        String add = "";
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            Timber.d("getAddress:" + context + ", " + geocoder);
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses.size() > 0) {
                Address obj = addresses.get(0);
                add = obj.getAddressLine(0);
                add = add + "\n" + obj.getCountryName();
                add = add + "\n" + obj.getAdminArea();
                add = add + "\n" + obj.getSubAdminArea();
                add = add + "\n" + obj.getLocality();
                add = add + "\n" + "Time: " + new SimpleDateFormat(Constants.DATE_FORMAT).format(new java.util.Date());
            }
            Timber.v("Address" + add);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException n) {
            n.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }

        return add;
    }

    public Context setLocale(String language, Context context) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        if (Build.VERSION.SDK_INT >= 17) {
            config.setLocale(locale);
            context = context.createConfigurationContext(config);
        } else {
            config.locale = locale;
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
        return context;
    }

    public String getLocale(int langPos) {
        String locale = "en";
        if (langPos == Constants.POSTION_ENG) {
            locale = "en";
        } else if (langPos == Constants.POSTION_ARABIC) {
            locale = "ar";
        }
        if (langPos == Constants.POSTION_FRENCH) {
            locale = "fr";
        }
        return locale;
    }

    public boolean checkConnection(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = Objects.requireNonNull(cm).getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }


}