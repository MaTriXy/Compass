package com.james.compass.activities;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.james.compass.Compass;
import com.james.compass.R;
import com.james.compass.adapters.PlaceAdapter;
import com.james.compass.data.PlaceData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private Compass compass;
    private GoogleMap googleMap;

    private CoordinatorLayout coordinatorLayout;
    private SupportMapFragment mapFragment;

    private RecyclerView recyclerView;
    private PlaceAdapter placeAdapter;
    private ArrayList<PlaceData> places;

    private Location prevLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        compass = (Compass) getApplicationContext();

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        compass.googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        places = new ArrayList<>();
        placeAdapter = new PlaceAdapter(this, places);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.setAdapter(placeAdapter);
    }

    @Override
    protected void onStart() {
        compass.googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        compass.googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (compass.googleApiClient.isConnected()) startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocationServices.FusedLocationApi.removeLocationUpdates(compass.googleApiClient, this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        try {
            googleMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setMyLocationButtonEnabled(false);
        uiSettings.setTiltGesturesEnabled(false);
        uiSettings.setCompassEnabled(false);
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setZoomControlsEnabled(false);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        String errorMessage = connectionResult.getErrorMessage();
        if (errorMessage == null) errorMessage = String.valueOf(connectionResult.getErrorCode());

        Snackbar.make(coordinatorLayout, errorMessage, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (googleMap != null && (prevLocation == null || location.distanceTo(prevLocation) > 500)) {
            googleMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                            .target(new LatLng(location.getLatitude(), location.getLongitude()))
                            .bearing(location.getBearing())
                            .zoom(16)
                            .build()
                    )
            );

            PendingResult<PlaceLikelihoodBuffer> result;
            try {
                result = Places.PlaceDetectionApi.getCurrentPlace(compass.googleApiClient, null);
            } catch (SecurityException e) {
                e.printStackTrace();
                return;
            }

            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                @Override
                public void onResult(@NonNull PlaceLikelihoodBuffer likelyPlaces) {
                    ArrayList<PlaceLikelihood> likelihoods = new ArrayList<>();
                    for (PlaceLikelihood likelihood : likelyPlaces) {
                        likelihoods.add(likelihood);
                        googleMap.addMarker(new MarkerOptions().position(likelihood.getPlace().getLatLng()).title(likelihood.getPlace().getName().toString()).snippet(likelihood.getPlace().getAddress().toString()));
                    }

                    Collections.sort(likelihoods, new Comparator<PlaceLikelihood>() {
                        @Override
                        public int compare(PlaceLikelihood lhs, PlaceLikelihood rhs) {
                            return (int) (lhs.getLikelihood() - rhs.getLikelihood());
                        }
                    });

                    places.clear();
                    for (PlaceLikelihood likelihood : likelihoods) {
                        places.add(new PlaceData(likelihood.getPlace()));
                    }

                    placeAdapter.notifyDataSetChanged();
                    likelyPlaces.release();
                }
            });

            prevLocation = location;
        }
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest()
                .setInterval(10000)
                .setFastestInterval(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(compass.googleApiClient, locationRequest, this);
        } catch (SecurityException e) {
            e.printStackTrace();
            finish();
        }
    }
}
