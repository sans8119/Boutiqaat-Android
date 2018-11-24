package com.boutiqaat.android.boutiqaat.ui.fragment;

import android.Manifest;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.boutiqaat.android.boutiqaat.R;
import com.boutiqaat.android.boutiqaat.databinding.ProfileScreenBinding;
import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.ProfileData;
import com.boutiqaat.android.boutiqaat.ui.FragmentView;
import com.boutiqaat.android.boutiqaat.utils.Constants;
import com.boutiqaat.android.boutiqaat.utils.GlideApp;
import com.boutiqaat.android.boutiqaat.utils.Utils;
import com.boutiqaat.android.boutiqaat.viewmodel.implementation.ProfileViewModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.bumptech.glide.signature.ObjectKey;

import java.io.File;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import timber.log.Timber;

/**
 * This is the user interface class for showing the users profile information. His gender, name ,email , address and photo
 * are shown here. User can any time update his sign-up information here.
 * Address of the user is not the same as his location information.
 * Only if the user has logged in will his updates in this page be saved permanently when he clicks 'Update' button. If the user
 * has not logged in(Anonymous user) then his information updates will be saved on clicking Update button until the app is active
 * in the foreground or background.
 */
public class ProfilesFragment extends DaggerFragment implements View.OnClickListener,FragmentView {
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private ProfileScreenBinding binding;
    private ProfileViewModel viewModel;
    private Utils utils;
    private SharedPreferences prefs;
    private boolean flag;
    private boolean onActivityResult;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        init(inflater, container);

