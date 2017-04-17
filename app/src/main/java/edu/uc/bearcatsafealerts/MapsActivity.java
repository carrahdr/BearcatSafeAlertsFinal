package edu.uc.bearcatsafealerts;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;

import java.io.IOException;
import java.util.List;

// This class is a fairly standard implementation of the Google Maps API
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Marker mCurrLocationMarker;
    private Location mLastLocation;
    private MySingleton mMySingleton;
    private List<String[]> mAlertList;
    private int goToAlert = 0;
    private int setLocalToUC = 0;

    // On creation, set up the map
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * This code comes directly from the sample code online
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker at our current location, or the location of the crime alert selected.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        addAlertsToMap();
    }

    // On location change, update the map with our current location.  Use a magenta marker
    @Override
    public void onLocationChanged(Location location) {
        // If the setLocalToUC flag is set to 0, use current location of user.  Otherwise,
        // the application will set the current location to the UC campus (for demo purposes)
        if(setLocalToUC==0) {
            mLastLocation = location;
            if (mCurrLocationMarker != null) {
                mCurrLocationMarker.remove();
            }

            //Place current location marker
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            mCurrLocationMarker = mMap.addMarker(markerOptions);

            //move map camera to current location
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

            //stop location updates
            if (mGoogleApiClient != null) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);
            }
        }
    }

    // This method adds the crime alert locations to the map
    public void addAlertsToMap()
    {
        // Get the geocoder to map addresses to lat/long, and a pointer to the singleton object with our alert list
        Geocoder geocoder = new Geocoder(getBaseContext());
        List<Address> nxtAddr = null;
        int nNumAlerts = mMySingleton.getmCount();
        mAlertList = mMySingleton.mCrimeList;
        String mCrime = mMySingleton.getmCrime();

        // If the setLocal flag is on, fake our position as being on the UC campus
        if(setLocalToUC==1)fakeMyPosition();

        // Step through the alerts one at a time, placing a marker with the crime for each alert
        for(int i = 0; i<nNumAlerts; i++) {
            String[] nxtAlert = mAlertList.get(i + 1);
            int nNumFields = nxtAlert.length;
            // Verify that this is a valid alert with at least 6 columns of data
            if (nNumFields > 6) {
                // Get the name of the crime and the address of the crime
                String thisCrime = "Crime: " + nxtAlert[4];
                // Since the crime log only provides a street address, append Cinci OH to make sure we get the right location
                String nxtAddress = nxtAlert[5] + ", Cincinnati, OH";
                try {
                    // We only retrieve the Top 1 result
                    nxtAddr = geocoder.getFromLocationName(nxtAddress, 1);
                    Address addr = nxtAddr.get(0);
                    double latitude = addr.getLatitude();
                    double longitude = addr.getLongitude();
                    //Place crime location marker with our crime name and a yellow marker
                    LatLng latLng = new LatLng(addr.getLatitude(), addr.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title(nxtAlert[4]);
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                    mCurrLocationMarker = mMap.addMarker(markerOptions);
                    // if this crime is the one selected from the Crime Log page, center the map on this marker
                    if(thisCrime.equals(mCrime)){
                        //move map camera
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                    }
                    // We don't really handle the exceptions at this time other than to keep them from crashing the app
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    // This method handles getting our location when the phone connects
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    // This method fakes our current position as UC campus, if desired for demo purposes
    private void fakeMyPosition(){
        // Get a geocoder and feed it the UC address
        Geocoder geocoder = new Geocoder(getBaseContext());
        List<Address> nxtAddr = null;
        String nxtAddress = "University of Cincinnati, Cincinnati, OH";
        String mCrime = mMySingleton.getmCrime();
        try {
            // Only get the top 1 result
            nxtAddr = geocoder.getFromLocationName(nxtAddress, 1);
            System.out.println(nxtAddr);
            Address addr = nxtAddr.get(0);
            double latitude = addr.getLatitude();
            double longitude = addr.getLongitude(); // DO SOMETHING WITH
            //Place our location marker
            LatLng latLng = new LatLng(addr.getLatitude(), addr.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Location");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            mCurrLocationMarker = mMap.addMarker(markerOptions);
            // if no specific crime was selected from the crime log page, center the map on our location
            if(mCrime==null||mCrime.isEmpty()){
                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // Required method to use the Google api client for maps
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }
}
