package com.aaronsng.wheresitgo.common;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import com.aaronsng.wheresitgo.R;
import com.aaronsng.wheresitgo.activity.MainActivity;

public class  CustomLocationManager implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener,
        ResultCallback<LocationSettingsResult>
{

    protected LocationSettingsRequest mLocationSettingsRequest;
    private GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Boolean isRunning=false;
    private IGPSActivity main;
    Location lastKnownLocation;
    private Handler handler = new Handler();
    Runnable locationRunnable=null;
    Context context;
    public CustomLocationManager(IGPSActivity main, Context context) {
        this.main=main;
        this.context=context;
        this.mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000); // Update location every second
        mLocationRequest.setFastestInterval(500);
                buildLocationSettingsRequest();
        //checkLocationSettings();
    }



    @Override
    public void onConnected(Bundle bundle) {

        Log.d(Config.log_id, "GPS Conneted google play service");
        lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    public void stopLocationUpdate() {
        try {
            handler.removeCallbacks(locationRunnable);
            //mGoogleApiClient.disconnect();;
            LocationServices.FusedLocationApi.removeLocationUpdates( mGoogleApiClient, this);
            this.isRunning = false;
        } catch (Exception e) {
            Log.e(Config.log_id,e.getMessage());
        }
    }
    public void startLocationUpdates() {

        if(mGoogleApiClient.isConnected())
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }else
        mGoogleApiClient.connect();

         this.isRunning = true;
        locationRunnable= new Runnable()
        {
            public void run()
            {
                if(lastKnownLocation==null)
                {
                    main.locationNotFound();

                }
                else
                {
                    main.locationChanged(lastKnownLocation);
                }
                stopLocationUpdate();
            }
        };
        handler.postDelayed(locationRunnable, 10000);
    }
    @Override
    public void onConnectionSuspended(int i) {

        Log.i(Config.log_id, "GoogleApiClient connection has been suspend");
    }

    @Override
    public void onLocationChanged(Location loc) {
        handler.removeCallbacks(locationRunnable);
        Log.d(Config.log_id, "Latitude: " + loc.getLatitude() + " long"
                + loc.getLongitude() + " acc" + loc.getAccuracy());
        main.locationChanged(loc);
        stopLocationUpdate();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.e(Config.log_id, "GoogleApiClient onConnectionFailed");
    }



    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }
    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }
    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(Config.log_id, "All location settings are satisfied.");
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(Config.log_id, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");

                try {
                      final int REQUEST_CHECK_SETTINGS = 0x1;
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                   status.startResolutionForResult((Activity)main, REQUEST_CHECK_SETTINGS);
                } catch (Exception e) {
                    Log.i(Config.log_id, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(Config.log_id, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

    public interface IGPSActivity {
        public void locationChanged(Location location);
        public void locationNotFound();

        public void displayGPSSettingsDialog();
    }


}
