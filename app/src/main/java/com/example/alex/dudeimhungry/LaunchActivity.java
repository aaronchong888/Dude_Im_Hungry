package com.example.alex.dudeimhungry;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;

import com.google.android.gms.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;


import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import com.example.alex.dudeimhungry.YelpSetup;




public class LaunchActivity extends ActionBarActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
                    LocationListener {

    protected static final int REQUEST_CHECK_SETTINGS = 0x1; //location dialog result

    //constants used as keys for data retrieval in onSavedInstanceState
    final String REQUESTING_LOCATION_UPDATES_KEY = "REQUESTING_LOCATION_UPDATES_KEY";
    final String LOCATION_KEY = "LOCATION_KEY";
    final String LAST_UPDATED_TIME_STRING_KEY = "LAST_UPDATED_TIME_STRING_KEY";

    //Globals
    YelpSetup yelp;
    String mLastUpdateTime;
    boolean mRequestingLocationUpdates;
    LocationRequest mLocReq;
    GoogleApiClient mGoogleApiClient;
    Location myLoc = new Location("");
    static double myLat;
    static double myLong;
    // variables used in the UI
    private LinearLayout resultlayout;
    private ImageButton hungrybtn;
    private TextView nameView;
    private TextView distanceView;
    //private TextView priceView;
    private Button mapbtn;
    private RatingBar ratebar;
    private ImageView cickView;
    static int hitCount = 0;

    @Override
    protected void onStart() {
        mGoogleApiClient.connect(); //call this when we want to begin talking to google play.
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        stopLocationUpdates();
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onLocationChanged(Location location) {
        myLoc = location;
        myLat = myLoc.getLatitude();
        myLong = myLoc.getLongitude();
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        Log.i("Location Settings", "(Lat, Long): (" + myLat +
                ", " + myLong + ")");
        //do whatever we need with the updates location here.
    }

    // Coordinate retrieval functions
    public static double getUserLat() {
        return (34.0722);
    }

    public static double getUserLong() {
        return (-118.4441);
    }

    /*
    OnConnected means that we connected to GoogleApi, we dont necessarily have a location yet
     */
    @Override
    public void onConnected(Bundle bundle) {
        //adds the location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocReq);

        //make sure the phone is giving us what we asked for
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        builder.setAlwaysShow(true);
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:  //we have what we want
                        Log.v("Location settings", "Location settings are satisfied.");
                        startLocationUpdates();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.v("Location Settings", "Location settings not satisfied. Starting REQUEST_CHECK_SETTINGS");
                        //we need to ask user before phone will give location information
                        try {
                            status.startResolutionForResult(LaunchActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // This phone can't give us what we need.
                        Log.e("Location Settings", "The user \"never\" wants to give us location info");
                        try {
                            status.startResolutionForResult(LaunchActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                        }
                        break;
                }
            }
        });
        yelp.searchByCoordinate(); //now that we have gps coords we can search
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS: //if we got the request check settings callback
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i("Location Settings", "onActivityForResult returned RESUME_OK");
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i("Location Settings", "user did not want to give us location permissions");
                        break;
                }
                break;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.w("Location Settings", "Cannot connect to Google Play Services");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        //object to connect to google api stuff
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        updateValuesFromBundle(savedInstanceState);
        if(mLocReq == null) {
            createLocationRequest(); //update mLocReq
        }
        yelp = new YelpSetup(); //initialize object
        // used for UI
        resultlayout = (LinearLayout)findViewById(R.id.ResultDisplay);
        hungrybtn = (ImageButton)findViewById(R.id.imageButton);
        nameView = (TextView)findViewById(R.id.editName);
        distanceView = (TextView)findViewById(R.id.editDistance);
        //priceView = (TextView)findViewById(R.id.editPrice);
        mapbtn = (Button)findViewById(R.id.btnDirection);
        ratebar = (RatingBar)findViewById(R.id.ratingBar);
        cickView = (ImageView)findViewById(R.id.imageClick);
        hungrybtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                yelp.businessInfo(hitCount);
                cickView.setVisibility(View.INVISIBLE);
                resultlayout.setVisibility(View.VISIBLE);
                nameView.setText(yelp.businessName);
                distanceView.setText(String.format ("%.1f", yelp.busDist));
                distanceView.append(" miles");
                float rating = (float) yelp.rating;
                ratebar.setRating(rating);
                hitCount++;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }


    protected void startLocationUpdates() {
        try {
            Log.v("Location Settings", "Starting requestLocationUpdates()");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocReq, this);
        } catch(java.lang.IllegalStateException e) {
            Log.d("Location Settings", "GoogleApiClient is not working");
        }

    }

    //if we want to save power and/or leave app.
    protected void stopLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, LaunchActivity.this);
        } catch(java.lang.IllegalStateException e) {
            Log.d("Location Settings", "GoogleApiClient is not connected");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_launch, menu);
        return true;
    }


    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, myLoc);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and
            // make sure that the Start Updates and Stop Updates buttons are
            // correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
                //setButtonsEnabledState();
            }

            // Update the value of mCurrentLocation from the Bundle and update the
            // UI to show the correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that
                // mCurrentLocationis not null.
                myLoc = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(
                        LAST_UPDATED_TIME_STRING_KEY);
            }
            //updateUI();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //Initialize LocationRequest for high-accuracy update every 10 seconds
    protected void createLocationRequest() {
        mLocReq = new LocationRequest();
        mLocReq.setInterval(10000); //in ms
        mLocReq.setFastestInterval(5000); //upper rate to receive updates
        mLocReq.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); //highest accuracy and power
    }

    public void onGPSClick(View v) {
        //check if we have a location
        if(myLoc == null) {
            Toast.makeText(this, R.string.please_enable_gps, Toast.LENGTH_LONG).show();
            return;
        }
        //Uri gmmIntentUri = Uri.parse("google.navigation:q=40.7127837,-74.00594130000002");
        String uriString = String.format(Locale.ENGLISH, "google.navigation:q=%f,%f",
                YelpSetup.busLat, YelpSetup.busLong);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));
        intent.setPackage("com.google.android.apps.maps"); //open in google maps by default

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
