package com.boutiqaat.android.boutiqaat.viewmodel.implementation;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.ProfileData;
import com.boutiqaat.android.boutiqaat.repository.Repository;
import com.boutiqaat.android.boutiqaat.utils.Constants;
import com.boutiqaat.android.boutiqaat.utils.Utils;
import com.boutiqaat.android.boutiqaat.viewmodel.viewModelInterface.ProfileVM;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Viewmodel for ProfilesFragment.
 */
public class ProfileViewModel extends ViewModel implements ProfileVM {
    private final Repository repo;
    private final MutableLiveData<Bitmap> imageResults;
    public Uri galleryPicUrl;//To do: may not be needed
    private Application application;
    ;
    private SharedPreferences prefs;
    private File image;
    private Utils utils;
    private Bitmap bitmap;
    private ProfileData profileDataOfLoggedInUser;
    private ProfileData profileDataOfAnonimousUser;

    @Inject
    public ProfileViewModel(Repository userRepo, Application application) {
        this.repo = userRepo;
        this.application = application;
        imageResults = new MutableLiveData<>();
        profileDataOfAnonimousUser = new ProfileData();
        utils = new Utils();
    }

    /**
     * All Rxjava observers are unregistered here.
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        repo.getCompositeDisposable().clear();
    }

    public ProfileData getProfileDataOfLoggedInUser() {
        return profileDataOfLoggedInUser;
    }

    public void setProfileDataOfLoggedInUser(ProfileData profileDataOfLoggedInUser) {
        this.profileDataOfLoggedInUser = profileDataOfLoggedInUser;
    }

    public ProfileData getProfileDataOfAnonimousUser() {
        return profileDataOfAnonimousUser;
    }

    public void setProfileDataOfAnonimousInUser(ProfileData profileDataOfAnonInUser) {
        this.profileDataOfAnonimousUser = profileDataOfAnonInUser;
    }

    public MutableLiveData<Bitmap> getImageResults() {
        return imageResults;
    }

    public LiveData<LinkedHashMap<String, ProfileData>> getProfilesLiveData() {
        return repo.getProfilesLiveData();
    }

    public void storeProfileDetails(ProfileData profileData) {
        repo.storeProfileDetails(profileData);
    }

    /**
     * Image data obtained from Camera is stored with a custom name. The starts with with his email
     *
     * @param email
     * @return
     */
    public String currentSavedCameraPicName(String email) {
        return email + "_" + Constants.PROFILE_PIC_FILE_NAME;
    }

    public void getProfileDetails(String email) {
        repo.getProfileDetails(email);
    }

    /**
     * To store the Camera image in sd card.
     *
     * @param bitmap
     * @param email
     */
    public void storeCameraImageInSDCard(Bitmap bitmap, String email) {
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File (sdCard.getAbsolutePath() + Constants.PROFILE_PIC_DIR);
        if(!directory.exists()) directory.mkdir();
        directory.setExecutable(true);
        File outputFile = new File(directory, currentSavedCameraPicName(email));
        if (!outputFile.exists())
            outputFile.delete();
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * To retrieve camera image for a logged in user from sd card.
     *
     * @param email
     * @return
     */
    public Bitmap getImageFileFromSDCard(String email,Context context) {
        Bitmap bitmap = null;
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File (sdCard.getAbsolutePath() + Constants.PROFILE_PIC_DIR);
        File imageFile = new File(directory, currentSavedCameraPicName(email));
        //File imageFile = new File(Environment.getExternalStorageDirectory() + currentSavedCameraPicName(email));
        try {Timber.d("---getImageFileFromSDCard >000"+bitmap);
            FileInputStream fis = new FileInputStream(imageFile);
            Toast.makeText(context, "---getImageFileFromSDCard >111"+bitmap,Toast.LENGTH_LONG).show();
            bitmap = BitmapFactory.decodeStream(fis);
            Toast.makeText(context, "---getImageFileFromSDCard 111>"+bitmap,Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Toast.makeText(context, "---getImageFileFromSDCard >333"+bitmap,Toast.LENGTH_LONG).show();
        return bitmap;
    }

}
