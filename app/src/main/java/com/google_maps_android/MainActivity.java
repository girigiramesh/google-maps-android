package com.google_maps_android;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    public static final int REQUEST_PERMISSION_LOCATION = 1;
    public static String[] PERMISSIONS_LOCATION = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};
    private GoogleApiClient mGoogleApiClient;
    private TextView tv_location_info, tv_location_updates_info;
    private Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_location_info = (TextView) findViewById(R.id.tv_location_info);
        tv_location_updates_info = (TextView) findViewById(R.id.tv_location_updates_info);
        if (!checkLocationPermission())
            requestPermission(PERMISSIONS_LOCATION, REQUEST_PERMISSION_LOCATION);
        buildGoogleApiClient();

        findViewById(R.id.btn_map).setOnClickListener(this::onClick);
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showToast(getString(R.string.toast_location_permission_thanks));
                } else {
                    showToast(getString(R.string.toast_location_permission_alert));
                }
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (checkLocationPermission()) {
            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLocation != null) {
                tv_location_info.setText(String.valueOf(mLocation.getLatitude()) + " " + String.valueOf(mLocation.getLongitude()));
            }
            getLocationUpdates();
        } else {
            requestPermission(PERMISSIONS_LOCATION, REQUEST_PERMISSION_LOCATION);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (checkLocationPermission()) {
            Location mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLocation != null) {
                tv_location_updates_info.setText(String.valueOf(mLocation.getLatitude()) + " " + String.valueOf(mLocation.getLongitude()));
            }
        } else {
            requestPermission(PERMISSIONS_LOCATION, REQUEST_PERMISSION_LOCATION);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        showToast(getString(R.string.toast_location_failed));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        showToast(getString(R.string.toast_location_failed));
    }

    /**
     * Checks whether the location permission granted or not
     *
     * @return
     */
    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * creating google api client
     */
    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * creating location changed request
     */
    private void getLocationUpdates() {
        if (checkLocationPermission()) {
            LocationRequest mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(1000);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            requestPermission(PERMISSIONS_LOCATION, REQUEST_PERMISSION_LOCATION);
        }
    }

    /**
     * Requests permission
     *
     * @param permissions
     * @param requestCode
     */
    private void requestPermission(String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(this, permissions, requestCode);
    }

    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void onClick(View view) {
        MapsActivity.start(this, Double.parseDouble(String.valueOf(mLocation.getLatitude())), Double.parseDouble(String.valueOf(mLocation.getLongitude())));
    }
}
