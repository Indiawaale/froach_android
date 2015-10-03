package com.foodroacher.app.android.ui.activities;

import com.foodroacher.app.android.R;
import com.foodroacher.app.android.ui.fragments.HelpFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class Help extends AppCompatActivity {
    private Toolbar mToolbar = null;
    private HelpFragment mHelpFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        initViews();
    }

    private void initViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        initActionBar();
        addHelpFragment();
    }

    private void addHelpFragment() {
        mHelpFragment = new HelpFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.flFragmentContainer, mHelpFragment, "time_picker").commit();

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
    
    public static void launchHelp(Activity context) {
        Intent helpIntent = new Intent(context, Help.class);
        context.startActivity(helpIntent);
    }
    
}
