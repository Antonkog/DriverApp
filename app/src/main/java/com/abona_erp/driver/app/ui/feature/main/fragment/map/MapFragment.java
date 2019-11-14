package com.abona_erp.driver.app.ui.feature.main.fragment.map;

import android.Manifest;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.app.receiver.GeofenceBroadcastReceiver;
import com.abona_erp.driver.app.service.GeofenceService;
import com.abona_erp.driver.app.ui.event.BackEvent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapFragment extends Fragment implements
  GoogleApiClient.ConnectionCallbacks,
  GoogleApiClient.OnConnectionFailedListener,
  OnMapReadyCallback {
  
  private static final String TAG = MapFragment.class.getCanonicalName();
  
  public static final float GEOFENCE_RADIUS_IN_METERS = 100;

  private static final int REQUEST_LOCATION_PERMISSION_CODE = 101;

  private static final String GEOFENCE_REQ_ID = "Driver App Geofence";
/*
  private final int UPDATE_INTERVAL =  1000;
  private final int FASTEST_INTERVAL = 900;
*/
  private static final float GEOFENCE_RADIUS = 600.0f; // in meters
  private static final long GEO_DURATION = 60 * 60 * 1000;

  private GoogleMap mMap;
  GoogleApiClient   mGoogleApiClient;
  
  private Marker          mGeoFenceMarker;
  
  private PendingIntent mPendingIntent;

  private MapView mMapView;
  private AppCompatImageButton mBtnBack;
  
  private GeofencingRequest mGeofencingRequest;
  private MarkerOptions markerOptions;
  private Marker currentLocationMarker;
  private boolean isMonitoring = false;

  private double mLongitude;
  private double mLatitude;
  private String mName;

  public MapFragment() {
    // Required empty public constructor.
  }
  
  public static MapFragment newInstance() {
    return new MapFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    mGoogleApiClient = new GoogleApiClient.Builder(getContext())
      .addApi(LocationServices.API)
      .addConnectionCallbacks(this)
      .addOnConnectionFailedListener(this)
      .build();
  
    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION_CODE);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    
    View root = inflater.inflate(R.layout.activity_map_layout, container, false);
    initComponents(root);
    
    if (getArguments() != null) {
      mLongitude = getArguments().getDouble("longitude");
      mLatitude = getArguments().getDouble("latitude");
      mName = getArguments().getString("name");
    } else {
      mLongitude = 0D;
      mLatitude = 0D;
      mName = "UNKNOWN";
    }
  
    mMapView = (MapView)root.findViewById(R.id.map);
    
    return root;
  }
  
  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mMapView.onCreate(savedInstanceState);
    mMapView.getMapAsync(this);
  }

  private void startLocationMonitor() {
    Log.d(TAG, "Start location monitor");
    LocationRequest locationRequest = LocationRequest.create()
      .setInterval(2000)
      .setFastestInterval(1000)
      .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    
    try {
      LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
      
          if (currentLocationMarker != null) {
            currentLocationMarker.remove();
          }
          markerOptions = new MarkerOptions();
          markerOptions.position(new LatLng(location.getLatitude(), location.getLongitude()));
          markerOptions.title("Current Location");
          currentLocationMarker = mMap.addMarker(markerOptions);
          Log.d(TAG, "Location Change Lat Lng " + location.getLatitude() + " " + location.getLongitude());
        }
      });
    } catch (SecurityException e) {
      Log.d(TAG, e.getMessage());
    }
  }
  
  private void startGeofencing() {
    Log.d(TAG, "Start geofencing monitoring call");
    Log.d(TAG, "-------------------------------------------------------------------");
    mPendingIntent = getGeofencePendingIntent();
    mGeofencingRequest = new GeofencingRequest.Builder()
      .setInitialTrigger(Geofence.GEOFENCE_TRANSITION_ENTER)
      .addGeofence(getGeofence())
      .build();
    
    if (!mGoogleApiClient.isConnected()) {
      Log.d(TAG, "Google API client not connected");
    } else {
      try {
        LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, mGeofencingRequest, mPendingIntent).setResultCallback(new ResultCallback<Status>() {
          @Override
          public void onResult(@NonNull Status status) {
            if (status.isSuccess()) {
              Log.d(TAG, "Successfully Geofencing Connected");
            } else {
              Log.d(TAG, "Failed to add Geofencing " + status.getStatus());
            }
          }
        });
      } catch (SecurityException e) {
        Log.d(TAG, e.getMessage());
      }
    }
    isMonitoring = true;
  }
  
  @NonNull
  private Geofence getGeofence() {
    LatLng latLng = new LatLng(mLongitude, mLatitude);
    return new Geofence.Builder()
      .setRequestId(mName)
      .setExpirationDuration(Geofence.NEVER_EXPIRE)
      .setCircularRegion(latLng.latitude, latLng.longitude, GEOFENCE_RADIUS_IN_METERS)
      .setNotificationResponsiveness(1000)
      .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
      .build();
  }
  
  private PendingIntent getGeofencePendingIntent() {
    if (mPendingIntent != null) {
      return mPendingIntent;
    }
    Intent intent = new Intent(getContext(), GeofenceService.class);
    return PendingIntent.getService(getContext(), 0, intent, PendingIntent.
      FLAG_UPDATE_CURRENT);
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    MapsInitializer.initialize(getActivity());
  
    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      return;
    }
    
    mMap = googleMap;
    LatLng latLng = new LatLng(mLongitude, mLatitude);
    mMap.addMarker(new MarkerOptions().position(latLng).title(mName));
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f));
    
    mMap.setMyLocationEnabled(true);
    
    Circle circle = mMap.addCircle(new CircleOptions()
      .center(new LatLng(latLng.latitude, latLng.longitude))
      .radius(GEOFENCE_RADIUS_IN_METERS)
      .strokeColor(Color.RED)
      .strokeWidth(4f));
