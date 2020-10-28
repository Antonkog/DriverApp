package com.abona_erp.driverapp.ui.fhome

import android.content.res.Resources
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.data.local.db.ActionType
import com.abona_erp.driverapp.data.local.db.ConfirmationType
import com.abona_erp.driverapp.databinding.TaskItemBinding
import com.abona_erp.driverapp.ui.utils.UtilModel.getImageResource
import com.abona_erp.driverapp.ui.utils.UtilModel.toDangerousGoodsClass
import com.kivi.remote.presentation.base.recycler.LazyAdapter


class TasksAdapter(itemClickListener: HomeFragment, val navController: NavController) :
    LazyAdapter<TaskWithActivities, TaskItemBinding>(
        itemClickListener
    ) {
    lateinit var mResources: Resources
    lateinit var dialogBuilder: DialogBuilder
    val TAG = "TasksAdapter"
    override fun bindData(data: TaskWithActivities, binding: TaskItemBinding) {
        mResources = binding.root.resources

      val nameId =  when(data.taskEntity.actionType){
           ActionType.PICK_UP -> R.string.action_type_pick_up
          ActionType.DROP_OFF -> R.string.action_type_drop_off
          ActionType.GENERAL -> R.string.action_type_general
          ActionType.TRACTOR_SWAP -> R.string.action_type_tractor_swap
          ActionType.DELAY ->R.string.action_type_delay
          ActionType.UNKNOWN -> R.string.action_type_unknown
          ActionType.ENUM_ERROR -> R.string.action_type_unknown
      }
        binding.textTaskName.text =  mResources.getString(nameId)

        binding.progressTask.progress = data.taskEntity.status.intId

        binding.textOrderNo.text = "" + data.taskEntity.orderDetails?.orderNo
        binding.textFinishTime.text = data.taskEntity.taskDueDateFinish
        binding.textActName.text =
            data.activities.firstOrNull { it.confirmstatus == ConfirmationType.RECEIVED }?.name
                ?: ""
        binding.textOrderNo.text = "" + data.taskEntity.orderDetails?.orderNo

        val context = binding.root.context
        dialogBuilder = DialogBuilder(context)



        val address =  data.taskEntity.address

        if (address != null) {
            binding.mapLayout.textAddrestFirst.text = String.format("${address.nation}, ${address.city}")
            binding.mapLayout.textAddressSecond.text = address.street
        }

        binding.imageInfo.setOnClickListener {
            val bundle = bundleOf("task_entity" to data.taskEntity)
            navController.navigate(R.id.action_nav_home_to_taskInfoFragment, bundle)
        }


        binding.mapLayout.imageMap.setOnClickListener {
            val bundle = bundleOf("map_data" to data.taskEntity.address)
            navController.navigate(R.id.action_nav_home_to_mapFragment, bundle)
        }

        setDangerGoodsImage(data, binding)
        setPalletsImage(data, binding)


        binding.activityButton.setOnClickListener { navController.navigate(R.id.action_nav_home_to_nav_activities) }
    }


    private fun setPalletsImage(
        data: TaskWithActivities,
        binding: TaskItemBinding
    ) {
        if (data.taskEntity.palletExchange?.palletsAmount ?: 0 > 0)
            binding.imagePallet.visibility = View.VISIBLE
        else binding.imagePallet.visibility = View.GONE

        binding.imagePallet.setOnClickListener {


        }
    }

    private fun setDangerGoodsImage(
        data: TaskWithActivities,
        binding: TaskItemBinding
    ) {
        if (data.taskEntity.dangerousGoods?.isGoodsDangerous == true) {
            binding.imageDanger.visibility = View.VISIBLE
            data.taskEntity.dangerousGoods?.dangerousGoodsClassType?.toDangerousGoodsClass()
                ?.getImageResource(binding.root.resources)?.let {
                binding.imageDanger.setImageDrawable(it)
            }

            binding.imageDanger.setOnClickListener{
                val bundle = bundleOf("goods_data" to data.taskEntity.dangerousGoods)
                navController.navigate(R.id.action_nav_home_to_dangerousGoodsFragment, bundle)
            }

        } else binding.imageDanger.visibility = View.GONE
    }

    override fun getLayoutId(): Int = R.layout.task_item


}