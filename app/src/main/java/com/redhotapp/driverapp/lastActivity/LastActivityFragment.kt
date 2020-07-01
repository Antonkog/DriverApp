package com.redhotapp.driverapp.lastActivity

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.redhotapp.driverapp.R

class LastActivityFragment : Fragment() {

    companion object {
        fun newInstance() =
            LastActivityFragment()
    }

    private lateinit var viewModel: LastActivityViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_last_activity, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LastActivityViewModel::class.java)
        // TODO: Use the ViewModel
    }

}