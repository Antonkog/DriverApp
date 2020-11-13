package com.abona_erp.driverapp.ui.fmap

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.data.Constant
import com.abona_erp.driverapp.data.model.Address
import com.abona_erp.driverapp.databinding.MapFragmentBinding
import com.abona_erp.driverapp.ui.base.BaseFragment
import com.abona_erp.driverapp.ui.ftasks.DialogBuilder
import com.abona_erp.driverapp.ui.utils.JsonParser
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject


@AndroidEntryPoint
class MapFragment : BaseFragment() {

    val TAG = "MapFragment"

    private val mapViewModel by viewModels<MapViewModel>()
    private lateinit var mapBinding: MapFragmentBinding

    val args: MapFragmentArgs by navArgs()

    lateinit var googleMap : GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.map_fragment, container, false)

        mapBinding = MapFragmentBinding.bind(view).apply {
            viewmodel = mapViewModel
        }

        mapBinding.lifecycleOwner = this.viewLifecycleOwner

        setGoogleMaps(savedInstanceState)
        val data = args.mapData
        addTestRows(data)
        return view

    }


    private fun setGoogleMaps(savedInstanceState: Bundle?) {
        mapBinding.mapView.onCreate(savedInstanceState)
        mapBinding.mapView.onResume() // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(context)
        } catch (e: Exception) {
            e.printStackTrace()
        }


        mapBinding.mapView.getMapAsync { map ->
            googleMap = map

            if (checkLocationPermission()) googleMap.isMyLocationEnabled = true
            else requestPermissions(
                *arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                Constant.KEY_PERMISSION_LOCATION
            )

            val task = if (args.mapData.latitude != 0.0 && args.mapData.longitude != 0.0) LatLng(
                args.mapData.latitude,
                args.mapData.longitude
            )
            else LatLng(
                49.1249637,
                8.5503113
            )//office location, just for now, when data exist - need to seet


            googleMap.addMarker(
                MarkerOptions().position(task).title("Marker Title")
                    .snippet("Position from Abona Server")
            )

            // For zooming automatically to the location of the marker
            val cameraPosition = CameraPosition.Builder().target(task).zoom(16f).build()

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }

    private fun checkLocationPermission(): Boolean {
        // For showing a move to my location button
        return !(ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED)
    }

    override fun onResume() {
        super.onResume()
        mapBinding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapBinding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapBinding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapBinding.mapView.onLowMemory()
    }


    private fun addTestRows(address: Address) {
        val jsonObject = JSONObject(Gson().toJson(address).trim())


        val linearContent: LinearLayout = mapBinding.infoContainer

        val map: HashMap<String, String> = java.util.HashMap()
        JsonParser.parseJson(jsonObject, map)

        map.entries.forEach { entry ->
            run {
                val row = LayoutInflater.from(mapBinding.root.context)
                    .inflate(R.layout.parsed_json_row, null, false)
                row.findViewById<TextView>(R.id.txt_item_row).text = "${entry.key} ${entry.value}"
                row.findViewById<TextView>(R.id.txt_item_row).setTextColor(Color.WHITE)
                linearContent.addView(row)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val success = (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED)

        when (requestCode) {
            Constant.KEY_PERMISSION_LOCATION -> {
                Log.e(TAG, " KEY_PERMISSION_LOCATION")

                if (!success) context?.let {
                    DialogBuilder.getPermissionErrorDialog(it) { _, _ ->  openSettings() }.show()
                } else {
                    if(checkLocationPermission()) googleMap.isMyLocationEnabled = true
                }
            }
//            REQUEST_APP_SETTINGS->{}
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    /**
     * Navigating User to App Settings.
     */
    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", context?.packageName, null)
        intent.data = uri
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK
                or Intent.FLAG_ACTIVITY_CLEAR_TASK
                or Intent.FLAG_ACTIVITY_NO_HISTORY
                or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        this.startActivityForResult(intent, Constant.REQUEST_APP_SETTINGS)
    }
}