package com.foodroacher.app.android.ui.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.foodroacher.app.android.R;
import com.foodroacher.app.android.app.FoodRoacherApp;
import com.foodroacher.app.android.network.FoodEvents;
import com.foodroacher.app.android.network.NetworkUtils;
import com.foodroacher.app.android.utils.GCMClientManager;
import com.foodroacher.app.android.utils.MSBandUtils;
import com.foodroacher.app.android.utils.PreferenceUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.microsoft.band.BandException;
import com.microsoft.band.tiles.BandTile;
import com.microsoft.band.tiles.pages.Barcode;
import com.microsoft.band.tiles.pages.BarcodeType;
import com.microsoft.band.tiles.pages.FlowPanel;
import com.microsoft.band.tiles.pages.FlowPanelOrientation;
import com.microsoft.band.tiles.pages.PageLayout;
import com.microsoft.band.tiles.pages.TextBlock;
import com.microsoft.band.tiles.pages.TextBlockFont;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class Home extends BaseActivity {

    private static final int REQUEST_CHECK_LOCATION_SETTINGS = 101;
    private SupportMapFragment mMapFragment = null;
    private GoogleMap mGoogleMap = null;
    private Location mCurrentLocation = null;
    private MarkerOptions mCurrentLocationMarker = null;
    private GoogleApiClient mGoogleApiClient = null;
    private LocationRequest mLocationRequest = null;
    private boolean isGoogleApiConnected = false;
    private boolean isRequestingLocationUpdates = false;
    private Toolbar mToolbar = null;
    private DrawerLayout mDrawerLayout = null;
    private NavigationView mNavigationView = null;
    private FloatingActionButton mFabNewOrder = null;
    private GetEventsTask mEventsTask = null;
    private List<FoodEvents> mEvents = new ArrayList<FoodEvents>();
    private List<Marker> mEventsMarker = new ArrayList<Marker>();
    private HashMap<Marker, FoodEvents> mMarkerToEventMap = new HashMap<Marker, FoodEvents>();
    private GCMClientManager pushClientManager;
    String PROJECT_NUMBER = "700875766105";
    private RegisterGCMTask mRegisterGcmTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
//        runAddTile();
        if (isUserRegisterd()) {
            setUpPush();
            initViews();
            buildGoogleApiClient();
            cancelNotifications();
        } else {
            FoodRoacherApp.showGenericToast(getBaseContext(), getString(R.string.registration_error));
        }
    }

    private void setUpPush() {
        pushClientManager = new GCMClientManager(this, PROJECT_NUMBER);
        pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
            @Override
            public void onSuccess(String registrationId, boolean isNewRegistration) {
                FoodRoacherApp.showGenericToast(getBaseContext(), registrationId);
                // SEND async device registration to your back-end server
                // linking user with device registration id
                // POST
                // https://my-back-end.com/devices/register?user_id=123&device_id=abc

                registerGcmKeyToServer(registrationId);
            }

            @Override
            public void onFailure(String ex) {
                super.onFailure(ex);
                FoodRoacherApp.showGenericToast(getBaseContext(), "gcm failed");
                // If there is an error registering, don't just keep trying to
                // register.
                // Require the user to click a button again, or perform
                // exponential back-off when retrying.
            }
        });

    }

    private void registerGcmKeyToServer(String registrationId) {
        mRegisterGcmTask = new RegisterGCMTask();
        mRegisterGcmTask.execute(PreferenceUtils.getUserId(getBaseContext()), registrationId);

    }

    private boolean isUserRegisterd() {
        return PreferenceUtils.getUserId(getBaseContext()) != null && PreferenceUtils.isRegistered(getBaseContext());
    }

    private void cancelNotifications() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();

    }

    private void initViews() {
        setupMapViews();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.dlVendorHome);
        mNavigationView = (NavigationView) findViewById(R.id.navHome);
        mFabNewOrder = (FloatingActionButton) findViewById(R.id.fabAddLocation);
        mFabNewOrder.setOnClickListener(mOnClickListener);
        initActionBar();
        setUpNavDrawer();
    }

    private void setUpNavDrawer() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationIcon(R.drawable.ic_launcher);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
        mNavigationView.setNavigationItemSelectedListener(mNavigationSelectionListener);
    }

    private void setupMapViews() {
        mMapFragment = SupportMapFragment.newInstance(getMapOptions());
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.flFragmentContainer, mMapFragment);
        fragmentTransaction.commit();
        mMapFragment.getMapAsync(mOnMapReadyCallBack);
    }

    private void setupGoogleMap(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setOnMyLocationButtonClickListener(mOnMyLocationButtonClickListener);
        mGoogleMap.setOnInfoWindowClickListener(mOnInfoWindowClickListener);
    }

    private void setUpToolbar() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
    }

    private GoogleMapOptions getMapOptions() {
        GoogleMapOptions mMapOptions = new GoogleMapOptions();
        mMapOptions.compassEnabled(false);
        mMapOptions.mapToolbarEnabled(false);
        mMapOptions.mapType(GoogleMap.MAP_TYPE_NORMAL);
        mMapOptions.rotateGesturesEnabled(false);
        mMapOptions.zoomControlsEnabled(false);
        mMapOptions.liteMode(false);
        return mMapOptions;
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(mGoogleApiCallback).addOnConnectionFailedListener(mConnectionFailedCallback)
                .addApi(LocationServices.API).build();
    }

    private void createLocationRequest() {
        if (mLocationRequest == null) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(15000);
            mLocationRequest.setFastestInterval(15000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }
    }

    private void checkLocationServiceEnabled() {
        createLocationRequest();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest).setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(mLocationResultCallback);
    }

    private void startRequestingLocationUpdates() {
        isRequestingLocationUpdates = true;
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, mLocationListener);
        FoodRoacherApp.showGenericToast(getBaseContext(), getString(R.string.waiting_for_location));
        updateCurrentLocationOnMap();
    }

    private void stopRequestingLocationUpdates() {
        if (isRequestingLocationUpdates) {
            isRequestingLocationUpdates = false;
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mLocationListener);
        }
    }

    private void updateCurrentLocationOnMap() {
        if (mCurrentLocation != null) {
            LatLng clickedLocation = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            mGoogleMap.clear();
            mCurrentLocationMarker = new MarkerOptions().position(clickedLocation).title("Current Location");
            mGoogleMap.addMarker(mCurrentLocationMarker);
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(clickedLocation, 18.0f), 1000, null);
        }

    }

    private void fetchNewEvents(Location mCurrentLocation) {
        if (mEventsTask != null && !mEventsTask.isCancelled()) {
            return;
        }
        mEventsTask = new GetEventsTask();
        mEventsTask.execute(PreferenceUtils.getUserId(getBaseContext()), Double.toString(mCurrentLocation.getLatitude()), Double.toString(mCurrentLocation.getLongitude()));

    }

    private ResultCallback<LocationSettingsResult> mLocationResultCallback = new ResultCallback<LocationSettingsResult>() {

        @Override
        public void onResult(LocationSettingsResult result) {
            final Status status = result.getStatus();
            final LocationSettingsStates states = result.getLocationSettingsStates();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    // All location settings are satisfied. The client can
                    // initialize location
                    // requests here.
                    startRequestingLocationUpdates();
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    // Location settings are not satisfied. But could be fixed
                    // by showing the user
                    // a dialog.
                    try {
                        // Show the dialog by calling
                        // startResolutionForResult(),
                        // and check the result in onActivityResult().
                        status.startResolutionForResult(Home.this, REQUEST_CHECK_LOCATION_SETTINGS);
                    } catch (SendIntentException e) {
                        // Ignore the error.
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    // Location settings are not satisfied. However, we have no
                    // way to fix the
                    // settings so we won't show the dialog.
                    break;
            }
        }
    };
    private LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location changedLocation) {
            mCurrentLocation = changedLocation;
            updateCurrentLocationOnMap();
            stopRequestingLocationUpdates();
            fetchNewEvents(mCurrentLocation);
        }

    };

    private void onGoogleApiDisabled() {
        isGoogleApiConnected = false;
    }

    private void onGoogleApiEnabled(Bundle connectionHint) {
        isGoogleApiConnected = true;
        checkLocationServiceEnabled();
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

    private OnMapReadyCallback mOnMapReadyCallBack = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            setupGoogleMap(googleMap);
        }

    };
    private OnInfoWindowClickListener mOnInfoWindowClickListener = new OnInfoWindowClickListener() {

        @Override
        public void onInfoWindowClick(Marker marker) {
            FoodEvents event = mMarkerToEventMap.get(marker);
            FoodRoacherApp.showGenericToast(getBaseContext(), "infor window -> " + event.getTitle());
            EventDetails.launchEventDetail(Home.this, event);
        }
    };

    private OnMyLocationButtonClickListener mOnMyLocationButtonClickListener = new OnMyLocationButtonClickListener() {

        @Override
        public boolean onMyLocationButtonClick() {
            // TODO Auto-generated method stub
            return false;
        }
    };

    private void initActionBar() {
        setUpToolbar();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.menu_drawer_home_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fabAddLocation:
                    onClickAddPlace();
                    break;

                default:
                    break;
            }

        }
    };
    private OnNavigationItemSelectedListener mNavigationSelectionListener = new OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            boolean result = false;
            switch (menuItem.getItemId()) {
                case R.id.menu_drawer_home_myprofile:
                    result = true;
                    break;
                case R.id.menu_drawer_home_add_place:
                    onClickAddPlace();
                    result = true;
                    break;
                case R.id.menu_drawer_home_myfavourites:
                    result = true;
                    break;
                case R.id.menu_drawer_home_settings:
                    result = true;
                    break;
                case R.id.menu_drawer_home_help:
                    onClickHelp();
                    result = true;
                    break;
                case R.id.menu_drawer_home_aboutus:
                    onClickAboutUs();
                    result = true;
                    break;
                default:
                    break;
            }
            mDrawerLayout.closeDrawers();
            return result;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_LOCATION_SETTINGS:
                onCheckLocationResult(resultCode, data);
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onCheckLocationResult(int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                // All required changes were successfully made
                startRequestingLocationUpdates();
                break;
            case RESULT_CANCELED:
                // TODO proper finisihing rewuired
                finish();
                break;
            default:
                break;
        }
    }

    private void onClickAboutUs() {

        AboutUs.launchAboutUs(this);
    }

    private void onClickHelp() {
        Help.launchHelp(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onPause() {
        stopRequestingLocationUpdates();
        super.onPause();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    private void onClickAddPlace() {

    }

    public static void launchHome(Activity context) {
        Intent homeIntent = new Intent(context, Home.class);
        context.startActivity(homeIntent);
    }

    private void onGetEventsResult(List<FoodEvents> result) {
        if (result != null) {
            FoodRoacherApp.showGenericToast(getBaseContext(), getString(R.string.events_success));
            mEvents.clear();
            mEvents.addAll(result);
            updateEventsOnMap();
        } else {
            showFetchEventsError();
        }
    }

    private void updateEventsOnMap() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        int padding = 50; // offset from edges of the map in pixels
        mGoogleMap.clear();
        mEventsMarker.clear();
        mMarkerToEventMap.clear();
        for (FoodEvents events : mEvents) {
            LatLng location = new LatLng(events.getLatitude(), events.getLongitude());
            MarkerOptions markerOption = new MarkerOptions().position(location).title(events.getTitle()).snippet(events.getDescription());
            Marker newMarker = mGoogleMap.addMarker(markerOption);
            mEventsMarker.add(newMarker);
            mMarkerToEventMap.put(newMarker, events);
            builder.include(newMarker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mGoogleMap.setOnInfoWindowClickListener(mOnInfoWindowClickListener);
        mGoogleMap.animateCamera(cu, 1000, null);
        runAddTile();
    }

    private void showFetchEventsError() {
        FoodRoacherApp.showGenericToast(getBaseContext(), getString(R.string.events_error));
    }

    private class GetEventsTask extends AsyncTask<String, Void, List<FoodEvents>> {
        @Override
        protected void onPreExecute() {
            showProgressDialog(getString(R.string.registering), false);
            super.onPreExecute();
        }

        @Override
        protected List<FoodEvents> doInBackground(String... params) {
            List<FoodEvents> result = NetworkUtils.getFoodEvents(getBaseContext(), params[0], params[1], params[2]);
            return result;
        }

        @Override
        protected void onPostExecute(List<FoodEvents> result) {
            dismissProgressDialog();
            if (result != null) {
                onGetEventsResult(result);
            } else {
                showFetchEventsError();
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onCancelled() {
            dismissProgressDialog();
            super.onCancelled();
        }

    }

    private class RegisterGCMTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean result = NetworkUtils.registerUserGcm(getBaseContext(), params[0], params[1]);
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                FoodRoacherApp.showGenericToast(getBaseContext(), "gcm registered success");
            } else {
                FoodRoacherApp.showGenericToast(getBaseContext(), "gcm registered fail");
            }
            PreferenceUtils.setGcmRegistered(getBaseContext(), result);
            super.onPostExecute(result);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

    }
    private void runAddTile(){
        new AddTileToBandTask().execute();
    }
    private boolean addTile() throws Exception {
        if (MSBandUtils.doesTileExist(FoodRoacherApp.getApp(getBaseContext()).getClient().getTileManager().getTiles().await(), MSBandUtils.tileId)) {
            return true;
        }
        /* Set the options */
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap tileIcon = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.ic_launcher, options);
        BandTile tile = new BandTile.Builder(MSBandUtils.tileId, "Barcode Tile", tileIcon)
                .setPageLayouts(createBarcodeLayout(BarcodeType.CODE39), createBarcodeLayout(BarcodeType.PDF417)).build();
        if (FoodRoacherApp.getApp(getBaseContext()).getClient().getTileManager().addTile(this, tile).await()) {
            return true;
        } else {
            return false;
        }
    }

    private PageLayout createBarcodeLayout(BarcodeType type) {
        return new PageLayout(new FlowPanel(15, 0, 245, 105, FlowPanelOrientation.VERTICAL).addElements(new Barcode(0, 0, 221, 70, type).setId(11).setMargins(3, 0, 0, 0))
                .addElements(new TextBlock(0, 0, 230, 30, TextBlockFont.SMALL, 0).setId(21).setColor(Color.RED)));
    }
    private class AddTileToBandTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(2000);
                addTile();
            } catch (BandException e) {
                String exceptionMessage="";
                switch (e.getErrorType()) {
                case DEVICE_ERROR:
                    exceptionMessage = "Please make sure bluetooth is on and the band is in range.";
                    break;
                case UNSUPPORTED_SDK_VERSION_ERROR:
                    exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.";
                    break;
                case SERVICE_ERROR:
                    exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.";
                    break;
                case BAND_FULL_ERROR:
                    exceptionMessage = "Band is full. Please use Microsoft Health to remove a tile.";
                    break;
                default:
                    exceptionMessage = "Unknown error occured: " + e.getMessage();
                    break;
                }
            } catch (Exception e) {
            } 
            return null;
        }
    }
}
