package com.abona_erp.driverapp.ui.factivities

import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.data.local.db.ActivityConfirmationType
import com.abona_erp.driverapp.data.local.db.ActivityWrapper
import com.abona_erp.driverapp.data.model.ActivityStatus
import com.abona_erp.driverapp.databinding.ActivityItemBinding
import com.abona_erp.driverapp.ui.utils.UtilModel
import com.kivi.remote.presentation.base.recycler.LazyAdapter


class ActivityAdapter(itemClickListener: DriverActFragment,  val navController: NavController) :
    LazyAdapter<ActivityWrapper, ActivityItemBinding>(itemClickListener) {

    override fun bindData(data: ActivityWrapper, binding: ActivityItemBinding) {

        val context = binding.root.context

        binding.textActName.text = data.activity.name

        if(data.buttonVisible) binding.buttonActConfirm.visibility = View.VISIBLE
        else binding.buttonActConfirm.visibility = View.GONE

        binding.imageConfirmed.setColorFilter(ActivityConfirmationType.getColor(context, data.activity.confirmationType))

        when (data.activity.activityStatus) {
            ActivityStatus.PENDING -> {
                binding.textFinished.visibility = View.INVISIBLE
                binding.textFinishedValue.clearComposingText()
                binding.buttonActConfirm.text =
                    binding.root.resources.getString(R.string.activity_start)

            }
            ActivityStatus.RUNNING -> {
                binding.textFinished.visibility = View.INVISIBLE
                binding.textFinishedValue.clearComposingText()
                binding.buttonActConfirm.text =
                    binding.root.resources.getString(R.string.activity_next)
            }
            ActivityStatus.FINISHED -> {
                binding.textFinished.visibility = View.VISIBLE
                binding.buttonActConfirm.visibility = View.GONE
                data.activity.finished.let {
                    binding.textFinishedValue.text = UtilModel.formatLongTime(it)
                }
                binding.imageConfirmed.setColorFilter( // if activity finished setting green, even if confirmation status not saved in database (was removed when exit)
                    ResourcesCompat.getColor(
                    context.resources,
                    R.color.confirm_green,
                    null
                ))
            }
            ActivityStatus.ENUM_ERROR -> {
                binding.textFinished.visibility = View.INVISIBLE
                binding.buttonActConfirm.visibility = View.VISIBLE
            }
        }

        if(data.isLastActivity){
            binding.buttonActConfirm.text =
                binding.root.resources.getString(R.string.activity_finish)
        }
        data.activity.started.let {
            binding.textStartedValue.text =  UtilModel.formatLongTime(it)
        }

        binding.buttonActConfirm.setOnClickListener {
            itemClickListener?.onLazyItemClick(data)
        }

        setDelayReasonButton(data, binding)
    }


    fun setDelayReasonButton(data: ActivityWrapper, binding: ActivityItemBinding){
        if(data.activity.delayReasons?.isNotEmpty() == true){
            val bundle = bundleOf(binding.root.context.getString(R.string.key_activity_entity) to data.activity)
            binding.imageDelays.visibility = View.VISIBLE
            binding.imageDelays.setOnClickListener {
                navController.navigate(
                    R.id.action_nav_activities_to_delayReasonDialog,
                    bundle
                )
            }
        } else {
            binding.imageDelays.visibility = View.GONE
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_item
}