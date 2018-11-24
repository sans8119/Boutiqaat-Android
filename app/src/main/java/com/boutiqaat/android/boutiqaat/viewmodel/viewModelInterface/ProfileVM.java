package com.boutiqaat.android.boutiqaat.viewmodel.viewModelInterface;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.graphics.Bitmap;

import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.ProfileData;

import java.util.LinkedHashMap;

public interface ProfileVM extends ViewModelInterface {
    ProfileData getProfileDataOfLoggedInUser();

    void setProfileDataOfLoggedInUser(ProfileData profileDataOfLoggedInUser);

    ProfileData getProfileDataOfAnonimousUser();

    void setProfileDataOfAnonimousInUser(ProfileData profileDataOfAnonInUser);

    MutableLiveData<Bitmap> getImageResults();

    LiveData<LinkedHashMap<String, ProfileData>> getProfilesLiveData();

    void storeProfileDetails(ProfileData profileData);

    String currentSavedCameraPicName(String email);

    void getProfileDetails(String email);

    void storeCameraImageInSDCard(Bitmap bitmap, String email);

    //Bitmap getImageFileFromSDCard(String email);

}