        return binding.getRoot();
    }

    public void init(LayoutInflater inflater, ViewGroup container) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.profile_screen, container, false);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel.class);
        prefs = getActivity().getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, getActivity().getResources().getStringArray(R.array.gender_arrays));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.genderSpinner.setAdapter(dataAdapter);
        binding.genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                prefs.edit().putInt(Constants.GENDER, position).commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        initObservers();
        Timber.d("viewModel:" + viewModel + ", " + viewModelFactory);

        binding.userProfilePhoto.setOnClickListener(this);
        binding.saveBtn.setOnClickListener(this);
        utils = new Utils();
        GlideApp.with((Fragment)this).clear( binding.userProfilePhoto);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(onActivityResult) {
            onActivityResult=false;
            return;
        }
        Toast.makeText(getActivity(), "onResume" , Toast.LENGTH_LONG).show();
        boolean bool = (prefs.getString(Constants.LOGGED_IN_USER_EMAIL, Constants.ANON)).equals(Constants.ANON);
        if (bool) {
            viewModel.setProfileDataOfLoggedInUser(null);
        }
        ProfileData profileData = viewModel.getProfileDataOfLoggedInUser();
        Timber.d("-------onResume--------" + profileData);
        if (profileData != null) {
            setDataInUi(profileData);
        } else {
            flag = true;
            if (!prefs.getString(Constants.LOGGED_IN_USER_EMAIL, Constants.ANON).equals(Constants.ANON)) {
                viewModel.getProfileDetails(prefs.getString(Constants.LOGGED_IN_USER_EMAIL, Constants.ANON));
            } else {
                setDataInUi(viewModel.getProfileDataOfAnonimousUser());
            }
        }
    }

    /**
     * If the user information is saved then his information will be present in the ProfiData object which is sent as the argument.
     * From the data in the object ui elements like name, email , phone, address are set.
     * Location parameter has to be differentiated from address. Location is the users current and previous positons.
     * Address coulc be the users Home or Office addresses.
     *
     * @param profileData
     */
    private void setDataInUi(ProfileData profileData) {
        if (profileData != null) {
            binding.inputName.setText(profileData.name);
            binding.inputEmail.setText(profileData.email);
            binding.inputPhone.setText(profileData.phone);
            binding.inputLocation.setText(profileData.address);
            if (profileData.gender != null)
                binding.genderSpinner.setSelection(Integer.parseInt(profileData.gender));
            else
                binding.genderSpinner.setSelection(prefs.getInt(Constants.GENDER, 0));
            setProfileImage(profileData);
        }
    }

    /**
     * Profile image captured from camera or gallery is set here.
     *
     * @param profileData
     */
    private void setProfileImage(ProfileData profileData) {
        String url = profileData.profilePhotoUrl;
        String email = prefs
                .getString(Constants.LOGGED_IN_USER_EMAIL, Constants.ANON);
        if (url == null) return;
        Toast.makeText(getActivity(), "setProfileImage 000:url>" + url, Toast.LENGTH_LONG).show();
        if (url.length() > 0) {
            if (url.startsWith("file")) {
                GlideApp.with((Fragment)this)
                        .load(new File(Uri.parse(url).getPath()))
                        .signature(new MediaStoreSignature("",System.currentTimeMillis(),0))
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                        .apply(RequestOptions.circleCropTransform()).into(binding.userProfilePhoto);
                Toast.makeText(getActivity(), "setProfileImage 111:url>" + Uri.parse(url).getPath(), Toast.LENGTH_LONG).show();

            } else {
                GlideApp.with((Fragment)this).load(url)
                        .signature(new MediaStoreSignature("",System.currentTimeMillis(),0))
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                        .apply(RequestOptions.circleCropTransform()).into(binding.userProfilePhoto);
                Toast.makeText(getActivity(), "setProfileImage 222:url>" + Uri.parse(url), Toast.LENGTH_LONG).show();

            }
        } else if (viewModel.getImageFileFromSDCard(email,getActivity()) != null) {
            GlideApp.with(this).load(viewModel.getImageFileFromSDCard(email,getActivity()))
                    .signature(new ObjectKey(System.currentTimeMillis()))
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .apply(RequestOptions.circleCropTransform()).into(binding.userProfilePhoto);
            Toast.makeText(getActivity(), "setProfileImage 333" + viewModel.getImageFileFromSDCard(email,getActivity()).getByteCount(), Toast.LENGTH_LONG).show();
            //Toast.makeText(getActivity(), "***--***" + viewModel.getImageFileFromSDCard(email).getByteCount(), Toast.LENGTH_LONG).show();
        } else {
            binding.userProfilePhoto.setImageResource(R.drawable.badge_no_image);
            Toast.makeText(getActivity(), "setProfileImage 444", Toast.LENGTH_LONG).show();
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        }
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    /**
     * This is the observer for Profile data obtained from database and published by DataRepository.
     */
    public void initObservers() {
        viewModel.getProfilesLiveData().observe(this, map -> {
            if (!flag) return;
            Timber.d("-----initObservers----->" + map);
            if (map.keySet().toArray().length > 0) {
                ProfileData profileData = map.get(map.keySet().toArray()[0]);
                viewModel.setProfileDataOfLoggedInUser(profileData);
                setDataInUi(profileData);
            } else {
                ProfileData profileData = new ProfileData();
                profileData.name = "";
                profileData.email = "";
                profileData.gender = "1";
                profileData.profilePhotoUrl = "";
                setDataInUi(profileData);
            }
        });
    }

    /**
     * Dialog to ask user if he would like to get the image from Gallery or Camera.
     */
    public void selectImage() {
        CharSequence[] items = new CharSequence[]{getString(R.string.choose_from_camera), getString(R.string.choose_from_gallery)};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.choose_pic_from));
        builder.setItems(items,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        setSelectedImage("" + items[item]);
                    }
                });
        builder.show();
    }

    private void setSelectedImage(String choosenItem) {
        if (choosenItem.equals(getString(R.string.choose_from_camera))) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED)
                cameraIntent();
            else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 100);
            }
        } else if (choosenItem.equals(getString(R.string.choose_from_gallery))) {
            galleryIntent();
        }
    }

    /**
     * Lauching Camera
     */
    private void cameraIntent() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra("android.intent.extras.CAMERA_FACING", android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT)
                    .putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
                    .putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
            Timber.d("-----cameraIntent---->");
            startActivityForResult(intent, Constants.REQUEST_IMAGE_CAPTURE);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Launching Gallery
     */
    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_file)), Constants.SELECT_IMAGE);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == binding.userProfilePhoto.getId()) {
            selectImage();
        } else if (view.getId() == binding.saveBtn.getId()) {
            ProfileData profileData = viewModel.getProfileDataOfLoggedInUser();
            if (profileData != null) {
                String gender = String.valueOf(prefs.getInt(Constants.GENDER, 0));
                String email = binding.inputEmail.getText().toString();
                String name = binding.inputName.getText().toString();
                String profilePhotoUrl = viewModel.galleryPicUrl == null ? "" : getRealPathFromURI(viewModel.galleryPicUrl);
               // Toast.makeText(getActivity(), "Saving image data " + viewModel.galleryPicUrl.toString(), Toast.LENGTH_LONG).show();

                profileData.name = name.length() > 0 ? name : profileData.name;
                profileData.email = email.length() > 0 ? email : profileData.email;
                profileData.profilePhotoUrl = profilePhotoUrl;
                profileData.gender = gender;
                profileData.phone = binding.inputPhone.getText().toString().trim();
                profileData.address = binding.inputLocation.getText().toString().trim();
                viewModel.setProfileDataOfLoggedInUser(profileData);
                viewModel.storeProfileDetails(profileData);
                Toast.makeText(getActivity(), getString(R.string.data_saved), Toast.LENGTH_LONG).show();
            } else {
                String email = binding.inputEmail.getText().toString().trim();
                if (email.equals(Constants.ANON) || email.length() == 0) {
                    temporarilySaveDataOfAnonUser(email);
                } else {
                    new AlertDialog.Builder(getActivity())
                            .setMessage(getString(R.string.login_to_save_data_perm))
                            .setPositiveButton(R.string.ok, (dialog, button) -> {
                                temporarilySaveDataOfAnonUser("");
                                dialog.dismiss();
                            })
                            .show();
                }
            }

        }
    }

    /**
     * Anonymous user information is cached in memory and not saved in database. Only when the user logs in and saves his information
     * in Profiles page will his information be saved in database.
     *
     * @param email
     */
    private void temporarilySaveDataOfAnonUser(String email) {
        String name = binding.inputName.getText().toString();
        ProfileData profileData1 = new ProfileData();
        //ProfileData profileData1 = viewModel.getProfileDataOfAnonimousUser();
        profileData1.profilePhotoUrl = viewModel.galleryPicUrl == null ? "" : viewModel.galleryPicUrl.toString();
        profileData1.gender = String.valueOf(prefs.getInt(Constants.GENDER, 0));
        profileData1.email = email;
        profileData1.name = name;
        profileData1.phone = binding.inputPhone.getText().toString().trim();
        profileData1.location = binding.inputLocation.getText().toString().trim();

        viewModel.setProfileDataOfAnonimousInUser(profileData1);
        Toast.makeText(getActivity(), getString(R.string.login_to_save_data_perm) + profileData1.profilePhotoUrl, Toast.LENGTH_LONG).show();
    }


    /**
     * Used for handling Image data selected by user in Camera or Gallery.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Toast.makeText(getActivity(), "size 0:" + requestCode, Toast.LENGTH_LONG).show();
        // if(resultCode  == RESULT_OK) {
        Timber.d("----onActivityResult----" + requestCode + ", " + resultCode);
        Toast.makeText(getActivity(), "onActivityResult" + data.getData(), Toast.LENGTH_LONG).show();
        //GlideApp.get(getActivity()).clearMemory();
        if (requestCode == Constants.REQUEST_IMAGE_CAPTURE) {
            if (data != null) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                Toast.makeText(getActivity(), "size  1:" + bitmap.getByteCount(), Toast.LENGTH_LONG).show();
                if (bitmap != null) {
                    GlideApp.with((Fragment)this).load(bitmap)
                            .signature(new ObjectKey(System.currentTimeMillis()))
                            .apply(RequestOptions.circleCropTransform())
                            .apply(RequestOptions.skipMemoryCacheOf(true))
                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                            .into(binding.userProfilePhoto);
                    //binding.userProfilePhoto.setImageBitmap(bitmap);
                    Toast.makeText(getActivity(), "size  1 A:" + bitmap.getByteCount(), Toast.LENGTH_LONG).show();
                    String email = prefs
                            .getString(Constants.LOGGED_IN_USER_EMAIL, Constants.ANON);
                    viewModel.storeCameraImageInSDCard(bitmap, email);
                    viewModel.galleryPicUrl = null;
                }
            }
        } else if (requestCode == Constants.SELECT_IMAGE) {
            Toast.makeText(getActivity(), "size 4:" + data.getData(), Toast.LENGTH_LONG).show();
            Bitmap bm = null;
            if (data != null) {
                Toast.makeText(getActivity(), "size 5:" + data.getData().getScheme(), Toast.LENGTH_LONG).show();
                File file = new File(data.getData().getPath());
                Toast.makeText(getActivity(), "size 5 a:" + data.getData().getPath() + ", " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                if (data.getData().getScheme().equals("file")) {
                    GlideApp.with((Fragment)this)
                            .load(data.getData().getPath())
                            .signature(new MediaStoreSignature("",System.currentTimeMillis(),0))
                            .apply(RequestOptions.skipMemoryCacheOf(true))
                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                            .apply(RequestOptions.circleCropTransform()).into(binding.userProfilePhoto);
                    Toast.makeText(getActivity(), "size 5 b:" + new File(data.getData().getPath()).exists(), Toast.LENGTH_LONG).show();
                } else {
                    GlideApp.with(this).load(data.getData())
                            .signature(new MediaStoreSignature("",System.currentTimeMillis(),0))
                            .apply(RequestOptions.skipMemoryCacheOf(true))
                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                            .apply(RequestOptions.circleCropTransform()).into(binding.userProfilePhoto);
                    Toast.makeText(getActivity(), "size 5 c:" + data.getData(), Toast.LENGTH_LONG).show();
                }
                viewModel.galleryPicUrl = data.getData();
                Toast.makeText(getActivity(), "size 6:" + data.getData(), Toast.LENGTH_LONG).show();
            }
        }
        ((RelativeLayout) binding.userProfilePhoto.getParent()).invalidate();
        onActivityResult=true;
    }

}