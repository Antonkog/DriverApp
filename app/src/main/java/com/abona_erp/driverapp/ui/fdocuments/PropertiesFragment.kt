package com.abona_erp.driverapp.ui.fdocuments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.databinding.FragmentBottomPropertiesDialogBinding
import com.abona_erp.driverapp.ui.utils.adapter.LazyAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.abona_erp.driverapp.ui.utils.adapter.initWithLinLay
import java.util.ArrayList

class PropertiesFragment : BottomSheetDialogFragment(), OnSeekBarChangeListener,
    LazyAdapter.OnItemClickListener<Int> {
    private var properties: PropertiesListener? = null
    private lateinit var propertiesDialogBinding: FragmentBottomPropertiesDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bottom_properties_dialog, container, false)
        propertiesDialogBinding = FragmentBottomPropertiesDialogBinding.bind(view)
        propertiesDialogBinding.lifecycleOwner = this.viewLifecycleOwner
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        propertiesDialogBinding.opacity.setOnSeekBarChangeListener(this)
        propertiesDialogBinding.brush.setOnSeekBarChangeListener(this)
        val layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        propertiesDialogBinding.rvColors.layoutManager = layoutManager
        propertiesDialogBinding.rvColors.setHasFixedSize(true)
        val colorPickerAdapter = ColorPickerAdapter(this)
        propertiesDialogBinding.rvColors.initWithLinLay(
            LinearLayoutManager.HORIZONTAL,
            colorPickerAdapter,
            DocumentEditDataOption.getDefaultColors(requireContext())
        )
    }

    fun setPropertiesChangeListener(properties: PropertiesListener?) {
        this.properties = properties
    }

    override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
        when (seekBar.id) {
            R.id.opacity -> if (properties != null) {
                properties!!.onOpacityChanged(i)
            }
            R.id.brush -> if (properties != null) {
                properties!!.onBrushSizeChanged(i)
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}
    override fun onStopTrackingTouch(seekBar: SeekBar) {}

    override fun onLazyItemClick(data: Int) {
        if (properties != null) {
            dismiss()
            properties!!.onColorChanged(data)
        }
    }
}