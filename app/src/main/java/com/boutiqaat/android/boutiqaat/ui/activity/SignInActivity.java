package com.boutiqaat.android.boutiqaat.ui.activity;

import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import com.boutiqaat.android.boutiqaat.R;
import com.boutiqaat.android.boutiqaat.databinding.LoginActivityBinding;
import com.boutiqaat.android.boutiqaat.datasource.db.datamodel.ProfileData;
import com.boutiqaat.android.boutiqaat.ui.ActivityView;
import com.boutiqaat.android.boutiqaat.utils.Constants;
import com.boutiqaat.android.boutiqaat.utils.Utils;
import com.boutiqaat.android.boutiqaat.viewmodel.implementation.ProfileViewModel;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import timber.log.Timber;

/**
 * This is the user interface class from where user can sign-in. If the log-in information is not present then appropriate
 * error messages will be shown. Validation of email, name data is also done here.
 */
public class SignInActivity extends DaggerAppCompatActivity implements ActivityView {
    private static final int REQUEST_SIGNUP = 0;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private LoginActivityBinding binding;
    private SharedPreferences prefs;
    private ProfileViewModel viewModel;
    private ProgressDialog progressDialog;
    private boolean flag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.login_activity);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel.class);
        prefs = getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE);
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = true;
                login();
            }
        });

        binding.linkSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
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


    /**
     * Observer registered for checking if the users information is present in the database.
     */
    public void initObservers() {
        viewModel.getProfilesLiveData().observe(this, map -> {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            if (!flag) return;
            String email = binding.inputEmail.getText().toString().trim();
            if (map.containsKey(email)) {
                ProfileData profileData = map.get(email);
                if (profileData.password.equals(binding.inputPassword.getText().toString()) &&
                        (profileData.name.equals(binding.inputName.getText().toString()))) {
                    prefs.edit().putString(Constants.LOGGED_IN_USER_EMAIL, binding.inputEmail.getText().toString().trim()).commit();
                    viewModel.setProfileDataOfLoggedInUser(profileData);
                    finish();
                } else {
                    showDialog();
                }
            } else {
                showDialog();
                Timber.d("---SingUp finished in-onCreate----");
            }

        });
    }

    private void showDialog() {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.user_pwd_wrong))
                .setPositiveButton(R.string.ok, (dialog, button) -> dialog.dismiss())
                .show();
    }

    /**
     * email is used as the usename and we check in the db if the user's email is present.
     */
    public void login() {
        Timber.d("Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        progressDialog = new ProgressDialog(SignInActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = binding.inputEmail.getText().toString();
        String password = binding.inputPassword.getText().toString();
        String name = binding.inputName.getText().toString();

        viewModel.getProfileDetails(email);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                this.finish();
            }
        }
    }

    public void onLoginSuccess() {
        binding.btnLogin.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        binding.btnLogin.setEnabled(true);
    }

    /**
     * Validation of input information.
     *
     * @return
     */
    public boolean validate() {
        boolean valid = true;

        String email = binding.inputEmail.getText().toString().trim();
        String password = binding.inputPassword.getText().toString().trim();

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