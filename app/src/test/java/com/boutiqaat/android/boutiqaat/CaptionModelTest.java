package com.boutiqaat.android.boutiqaat;

import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.CaptionModel;
import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.CustomerLocation;
import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.ImageData;
import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.ImageModel;
import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.ProfileData;
import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.ServerData;
import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.UsersRecentMediaModel;

import junit.framework.Assert;

import org.junit.Test;

public class CaptionModelTest {

    @Test
    public void test() {
        CaptionModel model = new CaptionModel();
        Assert.assertNull("null", model.getText());
        CustomerLocation customerLocation = new CustomerLocation();
        customerLocation.toString();
        ImageData data2 = new ImageData();
        data2.toString();
        ProfileData profileData = new ProfileData();
        profileData.toString();
        ServerData serverData = new ServerData();
        serverData.data = new UsersRecentMediaModel[]{new UsersRecentMediaModel()};
        int length = serverData.data.length;
        UsersRecentMediaModel model1 = new UsersRecentMediaModel();
        model1.toString();
        ImageModel model2 = new ImageModel();
        model2.toString();


    }
}
