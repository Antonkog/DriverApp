package com.abona_erp.driverapp.ui.utils

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.TextView
import com.abona_erp.driverapp.R

/**
 * Created by Anton Kogan email: Akogan777@gmail.com on 11/17/2020
 */

class RangedTimePicker @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr){

    private var velocity = 1
    private val minValues = arrayListOf<String>()
    var hoursPick : NumberPicker
    var minPick : NumberPicker

    init {
        val root =  inflate(context, R.layout.ranged_picker, this)
        hoursPick  =  root.findViewById(R.id.hours_picker)
        minPick  =  root.findViewById(R.id.minutes_picker)

        hoursPick.maxValue = HOURS_MAX -1
        hoursPick.minValue = 0
        hoursPick.wrapSelectorWheel = true

        minPick.maxValue = MINUTE_MAX -1
        minPick.minValue = 0
        minPick.wrapSelectorWheel = true
        minPick.value = 1
        attrs?.let {
            processAttributeSet(it, context)
        }
    }

    private fun processAttributeSet(attributes: AttributeSet, ctx: Context) {
        //This method reads the parameters given in the xml file and sets the properties according to it
        ctx.theme.obtainStyledAttributes(
            attributes,
            R.styleable.RangedTimePicker,
            0, 0
        ).apply {

            try {
                velocity =
                getInteger( R.styleable.RangedTimePicker_velocity,
                    15)

                for (x in 0 until MINUTE_MAX step velocity){
                    minValues.add(x.toString())
                }

                getString(R.styleable.RangedTimePicker_minTitle)?.let {
                    findViewById<TextView>(R.id.minutes_title).text = it

                }
                 getString(R.styleable.RangedTimePicker_hoursTitle)?.let {
                     findViewById<TextView>(R.id.hours_title).text = it
                }

                minPick.maxValue = minValues.size - 1
                minPick.displayedValues = minValues.toTypedArray()

            } catch (e : Exception){
                Log.e(this.javaClass.canonicalName, e.toString())
            } finally {
                recycle()
            }
        }

        minPick.setOnValueChangedListener { picker, oldVal, newVal ->
            if(newVal == 0 && oldVal == minPick.maxValue){
                hoursPick.value =  hoursPick.value.inc()
            }
            if(newVal == 0 && oldVal == 1){
                hoursPick.value = hoursPick.value.dec()
            }
        }
    }

    fun getMinutes()= hoursPick.value * 60 + minPick.value * velocity

    companion object {
        const val MINUTE_MAX = 60
        const val HOURS_MAX = 24
    }
}