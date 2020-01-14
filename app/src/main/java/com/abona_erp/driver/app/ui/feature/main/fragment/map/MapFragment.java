package com.abona_erp.driver.app.ui.feature.main.fragment.map;

import android.Manifest;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.app.ui.event.BackEvent;
import com.abona_erp.driver.app.ui.feature.main.fragment.map.impl.DirectionsJSONParser;
import com.abona_erp.driver.app.ui.widget.AsapTextView;
import com.abona_erp.driver.core.base.ContextUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

public class MapFragment extends Fragment implements OnMapReadyCallback {
  
  private static final String TAG = MapFragment.class.getCanonicalName();
  
  public static final float GEOFENCE_RADIUS_IN_METERS = 100;

  private static final int REQUEST_LOCATION_PERMISSION_CODE = 101;

  private GoogleMap mMap;
  
  private Marker          mGeoFenceMarker;
  
  private PendingIntent mPendingIntent;

  private MapView mMapView;
  private AppCompatImageButton mBtnBack;
  
  private GeofencingRequest mGeofencingRequest;
  private MarkerOptions markerOptions;
  private Marker currentLocationMarker;
  
  // -----------------------------------------------------------------------------------------------
  
  private LocationManager mLocationManager;
  private LocationListener mLocationListener;
  private MarkerOptions mMarkerOptions;
  private LatLng mOrigin;
  private LatLng mDestination;
  private Polyline mPolyline;
  
  ArrayList<LatLng> mMarkerPoints;

  // -----------------------------------------------------------------------------------------------
  
  private double mLongitude;
  private double mLatitude;
  private String mName;
  
  private AsapTextView tv_current_location_lat;
  private AsapTextView tv_current_location_lng;
  private AsapTextView tv_current_location_alt;
  private AsapTextView tv_current_location_speed;
  private AsapTextView tv_destination_location_lat;
  private AsapTextView tv_destination_location_lng;
  
  // -----------------------------------------------------------------------------------------------

  public MapFragment() {
    // Required empty public constructor.
  }
  
  public static MapFragment newInstance() {
    return new MapFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mMarkerPoints = new ArrayList<>();
    
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
    tv_destination_location_lat.setText(String.valueOf(mLatitude));
    tv_destination_location_lng.setText(String.valueOf(mLongitude));
    
    return root;
  }
  
  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mMapView.onCreate(savedInstanceState);
    mMapView.getMapAsync(this);
  }

