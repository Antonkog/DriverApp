package com.abona_erp.driverapp.ui.ftasks

import android.content.res.Resources
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.data.local.db.ActionType
import com.abona_erp.driverapp.data.local.db.ConfirmationType
import com.abona_erp.driverapp.databinding.TaskItemBinding
import com.abona_erp.driverapp.ui.utils.UtilModel
import com.abona_erp.driverapp.ui.utils.UtilModel.getImageResource
import com.abona_erp.driverapp.ui.utils.UtilModel.toDangerousGoodsClass
import com.kivi.remote.presentation.base.recycler.LazyAdapter


class TasksAdapter(itemClickListener: TasksFragment, val navController: NavController) :
    LazyAdapter<TaskWithActivities, TaskItemBinding>(
        itemClickListener
    ) {

    lateinit var mResources: Resources
    lateinit var dialogBuilder: DialogBuilder

    override fun bindData(data: TaskWithActivities, binding: TaskItemBinding) {
        mResources = binding.root.resources
        val context = binding.root.context
        dialogBuilder = DialogBuilder(context)


        val nameId = when (data.taskEntity.actionType) {
            ActionType.PICK_UP -> R.string.action_type_pick_up
            ActionType.DROP_OFF -> R.string.action_type_drop_off
            ActionType.GENERAL -> R.string.action_type_general
            ActionType.TRACTOR_SWAP -> R.string.action_type_tractor_swap
            ActionType.DELAY -> R.string.action_type_delay
            ActionType.UNKNOWN -> R.string.action_type_unknown
            ActionType.ENUM_ERROR -> R.string.action_type_unknown
        }

        binding.progressTask.progress = data.taskEntity.status.intId
        binding.textTaskName.text = mResources.getString(nameId)
        data.taskEntity.taskDueDateFinish?.let{
            binding.textFinishTime.text = UtilModel.serverTimeShortener(it)
        }

        binding.textActName.text =
            data.activities.firstOrNull { it.confirmstatus == ConfirmationType.RECEIVED }?.name
                ?: "" //todo: sort by activity id
        binding.textOrderNo.text =
            UtilModel.parseOrderNo(data.taskEntity.orderDetails?.orderNo?.toLong() ?: 0)


        setAddressButton(data, binding)
        setInfoButton(data, binding)
        setMapButton(data, binding)
        setNotesButton(data, binding)
        setDangerGoodsButton(data, binding)
        setPalletsButton(data, binding)


        binding.activityButton.setOnClickListener {navController.navigate(R.id.action_nav_home_to_nav_activities) }
    }

    private fun setNotesButton(data: TaskWithActivities, binding: TaskItemBinding) {
        if(data.taskEntity.notesItem==null)binding.imageNotes.visibility = View.GONE
        else{
            binding.imageNotes.visibility = View.VISIBLE
            binding.imageNotes.setOnClickListener {
                val bundle = bundleOf(binding.root.context.getString(R.string.key_notes_data) to data.taskEntity.notesItem)
                navController.navigate(R.id.action_nav_home_to_notesFragment, bundle)
            }
        }

    }

    private fun setMapButton(
        data: TaskWithActivities,
        binding: TaskItemBinding
    ) {
        binding.imageMap.setOnClickListener {
            val bundle = bundleOf(binding.root.context.getString(R.string.key_map_data) to data.taskEntity.address)
            navController.navigate(R.id.action_nav_home_to_mapFragment, bundle)
        }
    }


    private fun setInfoButton(
        data: TaskWithActivities,
        binding: TaskItemBinding
    ) {
        binding.imageInfo.setOnClickListener {
            val bundle = bundleOf(binding.root.context.getString(R.string.key_task_entity) to data.taskEntity)
            navController.navigate(R.id.action_nav_home_to_taskInfoFragment, bundle)
        }
    }

    private fun setAddressButton(
        data: TaskWithActivities,
        binding: TaskItemBinding
    ) {
        val address = data.taskEntity.address

        if (address != null) {
            binding.mapLayout.textAddrestFirst.text =
                String.format("${address.nation}, ${address.city}")
            binding.mapLayout.textAddressSecond.text = address.street
        }
    }


    private fun setPalletsButton(
        data: TaskWithActivities,
        binding: TaskItemBinding
    ) {
        if (data.taskEntity.palletExchange?.palletsAmount ?: 0 > 0)
            binding.imagePallet.visibility = View.VISIBLE
        else binding.imagePallet.visibility = View.GONE

        binding.imagePallet.setOnClickListener {
            val bundle = bundleOf(binding.root.context.getString(R.string.key_pallets_data) to data.taskEntity.palletExchange)
            navController.navigate(R.id.action_nav_home_to_palletsFragment, bundle)
        }
    }

    private fun setDangerGoodsButton(
        data: TaskWithActivities,
        binding: TaskItemBinding
    ) {
        if (data.taskEntity.dangerousGoods?.isGoodsDangerous == true) {
            binding.imageDanger.visibility = View.VISIBLE
            data.taskEntity.dangerousGoods?.dangerousGoodsClassType?.toDangerousGoodsClass()
                ?.getImageResource(binding.root.resources)?.let {
                    binding.imageDanger.setImageDrawable(it)
                }

            binding.imageDanger.setOnClickListener {
                val bundle = bundleOf(binding.root.context.getString(R.string.key_goods_data) to data.taskEntity.dangerousGoods)
                navController.navigate(R.id.action_nav_home_to_dangerousGoodsFragment, bundle)
            }

        } else binding.imageDanger.visibility = View.GONE
    }

    override fun getLayoutId(): Int = R.layout.task_item

    companion object {
        const val TAG = "TasksAdapter"
    }
}