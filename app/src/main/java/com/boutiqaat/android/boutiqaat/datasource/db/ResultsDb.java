package com.boutiqaat.android.boutiqaat.datasource.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.boutiqaat.android.boutiqaat.datasource.db.dao.ResultsDao;
import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.CustomerLocation;
import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.ProfileData;
import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.UsersRecentMediaModel;

/**
 * Configuration file for Room to create tables.
 */
@Database(version = 1, exportSchema = false, entities = {UsersRecentMediaModel.class, CustomerLocation.class, ProfileData.class})
public abstract class ResultsDb extends RoomDatabase {
    public abstract ResultsDao getResultsDao();
}