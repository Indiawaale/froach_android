package com.foodroacher.app.android.ui.activities;

import com.foodroacher.app.android.R;
import com.foodroacher.app.android.ui.fragments.AboutUsFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class AboutUs extends AppCompatActivity {
    private Toolbar mToolbar = null;
    private AboutUsFragment mAboutUsFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        initViews();
    }

    private void initViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        initActionBar();
        addAboutUsFragment();
    }

    private void addAboutUsFragment() {
        mAboutUsFragment = new AboutUsFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.flFragmentContainer, mAboutUsFragment, "about_us").commit();

    }

    private void initActionBar() {
        setUpToolbar();
    }

    private void setUpToolbar() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        Intent aboutUsIntent = new Intent(context, AboutUs.class);
        context.startActivity(aboutUsIntent);
    }
    
}
