package com.boutiqaat.android.boutiqaat.ui.activity;

import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import com.boutiqaat.android.boutiqaat.R;
import com.boutiqaat.android.boutiqaat.databinding.SignUpActivityBinding;
import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.ProfileData;
import com.boutiqaat.android.boutiqaat.ui.ActivityView;
import com.boutiqaat.android.boutiqaat.utils.Constants;
import com.boutiqaat.android.boutiqaat.utils.Utils;
import com.boutiqaat.android.boutiqaat.viewmodel.implementation.LocationViewModel;
import com.boutiqaat.android.boutiqaat.viewmodel.implementation.ProfileViewModel;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import timber.log.Timber;

/**
 * This is the user interface class from where user can sign-up. If the sign-up email already taken up by other users then
 * appropriate error messages will be shown. Validation of email, name, password data is also done here.
 */
public class SignupActivity extends DaggerAppCompatActivity implements ActivityView {
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    ProgressDialog progressDialog;
    private ProfileViewModel viewModel;
    private LocationViewModel locaViewModel;
    private SignUpActivityBinding binding;
    private boolean flag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("----onCreate----");
        binding = DataBindingUtil.setContentView(this, R.layout.signup_activity);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel.class);
        locaViewModel = ViewModelProviders.of(this, viewModelFactory).get(LocationViewModel.class);
        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.d("----SignUp clicked----");
                flag = true;
                signup();
            }
        });

        binding.linkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.d("----SignUp linkLogin----");
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        String address = new Utils().getAddress(locaViewModel.lat, locaViewModel.lon, this);
        if (address.length() == 0) {
            binding.inputLocation.setHint(getString(R.string.address_not_available));
        } else {
            binding.inputLocation.setText(address);
        }
        initObservers();
    }

    @Override
    protected void attachBaseContext(Context base) {
        int langPos = base.getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE).getInt(Constants.LANGUAGE, Constants.POSTION_ENG);
        String locale = new Utils().getLocale(langPos);
        super.attachBaseContext(new Utils().setLocale(
                locale,
                base));
        Timber.d("attachBaseContext");
    }

    @Override
    public void onStop() {
        super.onStop();
        viewModel.getProfilesLiveData().removeObservers(this);
        Timber.d("----SignUp onStop----");
    }

    /**
     * Registering an Observer for checking if the email information provided is present in the database.
     */
    public void initObservers() {
        // viewModel.getProfilesLiveData().removeObservers(this);
        //if(viewModel.getProfilesLiveData().hasObservers())return;
        viewModel.getProfilesLiveData().observe(this, map -> {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            if (!flag) return;
            if (map.containsKey(binding.inputEmail.getText().toString().trim())) {
                new AlertDialog.Builder(this)
                        .setMessage(getString(R.string.email_already_in_use))
                        .setPositiveButton(R.string.ok, (dialog, button) -> dialog.dismiss())
                        .show();
            } else {
                String name = binding.inputName.getText().toString();
                String email = binding.inputEmail.getText().toString().trim();
                String password = binding.inputPassword.getText().toString();
                String location = binding.inputLocation.getText().toString();
                ProfileData profileData = new ProfileData();
                profileData.email = email;
                profileData.location = location;
                profileData.email = email;
                profileData.name = name;
                profileData.password = password;
                viewModel.storeProfileDetails(profileData);
                getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE).edit().putString(Constants.LOGGED_IN_USER_EMAIL,
                        email).commit();
                viewModel.setProfileDataOfLoggedInUser(profileData);
                finish();
                Timber.d("---SingUp finished in-onCreate----");
            }

        });
    }

    /**
     * Sign up information provided by the user in the ui are checked in the database.This is to find out if the email which is used as
     * username is already taken up by some other user.
     */
    public void signup() {
        Timber.d("Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        // binding.btnSignup.setEnabled(false);

        progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String email = binding.inputEmail.getText().toString().trim();
        Timber.d("Calling getProfileDetails");
        viewModel.getProfileDetails(email);

       /* new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000); */
    }


    public void onSignupSuccess() {
        // binding.btnSignup.setEnabled(true);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        //binding.btnSignup.setEnabled(true);
    }

    /**
     * Validation of input information.
     *
     * @return
     */
    public boolean validate() {
        boolean valid = true;

        String name = binding.inputName.getText().toString().trim();
        String email = binding.inputEmail.getText().toString().trim();
        String password = binding.inputPassword.getText().toString().trim();
        String location = binding.inputLocation.getText().toString().trim();

        if (name.isEmpty() || name.length() < 3) {
            binding.inputName.setError("at least 3 characters");
            valid = false;
        } else {
            binding.inputName.setError(null);
        }

        if (location.isEmpty()) {
            binding.inputLocation.setError("InValid Address");
            valid = false;
        } else {
            binding.inputLocation.setError(null);
        }


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.inputEmail.setError("enter a valid email address");
            valid = false;
        } else {
            binding.inputEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            binding.inputPassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            binding.inputPassword.setError(null);
        }


        return valid;
    }

}