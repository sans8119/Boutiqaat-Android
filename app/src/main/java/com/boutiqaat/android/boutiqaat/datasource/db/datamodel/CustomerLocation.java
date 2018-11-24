package com.boutiqaat.android.boutiqaat.datasource.db.datamodel;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.boutiqaat.android.boutiqaat.utils.Constants;

/**
 * This class defines the database table structure for storing users location information. As the user moves 500 m from his current location
 * his new location information is stored for him. New information is stored only when logged in user moves 500 m from his current location.
 * The table maintains multiple users location information.
 */
@Entity(tableName = "table_customer_location")
public class CustomerLocation implements DataModel {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String email = Constants.ANON;
    public String locationString;

    @Override
    public String toString() {
        return "CustomerLocation{" +
                "email='" + email + '\'' +
                ", locationString='" + locationString + '\'' +
                '}';
    }
}
