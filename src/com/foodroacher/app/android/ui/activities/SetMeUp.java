package com.foodroacher.app.android.ui.activities;

import com.foodroacher.app.android.R;
import com.foodroacher.app.android.ui.fragments.AboutUsFragment;
import com.foodroacher.app.android.ui.fragments.SetMeUpFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class SetMeUp extends AppCompatActivity {
    private Toolbar mToolbar = null;
    private SetMeUpFragment mSetMeUpFragment = null;

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
        switch(item.getItemId()){
            case android.R.id.home:{
                onBackPressed();
                return true ;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    public static void launchAboutUs(Activity context) {
        Intent aboutUsIntent = new Intent(context, SetMeUp.class);
        context.startActivity(aboutUsIntent);
    }
    
}
