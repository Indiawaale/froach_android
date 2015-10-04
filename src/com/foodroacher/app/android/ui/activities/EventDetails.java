package com.foodroacher.app.android.ui.activities;

import com.foodroacher.app.android.R;
import com.foodroacher.app.android.network.FoodEvents;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class EventDetails extends AppCompatActivity {
    public static final String EXTRA_EVENT = "event";
    private Toolbar mToolbar = null;
    private GoogleApiClient mGoogleApiClient = null;
    private boolean isGoogleApiConnected = false;
    private SupportMapFragment mMapFragment = null;
    private View mFlMapContainer = null;
    private GoogleMap mGoogleMap = null;
    private FoodEvents mEvent = null;
    private TextView mTxtEventName = null;
    private TextView mTxtEventDetail = null;
    private TextView mTxtEventTime = null;
    private FloatingActionButton fabShare = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataFromIntent();
        setContentView(R.layout.fragment_event_detail);
        initViews();
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View clickedView) {
            switch (clickedView.getId()) {
                case R.id.flMapContainer:
                    onClickRoute();
                    break;
                case R.id.fabShare:
                    onClickShare();
                    break;
                default:
                    break;
            }
        }
    };
    private void onClickRoute() {
        Uri gmmIntentUri = Uri.parse("google.navigation:q="+mEvent.getLatitude()+","+ mEvent.getLongitude());
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }
    private void initDataFromIntent() {
        mEvent = (FoodEvents) getIntent().getSerializableExtra(EXTRA_EVENT);
        buildGoogleApiClient();

    }
   
    private void initViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTxtEventName = (TextView) findViewById(R.id.txtEventName);
        mTxtEventDetail = (TextView) findViewById(R.id.txtEventDetails);
        mTxtEventTime = (TextView) findViewById(R.id.txtTime);
        fabShare = (FloatingActionButton)findViewById(R.id.fabShare);
        mFlMapContainer = findViewById(R.id.flMapContainer);
        mFlMapContainer.setOnClickListener(mOnClickListener);
        fabShare.setOnClickListener(mOnClickListener);
        initActionBar();
        updateUi();
        setupLiteMap();
    }

    private void onClickShare(){
        String shareBody = mEvent.getTitle();
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.details, menu);
        return true;
    }

    private void updateUi() {
        mTxtEventName.setText(mEvent.getTitle());
        mTxtEventDetail.setText(mEvent.getDescription());
        mTxtEventTime.setText("" + mEvent.getStartTime());
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

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(mGoogleApiCallback).addOnConnectionFailedListener(mConnectionFailedCallback)
                .addApi(LocationServices.API).build();
    }

    private void onGoogleApiEnabled(Bundle connectionHint) {
        isGoogleApiConnected = true;
    }

    private void onGoogleApiDisabled() {
        isGoogleApiConnected = false;
    }

    private OnConnectionFailedListener mConnectionFailedCallback = new OnConnectionFailedListener() {

        @Override
        public void onConnectionFailed(ConnectionResult arg0) {
            onGoogleApiDisabled();
        }
    };
    private ConnectionCallbacks mGoogleApiCallback = new ConnectionCallbacks() {

        @Override
        public void onConnectionSuspended(int arg0) {
            onGoogleApiDisabled();

        }

        @Override
        public void onConnected(Bundle connectionHint) {
            onGoogleApiEnabled(connectionHint);

        }
    };

    private void setupLiteMap() {
        mMapFragment = SupportMapFragment.newInstance(getMapOptionsLite());
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.flMapContainer, mMapFragment);
        fragmentTransaction.commit();
        mMapFragment.getMapAsync(mOnMapReadyCallBack);

    }

    private void setupGoogleMapLite(GoogleMap googleMap, LatLng placeLatLng) {
        mGoogleMap = googleMap;
        mGoogleMap.setMyLocationEnabled(false);
        mGoogleMap.addMarker(new MarkerOptions().position(placeLatLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(placeLatLng, 18.0f));
    }

    private GoogleMapOptions getMapOptionsLite() {
        GoogleMapOptions mMapOptions = new GoogleMapOptions();
        mMapOptions.compassEnabled(false);
        mMapOptions.mapToolbarEnabled(false);
        mMapOptions.mapType(GoogleMap.MAP_TYPE_NORMAL);
        mMapOptions.rotateGesturesEnabled(false);
        mMapOptions.zoomControlsEnabled(false);
        mMapOptions.liteMode(true);
        return mMapOptions;
    }

    private LatLng getCurrentLocation() {
        return new LatLng(mEvent.getLatitude(), mEvent.getLongitude());
    }

    private OnMapReadyCallback mOnMapReadyCallBack = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            setupGoogleMapLite(googleMap, getCurrentLocation());
        }

    };

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

    public static void launchEventDetail(Activity context, FoodEvents event) {
        Intent aboutUsIntent = new Intent(context, EventDetails.class);
        aboutUsIntent.putExtra(EXTRA_EVENT, event);
        context.startActivity(aboutUsIntent);
    }

}
