package com.bsalponia.mainactivity.uiController;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bsalponia.mainactivity.R;
import com.bsalponia.mainactivity.viewmodel.LocationViewModel;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnCameraIdleListener, OnMapReadyCallback {

    private static final String TAG= MapsActivity.class.getSimpleName();

    //gps stuff
    private static final int REQUEST_GPS_SETTINGS= 1337;
    private boolean isGPSEnabled= false;

    //current location stuff
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private static final int REQUEST_FINE_LOCATION_REQUEST_CODE = 1414;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1299;

    private static final String LOCATION_STRING= "Identifying Location...";

    private boolean isCameraScroll;     //by the user
    private boolean isFirstLaunch= true;

    private GoogleMap mMap;
    private LatLng currentLocation= null;
    private LatLng selectedLatLng= null;

    private TextView txtLocation;

    private LocationViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //user location
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Log.i(TAG, "in the onLocationResult lat: "+location.getLatitude()+" long: "+ location.getLongitude());
                }
            }
        };

        if(checkPermissions()){             /*has permission*/
            createLocationRequest();
        }else{
            requestPermissions();
        }

        setListeners();
    }

    private void setListeners(){

        txtLocation= findViewById(R.id.txtLocation);        /*place autocomplete activity*/
        txtLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(MapsActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });

        LinearLayout linearCurrent= findViewById(R.id.linearCurrent);
        linearCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(currentLocation!=null){
                    isCameraScroll= false;
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(currentLocation));

                    getAddress(true);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isCameraScroll= true;
                        }
                    }, 2500);
                }
            }
        });

        viewModel= ViewModelProviders.of(this).get(LocationViewModel.class);
        Button btnSave= findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!txtLocation.getText().toString().equalsIgnoreCase(LOCATION_STRING) &&
                        selectedLatLng!=null){
                    Log.i(TAG, "saved lat long: "+ selectedLatLng.latitude+ "  "+ selectedLatLng.longitude);
                    Toast.makeText(v.getContext(), "Location saved.", Toast.LENGTH_SHORT).show();
                    com.bsalponia.mainactivity.repository.Location location= new com.bsalponia.mainactivity.repository.Location();
                    location.setLatitude(selectedLatLng.latitude);
                    location.setLongitude(selectedLatLng.longitude);
                    location.setName(txtLocation.getText().toString());
                    location.setTimeStamp(String.valueOf(System.currentTimeMillis()));
                    viewModel.insert(location);
                }else{
                    Toast.makeText(v.getContext(), "Failed to save location.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button btnSaved= findViewById(R.id.btnSaved);
        btnSaved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), LocationListActivity.class));
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(isGPSEnabled)
            mMap.setMyLocationEnabled(false);

        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraMoveCanceledListener(this);

    }

    @SuppressWarnings("MissingPermission")
    protected void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(30000);
        mLocationRequest.setFastestInterval(15000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                Log.i(TAG, "in the createLocationRequest success");
                isGPSEnabled= true;
                getLastLocation();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {

                    Log.i(TAG, "in the createLocationRequest failure");
                    // RouteLocation settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MapsActivity.this,
                                REQUEST_GPS_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    @SuppressWarnings("MissingPermission")
    private void startLocationUpdates() {
        if(isGPSEnabled){
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(30000);
            mLocationRequest.setFastestInterval(15000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, null /* Looper */);
        }
    }

    private void stopLocationUpdates() {
        if(isGPSEnabled)
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    /*getting address from lat lng from geocoder api*/
    private void getAddress(boolean isCurrent){
        Log.i(TAG, "in getAddress");

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        selectedLatLng= new LatLng(isCurrent?currentLocation.latitude:mMap.getCameraPosition().target.latitude,
                isCurrent?currentLocation.longitude:mMap.getCameraPosition().target.longitude);

        Location selectedLocation= new Location("");
        selectedLocation.setLatitude(selectedLatLng.latitude);
        selectedLocation.setLongitude(selectedLatLng.longitude);

        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(selectedLatLng.latitude, selectedLatLng.longitude,
                    // In this sample, get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            Log.e(TAG, "address ioException");
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            Log.e(TAG, "address IllegalArgumentException");
        }
        // Handle case where no address was found.
        if (addresses == null ||
                addresses.size()  == 0) {
            Log.e(TAG, "address- no address found");
            txtLocation.setText("Invalid Address, Tap to Enter Manually.");
        } else {
            Log.i(TAG, "address- address found");
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<>();
            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                Log.i(TAG, "address---->: "+ address.getAddressLine(i));
                addressFragments.add(address.getAddressLine(i));
            }
            txtLocation.setText(""+addressFragments.get(0));
        }
    }

    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        Log.i(TAG, "on last location");
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            isCameraScroll= false;

                            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(currentLocation));

                            if(isFirstLaunch){
                                isFirstLaunch= false;
                                getAddress(true);
                            }

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    isCameraScroll= true;
                                }
                            }, 2500);

                            Log.i(TAG, "in getLastLocation lat :" + location.getLatitude()+ " "+ location.getLongitude());
                        }else{
                            Log.i(TAG, "in getLastLocation failure");
                        }
                    }
                });
    }

    @Override
    public void onCameraMoveStarted(int reason) {

        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            Log.i(TAG, "The user gestured on the map.");
            if(isCameraScroll){
                Log.i(TAG, "The user gestured on the map isCameraScroll.");
                txtLocation.setText(LOCATION_STRING);
            }
        } else if (reason == GoogleMap.OnCameraMoveStartedListener
                .REASON_API_ANIMATION) {
            Log.i(TAG, "The user tapped something on the map.");
        } else if (reason == GoogleMap.OnCameraMoveStartedListener
                .REASON_DEVELOPER_ANIMATION) {
            Log.i(TAG, "The app moved the camera.");
        }
    }

    @Override
    public void onCameraMove() {
//        Log.i(TAG, "The camera is moving.");
    }

    @Override
    public void onCameraMoveCanceled() {
//        Log.i(TAG, "Camera movement canceled.");
    }

    @Override
    public void onCameraIdle() {
        Log.i(TAG, "The camera has stopped moving.");
        if(isCameraScroll){
            Log.i(TAG, "The camera has stopped moving isCameraScroll.");
            getAddress(false);
        }
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
//            startLocationPermissionRequest(); /* I want the user to not enter the app unless he turn's on the gps*/
        }
        startLocationPermissionRequest();
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(MapsActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_FINE_LOCATION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_GPS_SETTINGS) {
            Log.i(TAG, "in onActivityResult");
            if(resultCode==RESULT_OK){
                new CountDownTimer(1000, 500) {
                    public void onTick(long millisUntilFinished) {}
                    public void onFinish() {
                        Log.i(TAG, "on finish");
                        if (!checkPermissions()) {
                            requestPermissions();
                        }else {
                            isGPSEnabled= true;
                            getLastLocation();
                        }
                    }
                }.start();
            }
        }else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);

                LatLng latLng= place.getLatLng();
                isCameraScroll= false;
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isCameraScroll= true;
                    }
                }, 2500);

                selectedLatLng= new LatLng(latLng.latitude, latLng.longitude);
                Location selectedLocation= new Location("");
                selectedLocation.setLatitude(selectedLatLng.latitude);
                selectedLocation.setLongitude(selectedLatLng.longitude);

                txtLocation.setText(""+place.getAddress());

                Log.i(TAG, "place picker RESULT_OK "+ " Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());
                Log.i(TAG, "place picker RESULT_ERROR");

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                Log.i(TAG, "place picker RESULT_CANCELED");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_FINE_LOCATION_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                new CountDownTimer(1000, 500) {
                    public void onTick(long millisUntilFinished) {}
                    public void onFinish() {
                        Log.i(TAG, "on finish");
                        createLocationRequest();
                    }
                }.start();
            } else {
                // Permission denied.
                Log.i(TAG, "on permission denied");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }
}
