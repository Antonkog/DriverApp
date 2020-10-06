package com.abona_erp.driver.app.ui.home

import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import com.abona_erp.driver.app.R
import com.abona_erp.driver.app.data.local.db.DangerousGoodsClass
import com.abona_erp.driver.app.data.local.db.TaskEntity
import com.abona_erp.driver.app.databinding.TaskItemBinding
import com.kivi.remote.presentation.base.recycler.LazyAdapter


class TasksAdapter(itemClickListener: HomeFragment) : LazyAdapter<TaskEntity, TaskItemBinding>(
    itemClickListener
) {
    lateinit var mResources : Resources
val TAG = "TasksAdapter"
    override fun bindData(data: TaskEntity, binding: TaskItemBinding) {
        mResources = binding.root.resources
     binding.textTaskName.text = data.actionType.name
     binding.textOrderNo.text = ""+ data.orderDetails?.orderNo
     binding.textFinishTime.text = data.taskDueDateFinish

    }

    override fun getLayoutId(): Int = R.layout.task_item


    private fun getDangerousGoodsClass(dangerousGoodsClass: DangerousGoodsClass): Drawable? {
        return when (dangerousGoodsClass) {
            DangerousGoodsClass.CLASS_1_EXPLOSIVES -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_1_explosives,
                null
            )
            DangerousGoodsClass.CLASS_1_1_EXPLOSIVES -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_1_explosives_1_1,
                null
            )
            DangerousGoodsClass.CLASS_1_2_EXPLOSIVES -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_1_explosives_1_2,
                null
            )
            DangerousGoodsClass.CLASS_1_3_EXPLOSIVES -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_1_explosives_1_3,
                null
            )
            DangerousGoodsClass.CLASS_1_4_EXPLOSIVES -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_1_explosives_1_4,
                null
            )
            DangerousGoodsClass.CLASS_1_5_EXPLOSIVES -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_1_explosives_1_5,
                null
            )
            DangerousGoodsClass.CLASS_1_6_EXPLOSIVES -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_1_explosives_1_6,
                null
            )
            DangerousGoodsClass.CLASS_2_FLAMMABLE_GAS -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_2_flammable_gas,
                null
            )
            DangerousGoodsClass.CLASS_2_NON_FLAMMABLE_GAS -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_2_non_flammable_gas,
                null
            )
            DangerousGoodsClass.CLASS_2_POISON_GAS -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_2_poison_gas,
                null
            )
            DangerousGoodsClass.CLASS_3_FLAMMABLE_LIQUID -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_3_flammable_liquid,
                null
            )
            DangerousGoodsClass.CLASS_4_1_FLAMMABLE_SOLIDS -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_4_flammable_solid,
                null
            )
            DangerousGoodsClass.CLASS_4_2_SPONTANEOUSLY_COMBUSTIBLE -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_4_spontaneously_combustible,
                null
            )
            DangerousGoodsClass.CLASS_4_3_DANGEROUSE_WHEN_WET -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_4_dangerous_when_wet,
                null
            )
            DangerousGoodsClass.CLASS_5_1_OXIDIZER -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_5_1_oxidizer,
                null
            )
            DangerousGoodsClass.CLASS_5_2_ORAGNIC_PEROXIDES -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_5_2_organic_peroxides,
                null
            )
            DangerousGoodsClass.CLASS_6_1_POISON -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_6_poison,
                null
            )
            DangerousGoodsClass.CLASS_6_2_INFECTIOUS_SUBSTANCE -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_6_2_infectious_substance,
                null
            )
            DangerousGoodsClass.CLASS_7_FISSILE -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_7_fissile,
                null
            )
            DangerousGoodsClass.CLASS_7_RADIOACTIVE_I -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_7_radioactive_i,
                null
            )
            DangerousGoodsClass.CLASS_7_RADIOACTIVE_II -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_7_radioactive_ii,
                null
            )
            DangerousGoodsClass.CLASS_7_RADIOACTIVE_III -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_7_radioactive_iii,
                null
            )
            DangerousGoodsClass.CLASS_8_CORROSIVE -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_8_corrosive,
                null
            )
            DangerousGoodsClass.CLASS_9_MISCELLANEOUS -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_9_miscellaneus,
                null
            )
            else -> ResourcesCompat.getDrawable(mResources, R.drawable.ic_risk, null)
        }
    }
}