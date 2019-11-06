package com.abona_erp.driver.app.ui.feature.main.fragment.map;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.ui.event.BackEvent;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapFragment extends Fragment
  implements OnMapReadyCallback {
  
  private static final String TAG = MapFragment.class.getCanonicalName();
  
  private GoogleMap mGoogleMap;
  private MarkerOptions mMarkerOptions;
  
  private MapView mMapView;
  private AppCompatImageButton mBtnBack;
  
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
    
    mMarkerOptions = new MarkerOptions();
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

  @Override
  public void onMapReady(GoogleMap googleMap) {
    MapsInitializer.initialize(getActivity());
    
    mGoogleMap = googleMap;
    mGoogleMap.clear();
    mGoogleMap.setMinZoomPreference(12);
    mGoogleMap.setIndoorEnabled(true);
  
    UiSettings uiSettings = mGoogleMap.getUiSettings();
    uiSettings.setIndoorLevelPickerEnabled(true);
    uiSettings.setMyLocationButtonEnabled(true);
    uiSettings.setMapToolbarEnabled(true);
    uiSettings.setCompassEnabled(true);
    uiSettings.setZoomControlsEnabled(true);

    LatLng marker = new LatLng(mLongitude, mLatitude);
    mMarkerOptions.position(marker).title(mName);
    mGoogleMap.addMarker(mMarkerOptions);
  
    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(marker));
    mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
  }
  
  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (mMapView != null) {
      mMapView.onSaveInstanceState(outState);
    }
  }
  
  @Override
  public void onResume() {
    super.onResume();
    if (mMapView != null) {
      mMapView.onResume();
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
