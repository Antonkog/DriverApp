package com.abona_erp.driverapp.ui.ftasks

import android.content.res.Resources
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.data.local.db.ActivityConfirmationType
import com.abona_erp.driverapp.data.local.db.ConfirmationType
import com.abona_erp.driverapp.databinding.TaskItemBinding
import com.abona_erp.driverapp.ui.utils.UtilModel
import com.abona_erp.driverapp.ui.utils.UtilModel.getImageResource
import com.abona_erp.driverapp.ui.utils.UtilModel.getTimeDifference
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

        binding.cardView.setOnClickListener { itemClickListener?.onLazyItemClick(data) }

        val color = ConfirmationType.getColor(context, data.taskEntity.confirmationType?: ConfirmationType.RECEIVED)
        binding.imageSync?.setColorFilter(color)

        val nameId = UtilModel.getResIdByTaskActionType(data.taskEntity)

        binding.hiddenSection.visibility =
            if (data.taskEntity.openCondition) View.VISIBLE else View.GONE

        binding.progressTask.progress = data.taskEntity.status.intId
        binding.textTaskName.text = mResources.getString(nameId)

        data.taskEntity.taskDueDateFinish?.let { //as we don't have taskDueDateFinish from server - here counting
            binding.textFinishTime.text = UtilModel.serverDateShortener(it)
            binding.textDueInTime.text = getTimeDifference(it, context)
        }

        binding.textActName.text =
            data.activities.firstOrNull { it.confirmationType == ActivityConfirmationType.RECEIVED }?.name
                ?: "" //todo: sort by activity id

        binding.textOrderNo.text = UtilModel.parseOrderNo(data.taskEntity.orderDetails?.orderNo?.toLong() ?: 0)

        setActivitiesButton(data, binding)
        setDocumentsButton(data, binding)
        setContactButton(data, binding)
        setAddressButton(data, binding)
        setInfoButton(data, binding)
        setMapButton(data, binding)
        setNotesButton(data, binding)
        setDangerGoodsButton(data, binding)
        setPalletsButton(data, binding)


    }

    private fun setDocumentsButton(data: TaskWithActivities, binding: TaskItemBinding) {
        binding.imageDocuments.setOnClickListener {
            val bundle = bundleOf(binding.root.context.getString(R.string.key_task_entity) to data.taskEntity)
            navController.navigate(R.id.action_nav_home_to_nav_documents, bundle)
        }
    }

    private fun setActivitiesButton(data: TaskWithActivities, binding: TaskItemBinding) {
        val bundle = bundleOf(binding.root.context.getString(R.string.key_task_entity) to data.taskEntity)
        binding.activityButton.setOnClickListener { navController.navigate(R.id.action_nav_home_to_nav_activities, bundle)}
    }


    private fun setContactButton(data: TaskWithActivities, binding: TaskItemBinding) {
        if (!data.taskEntity.constacts.isNullOrEmpty()) {
            binding.imagePhone.visibility = View.VISIBLE
            binding.imagePhone.setOnClickListener {
                val contactsArray =
                    data.taskEntity.constacts!!.toTypedArray()//checked for null or empty
                val bundle =
                    bundleOf(binding.root.context.getString(R.string.key_contacts_data) to contactsArray)
                navController.navigate(R.id.action_nav_home_to_contactsFragment, bundle)
            }
        } else {
            binding.imagePhone.visibility = View.GONE
        }
    }

    private fun setNotesButton(data: TaskWithActivities, binding: TaskItemBinding) {
        if (data.taskEntity.notesItem == null) binding.imageNotes.visibility = View.GONE
        else {
            binding.imageNotes.visibility = View.VISIBLE
            binding.imageNotes.setOnClickListener {
                val bundle =
                    bundleOf(binding.root.context.getString(R.string.key_notes_data) to data.taskEntity.notesItem)
                navController.navigate(R.id.action_nav_home_to_notesFragment, bundle)
            }
        }

    }

    private fun setMapButton(
        data: TaskWithActivities,
        binding: TaskItemBinding
    ) {
        binding.imageMap.setOnClickListener {
            val bundle =
                bundleOf(binding.root.context.getString(R.string.key_map_data) to data.taskEntity.address)
            navController.navigate(R.id.action_nav_home_to_mapFragment, bundle)
        }
    }


    private fun setInfoButton(
        data: TaskWithActivities,
        binding: TaskItemBinding
    ) {
        binding.imageInfo.setOnClickListener {
            val bundle =
                bundleOf(binding.root.context.getString(R.string.key_task_entity) to data.taskEntity)
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
            val bundle =
                bundleOf(binding.root.context.getString(R.string.key_pallets_data) to data.taskEntity.palletExchange)
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
                val bundle =
                    bundleOf(binding.root.context.getString(R.string.key_goods_data) to data.taskEntity.dangerousGoods)
                navController.navigate(R.id.action_nav_home_to_dangerousGoodsFragment, bundle)
            }

        } else binding.imageDanger.visibility = View.GONE
    }

    override fun getLayoutId(): Int = R.layout.task_item

    companion object {
        const val TAG = "TasksAdapter"
    }
}