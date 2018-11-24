package com.boutiqaat.android.boutiqaat.datasource.db.datamodel;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * This the database table structure where we store the users profile information. All users are classified into 2 types : Logged in
 * users and Anonmous users. Anonmous users data is not stored in database. It is only retained until the app is active in foreground or
 * background. This table maintains all the users who have signed up. During sign up only email, name, password and address information are
 * stored. The user can change all this information from Profile page. In Profile page he can upate/change information provided during
 * sign-up and also add more information like his gender, address.
 */
@Entity
public class ProfileData implements DataModel {
    @NonNull
    @PrimaryKey
    public String email;
    public String name;
    public String gender;
    public String profilePhotoUrl;
    public String password;
    public String location;

    public String phone;
    public String address;

    @Override
    public String toString() {
        return "ProfileData{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", profilePhotoUrl='" + profilePhotoUrl + '\'' +
                ", password='" + password + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
