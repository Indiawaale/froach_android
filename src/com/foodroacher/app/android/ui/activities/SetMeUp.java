package com.foodroacher.app.android.ui.activities;

import com.foodroacher.app.android.R;
import com.foodroacher.app.android.app.FoodRoacherApp;
import com.foodroacher.app.android.network.NetworkUtils;
import com.foodroacher.app.android.network.RegistrationResult;
import com.foodroacher.app.android.ui.fragments.SetMeUpFragment;
import com.foodroacher.app.android.ui.fragments.SetMeUpFragment.OnClickSubmitListener;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class SetMeUp extends BaseActivity {
    private Toolbar mToolbar = null;
    private SetMeUpFragment mSetMeUpFragment = null;
    private RegisterUserTask mRegisterTask = null;
    private OnClickSubmitListener mOnClickSubmitListener = new OnClickSubmitListener() {
        @Override
        public void onClickSubmit(String email, String password, int collegeType) {
            if (NetworkUtils.isNetworkConnected(getBaseContext())) {
                registerUser(email, password, collegeType);
            } else {
                FoodRoacherApp.showGenericToast(getBaseContext(), getString(R.string.no_network));
            }
        }

        private void registerUser(String email, String password, int collegeType) {
            if (mRegisterTask != null && !mRegisterTask.isCancelled()) {
                mRegisterTask.cancel(true);
            }
            mRegisterTask = new RegisterUserTask();
            mRegisterTask.execute(email, password, Integer.toString(collegeType));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);
        initViews();
    }

    private void initViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        initActionBar();
        addAboutUsFragment();
    }

    private void addAboutUsFragment() {
        mSetMeUpFragment = new SetMeUpFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.flFragmentContainer, mSetMeUpFragment, "about_us").commit();
        mSetMeUpFragment.setOnClickSubmitListener(mOnClickSubmitListener);
    }

    private void initActionBar() {
        setUpToolbar();
    }

    private void setUpToolbar() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void onRegistrationSuccess(RegistrationResult result) {
        FoodRoacherApp.showGenericToast(getBaseContext(), getString(R.string.registration_success));

    }

    private void showRegistrationError() {
        FoodRoacherApp.showGenericToast(getBaseContext(), getString(R.string.registration_error));
    }

    public static void launchSetMeUp(Activity context) {
        Intent aboutUsIntent = new Intent(context, SetMeUp.class);
        context.startActivity(aboutUsIntent);
    }

    private class RegisterUserTask extends AsyncTask<String, Void, RegistrationResult> {
        @Override
        protected void onPreExecute() {
            showProgressDialog(getString(R.string.registering), false);
            super.onPreExecute();
        }

        @Override
        protected RegistrationResult doInBackground(String... params) {
            RegistrationResult result = NetworkUtils.registerUser(getBaseContext(), params[0], params[1], params[2]);
            return result;
        }

        @Override
        protected void onPostExecute(RegistrationResult result) {
            dismissProgressDialog();
            if (result != null) {
                onRegistrationSuccess(result);
            } else {
                showRegistrationError();
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onCancelled() {
            dismissProgressDialog();
            super.onCancelled();
        }

    }
}
