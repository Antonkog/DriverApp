package com.abona_erp.driverapp.ui.fmap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.databinding.MapFragmentBinding
import com.abona_erp.driverapp.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapFragment : BaseFragment() {

    val TAG = "MapFragment"

    private val mapViewModel by viewModels<MapViewModel>()
    private lateinit var mapBinding: MapFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.map_fragment, container, false)
        mapBinding = MapFragmentBinding.bind(view).apply {
            viewmodel = mapViewModel
        }

        mapBinding.lifecycleOwner = this.viewLifecycleOwner

        return view

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

}