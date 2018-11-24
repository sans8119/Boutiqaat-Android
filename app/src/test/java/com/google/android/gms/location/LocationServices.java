package com.google.android.gms.location;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;


public class LocationServices {

    public static com.google.android.gms.location.FusedLocationProviderClient getFusedLocationProviderClient(@NonNull Activity var0) {
        return new FusedLocationProviderClient(var0);
    }

    public static com.google.android.gms.location.FusedLocationProviderClient getFusedLocationProviderClient(@NonNull Context var0) {
        return new FusedLocationProviderClient(var0);
    }
}