/*
    mLocationRequest = new LocationRequest();
    mLocationRequest.setInterval(1000);
    mLocationRequest.setFastestInterval(1000);
    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    
    mMap.clear();
    mMap.setMinZoomPreference(12);
    mMap.setIndoorEnabled(true);
  
    UiSettings uiSettings = mMap.getUiSettings();
    uiSettings.setIndoorLevelPickerEnabled(true);
    uiSettings.setMyLocationButtonEnabled(true);
    uiSettings.setMapToolbarEnabled(true);
    uiSettings.setCompassEnabled(true);
    uiSettings.setZoomControlsEnabled(true);

    LatLng marker = new LatLng(mLongitude, mLatitude);
    mMarkerOptions.position(marker).title(mName);
    mMap.addMarker(mMarkerOptions);
 
    mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));
    mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

    markerForGeofence(marker);
 */
  }

  @Override
  public void onConnected(@Nullable Bundle bundle) {
    Log.i(TAG, "***** Google Api Client Connected.");
    isMonitoring = true;
    startGeofencing();
    startLocationMonitor();
  }
  
  @Override
  public void onConnectionSuspended(int i) {
    Log.w(TAG, "Google Connection Suspended");
  }
  
  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    isMonitoring = false;
    Log.e(TAG, "Connection Failed: " + connectionResult.getErrorMessage());
  }

  private void markerForGeofence(LatLng latLng) {
    Log.i(TAG, "markerForGeofence(" + latLng + ")");
    String title = latLng.latitude + ", " + latLng.longitude;
    MarkerOptions markerOptions = new MarkerOptions()
      .position(latLng)
      .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
      .title(title);
    if (mMap != null) {
      if (mGeoFenceMarker != null) {
        mGeoFenceMarker.remove();
      }
      mGeoFenceMarker = mMap.addMarker(markerOptions);
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (mMapView != null) {
      mMapView.onSaveInstanceState(outState);
    }
  }
  
  @Override
  public void onStart() {
    super.onStart();
    mGoogleApiClient.reconnect();
  }
  
  @Override
  public void onResume() {
    super.onResume();
    if (mMapView != null) {
      mMapView.onResume();
    }
    int response = GoogleApiAvailability.getInstance()
      .isGooglePlayServicesAvailable(getContext());
    if (response != ConnectionResult.SUCCESS) {
      Log.d(TAG, "Google Play Service Not Available");
      GoogleApiAvailability.getInstance()
        .getErrorDialog(getActivity(), response, 1).show();
    } else {
      Log.d(TAG, "Google play service available");
    }
  }
  
  @Override
  public void onPause() {
    super.onPause();
    if (mMapView != null) {
      mMapView.onPause();
    }
  }
  
  @Override
  public void onStop() {
    super.onStop();
    mGoogleApiClient.disconnect();
  }
  
  @Override
  public void onDestroy() {
    if (mMapView != null) {
      try {
        mMapView.onDestroy();
      } catch (NullPointerException e) {
        Log.e(TAG, "Error while attempting MapView.onDestroy(), ignoring exception", e);
      }
    }
    super.onDestroy();
  }
 
  @Override
  public void onLowMemory() {
    super.onLowMemory();
    if (mMapView != null) {
      mMapView.onLowMemory();
    }
  }
  
  private void initComponents(@NonNull View root) {
    mBtnBack = (AppCompatImageButton)root.findViewById(R.id.btn_map_back);
    mBtnBack.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        App.eventBus.post(new BackEvent());
      }
    });
  }
}
