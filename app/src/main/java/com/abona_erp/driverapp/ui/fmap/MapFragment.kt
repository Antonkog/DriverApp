package com.abona_erp.driverapp.ui.fmap

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.data.model.Address
import com.abona_erp.driverapp.databinding.MapFragmentBinding
import com.abona_erp.driverapp.ui.base.BaseFragment
import com.abona_erp.driverapp.ui.utils.JsonParser
import com.google.android.gms.maps.*
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
       val data =  args.mapData
        addTestRows(data)
        return view

    }

    private fun setGoogleMaps(savedInstanceState: Bundle?) {
        mapBinding.mapView.onCreate(savedInstanceState)
        mapBinding.mapView.onResume() // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(activity?.applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }


        mapBinding.mapView.getMapAsync {
            googleMap->

            // For showing a move to my location button
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(requireContext(), resources.getString(R.string.need_permissions_message) + " to use current location", Toast.LENGTH_SHORT).show()
            } else{
                googleMap.isMyLocationEnabled = true
            }


            // For dropping a marker at a point on the Map
            val current = if(args.mapData.latitude != 0.0 && args.mapData.longitude!= 0.0) LatLng(args.mapData.latitude, args.mapData.longitude)
            else LatLng(49.1249637,8.5503113)//office location, just for now, when data exist - need to seet


            googleMap.addMarker(
                MarkerOptions().position(current).title("Marker Title")
                    .snippet("Position from Abona Server")
            )

            // For zooming automatically to the location of the marker
            val cameraPosition = CameraPosition.Builder().target(current).zoom(16f).build()
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
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
                linearContent.addView(row)
            }
        }
    }


}