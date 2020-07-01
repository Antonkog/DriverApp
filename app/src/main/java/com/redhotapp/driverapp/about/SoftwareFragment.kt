package com.redhotapp.driverapp.about

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.redhotapp.driverapp.R

class SoftwareFragment : Fragment() {

    companion object {
        fun newInstance() = SoftwareFragment()
    }

    private lateinit var viewModel: SoftwareViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_software, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SoftwareViewModel::class.java)
        // TODO: Use the ViewModel
    }

}