/*
  @Override
  public void onMapReady(GoogleMap googleMap) {
    MapsInitializer.initialize(getActivity());
    mMap = googleMap;
    getMyLocation();
 
    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      return;
    }
    
    mMap = googleMap;
    LatLng latLng = new LatLng(mLatitude, mLongitude);
    mMap.addMarker(new MarkerOptions().position(latLng).title(mName));
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
    
    mMap.setMyLocationEnabled(true);
    
    Circle circle = mMap.addCircle(new CircleOptions()
      .center(new LatLng(latLng.latitude, latLng.longitude))
      .radius(GEOFENCE_RADIUS_IN_METERS)
      .strokeColor(Color.RED)
      .strokeWidth(4f));
      
  */
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
  //}
  
  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;
    
    // ---------------------------------------------------------------------------------------------
    // SET TRAFFIC ENABLED:
    // ---------------------------------------------------------------------------------------------
    mMap.setTrafficEnabled(true);
    
    // ---------------------------------------------------------------------------------------------
    // GET MY CURRENT LOCATION:
    // ---------------------------------------------------------------------------------------------
    mMap.setMyLocationEnabled(true);
    mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
      @Override
      public void onMyLocationChange(Location location) {
        Log.i(TAG, "Lat: " + location.getLatitude() + " - Lng: " + location.getLongitude());
        tv_current_location_lat.setText(String.valueOf(location.getLatitude()));
        tv_current_location_lng.setText(String.valueOf(location.getLongitude()));
        if (location.hasAltitude()) {
          tv_current_location_alt.setText(String.valueOf(Math.round(location.getAltitude())));
        } else {
          tv_current_location_alt.setText("---");
        }
        if (location.hasSpeed()) {
          DecimalFormat f;
          float currentSpeed = location.getSpeed() * 3.6f;
          if (currentSpeed < 10f) {
            f = new DecimalFormat("#0.00");
          } else if (currentSpeed > 100f) {
            f = new DecimalFormat("#0");
          } else {
            f = new DecimalFormat("#0.0");
          }
          
          tv_current_location_speed.setText(f.format(currentSpeed));
        } else {
          tv_current_location_speed.setText("--");
        }
      }
    });
    
    // ---------------------------------------------------------------------------------------------
    // SET DESTINATION MARKER:
    // ---------------------------------------------------------------------------------------------
    LatLng latLng = new LatLng(mLatitude, mLongitude);
    mMap.addMarker(new MarkerOptions().position(latLng).title(mName));
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8f));
    
    // ---------------------------------------------------------------------------------------------
    // SET GEOFENCING CIRCLE:
    // ---------------------------------------------------------------------------------------------
    Circle circle = mMap.addCircle(new CircleOptions()
      .center(new LatLng(latLng.latitude, latLng.longitude))
      .radius(GEOFENCE_RADIUS_IN_METERS)
      .strokeColor(Color.RED)
      .strokeWidth(4f));
    /*
    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
      @Override
      public void onMapClick(LatLng point) {
        // Already two locations
        if(mMarkerPoints.size()>1){
          mMarkerPoints.clear();
          mMap.clear();
        }
        
        // Adding new item to the ArrayList
        mMarkerPoints.add(point);
        
        // Creating MarkerOptions
        MarkerOptions options = new MarkerOptions();
        
        // Setting the position of the marker
        options.position(point);
        
      
        if(mMarkerPoints.size()==1){
          options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        }else if(mMarkerPoints.size()==2){
          options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }
        
        // Add new marker to the Google Map Android API V2
        mMap.addMarker(options);
        
        // Checks, whether start and end locations are captured
        if(mMarkerPoints.size() >= 2){
          mOrigin = mMarkerPoints.get(0);
          mDestination = mMarkerPoints.get(1);
          drawRoute();
        }
        
      }
    });*/
  }
  
  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                         int[] grantResults) {
    
    if (requestCode == 100){
      if (!verifyAllPermissions(grantResults)) {
        Toast.makeText(ContextUtils.getApplicationContext(),"No sufficient permissions",Toast.LENGTH_LONG).show();
      }else{
        getMyLocation();
      }
    }else {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
    
    tv_current_location_lat = (AsapTextView)root.findViewById(R.id.tv_current_location_lat);
    tv_current_location_lng = (AsapTextView)root.findViewById(R.id.tv_current_location_lng);
    tv_current_location_alt = (AsapTextView)root.findViewById(R.id.tv_current_location_alt);
    tv_current_location_speed = (AsapTextView)root.findViewById(R.id.tv_current_location_speed);
    tv_destination_location_lat = (AsapTextView)root.findViewById(R.id.tv_destination_location_lat);
    tv_destination_location_lng = (AsapTextView)root.findViewById(R.id.tv_destination_location_lng);
  }
  
  private boolean verifyAllPermissions(int[] grantResults) {
    
    for (int result : grantResults) {
      if (result != PackageManager.PERMISSION_GRANTED) {
        return false;
      }
    }
    return true;
  }
  
  private void getMyLocation(){
    
    // Getting LocationManager object from System Service LOCATION_SERVICE
    mLocationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
    
    mLocationListener = new LocationListener() {
      @Override
      public void onLocationChanged(Location location) {
        mOrigin = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mOrigin,12));
        if(mOrigin != null && mDestination != null)
          drawRoute();
      }
      
      @Override
      public void onStatusChanged(String provider, int status, Bundle extras) {
      
      }
      
      @Override
      public void onProviderEnabled(String provider) {
      
      }
      
      @Override
      public void onProviderDisabled(String provider) {
      
      }
    };
    
    int currentApiVersion = Build.VERSION.SDK_INT;
    if (currentApiVersion >= Build.VERSION_CODES.M) {

      if (getContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_DENIED) {

        mMap.setMyLocationEnabled(true);

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,10000,0,mLocationListener);
        
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
          @Override
          public void onMapLongClick(LatLng latLng) {

            mDestination = latLng;
            Log.d(TAG, "DESTINATION " + mDestination.toString());
            Log.d(TAG, "ORIGIN " + mOrigin.toString());
            mMap.clear();
            mMarkerOptions = new MarkerOptions().position(mDestination).title("Destination");
            mMap.addMarker(mMarkerOptions);
            if(mOrigin != null && mDestination != null)
              drawRoute();

          }
        });

      }else{
        requestPermissions(new String[]{
          android.Manifest.permission.ACCESS_FINE_LOCATION
        },100);
      }
   

    }
  }
  
  private void drawRoute(){
    
    // Getting URL to the Google Directions API
    String url = getDirectionsUrl(mOrigin, mDestination);
    
    DownloadTask downloadTask = new DownloadTask();
    
    // Start downloading json data from Google Directions API
    downloadTask.execute(url);
  }
  
  private String getDirectionsUrl(LatLng origin,LatLng dest){
    
    // Origin of route
    String str_origin = "origin="+origin.latitude+","+origin.longitude;
    
    // Destination of route
    String str_dest = "destination="+dest.latitude+","+dest.longitude;
    
    // Key
    String key = "key=" + getString(R.string.google_maps_api_key);
    
    // Building the parameters to the web service
    String parameters = str_origin+"&"+str_dest+"&"+key;
    
    // Output format
    String output = "json";
    
    // Building the url to the web service
    String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
    
    return url;
  }
  
  /** A method to download json data from url */
  private String downloadUrl(String strUrl) throws IOException {
    String data = "";
    InputStream iStream = null;
    HttpURLConnection urlConnection = null;
    try{
      URL url = new URL(strUrl);
      
      // Creating an http connection to communicate with url
      urlConnection = (HttpURLConnection) url.openConnection();
      
      // Connecting to url
      urlConnection.connect();
      
      // Reading data from url
      iStream = urlConnection.getInputStream();
      
      BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
      
      StringBuffer sb  = new StringBuffer();
      
      String line = "";
      while( ( line = br.readLine())  != null){
        sb.append(line);
      }
      
      data = sb.toString();
      
      br.close();
      
    }catch(Exception e){
      Log.d("Exception on download", e.toString());
    }finally{
      iStream.close();
      urlConnection.disconnect();
    }
    return data;
  }
  
  /** A class to download data from Google Directions URL */
  private class DownloadTask extends AsyncTask<String, Void, String> {
  
    // Downloading data in non-ui thread
    @Override
    protected String doInBackground(String... url) {
    
      // For storing data from web service
      String data = "";
    
      try{
        // Fetching the data from web service
        data = downloadUrl(url[0]);
        Log.d("DownloadTask","DownloadTask : " + data);
      }catch(Exception e){
        Log.d("Background Task",e.toString());
      }
      return data;
    }
  
    // Executes in UI thread, after the execution of
    // doInBackground()
    @Override
    protected void onPostExecute(String result) {
      super.onPostExecute(result);
    
      ParserTask parserTask = new ParserTask();
    
      // Invokes the thread for parsing the JSON data
      parserTask.execute(result);
    }
  }
  
  /** A class to parse the Google Directions in JSON format */
  private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
    
    // Parsing the data in non-ui thread.
    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
  
      JSONObject jObject;
      List<List<HashMap<String, String>>> routes = null;
      
      try {
        jObject = new JSONObject(jsonData[0]);
        DirectionsJSONParser parser = new DirectionsJSONParser();
        
        // Starts parsing data:
        routes = parser.parse(jObject);
      } catch (Exception e) {
        e.printStackTrace();
      }
      return routes;
    }
  
    // Executes in UI thread, after the parsing process
    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
      ArrayList<LatLng> points = null;
      PolylineOptions lineOptions = null;
  
      // Traversing through all the routes
      for(int i=0;i<result.size();i++){
        points = new ArrayList<LatLng>();
        lineOptions = new PolylineOptions();
    
        // Fetching i-th route
        List<HashMap<String, String>> path = result.get(i);
    
        // Fetching all the points in i-th route
        for(int j=0;j<path.size();j++){
          HashMap<String,String> point = path.get(j);
      
          double lat = Double.parseDouble(point.get("lat"));
          double lng = Double.parseDouble(point.get("lng"));
          LatLng position = new LatLng(lat, lng);
      
          points.add(position);
        }
    
        // Adding all the points in the route to LineOptions
        lineOptions.addAll(points);
        lineOptions.width(8);
        lineOptions.color(Color.RED);
      }
  
      // Drawing polyline in the Google Map for the i-th route
      if(lineOptions != null) {
        if(mPolyline != null){
          mPolyline.remove();
        }
        mPolyline = mMap.addPolyline(lineOptions);
    
      }else
        Toast.makeText(ContextUtils.getApplicationContext(),
          "No route is found", Toast.LENGTH_LONG).show();
    }
  }
}
