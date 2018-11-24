package com.boutiqaat.android.boutiqaat.datasource.db.datamodel;

/**
 * This the root element of the json returned from Instagram rest api;
 */
public class ServerData implements DataModel {
    public UsersRecentMediaModel[] data;

    public String toString() {
        String str = "";
        for (UsersRecentMediaModel usersRecentMediaModel : data)
            str += usersRecentMediaModel.toString();
        return str;
    }
}
