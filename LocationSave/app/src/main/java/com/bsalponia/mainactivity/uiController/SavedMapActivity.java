package com.bsalponia.mainactivity.uiController;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.bsalponia.mainactivity.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SavedMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG= SavedMapActivity.class.getSimpleName();
    public static final String LATITUDE= TAG+".latitude";
    public static final String LONGITUDE= TAG+".longitude";
    public static final String ADDRESS= TAG+".address";

    private GoogleMap mMap;

    private LatLng latLng= null;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle bundle= getIntent().getExtras();
        if(bundle!=null &&
                bundle.containsKey(ADDRESS)){
            address= bundle.getString(ADDRESS);
            latLng= new LatLng(bundle.getDouble(LATITUDE), bundle.getDouble(LONGITUDE));
        }

        TextView txtAddress= findViewById(R.id.txtAddress);
        txtAddress.setText(address);
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.addMarker(new MarkerOptions().position(latLng)/*.title("Marker")*/);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }
}
