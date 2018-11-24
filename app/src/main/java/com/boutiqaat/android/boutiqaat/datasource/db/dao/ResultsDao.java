package com.boutiqaat.android.boutiqaat.datasource.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.CustomerLocation;
import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.ProfileData;
import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.UsersRecentMediaModel;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * DAO class for accessing and managing data in the database. Room is used for all database operations.
 */
@Dao
public interface ResultsDao {

    @Insert(onConflict = REPLACE)
    void insertImagesData(UsersRecentMediaModel usersRecentMediaModel);

/*    @Insert(onConflict = REPLACE)
    long insertCustomerData(CustomerData customerData); */

    @Insert(onConflict = REPLACE)
    long insertCustomerLocation(CustomerLocation customerLocation);

    @Insert(onConflict = REPLACE)
    long insertLocationData(CustomerLocation customerLocation);

    @Insert(onConflict = REPLACE)
    long insertProfileData(ProfileData profileData);

    // @Delete
    //long deleteProfileData(ProfileData profileData);

    @Query("SELECT * FROM table_customer_location where email=:email order by id DESC")
    CustomerLocation[] loadCustomerLocationData(String email);

    @Query("SELECT * FROM profiledata where email=:email")
    ProfileData[] loadProfileData(String email);

}
