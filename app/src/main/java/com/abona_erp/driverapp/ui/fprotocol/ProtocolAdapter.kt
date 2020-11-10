package com.abona_erp.driverapp.ui.fprotocol

import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.data.local.db.ChangeHistory
import com.abona_erp.driverapp.data.local.db.Status
import com.abona_erp.driverapp.databinding.RequestItemBinding
import com.abona_erp.driverapp.ui.RxBus
import com.abona_erp.driverapp.ui.events.RxBusEvent
import com.abona_erp.driverapp.ui.utils.UtilModel
import com.kivi.remote.presentation.base.recycler.LazyAdapter


class ProtocolAdapter(listener: OnItemClickListener<ChangeHistory>) :
    LazyAdapter<ChangeHistory, RequestItemBinding>(listener) {

    override fun bindData(data: ChangeHistory, binding: RequestItemBinding) {

        binding.root.setOnClickListener { itemClickListener?.onLazyItemClick(data) }
        binding.textStatusVal.text = data.status.name

        when (data.status) {
            Status.SENT -> {
                binding.textStatusVal.setTextColor(
                    ResourcesCompat.getColor(
                        binding.root.resources,
                        R.color.confirm_gray,
                        null
                    )
                )
                binding.imageRetry.visibility = View.GONE
            }
            Status.SUCCESS -> {
                binding.textStatusVal.setTextColor(
                    ResourcesCompat.getColor(
                        binding.root.resources,
                        R.color.confirm_green,
                        null
                    )
                )
                binding.imageRetry.visibility = View.GONE
            }
            Status.ERROR -> {
                binding.textStatusVal.setTextColor(
                    ResourcesCompat.getColor(
                        binding.root.resources,
                        R.color.color_error,
                        null
                    )
                )
                binding.imageRetry.visibility = View.VISIBLE
            }
        }

        binding.textRequestType.text = data.dataType.name
        binding.textResponseVal.text = data.params

        binding.textTimeCreatedVal.text = UtilModel.formatLongTime(data.created)
        binding.textTimeModifVal.text = UtilModel.formatLongTime(data.modified)

        if (data.response != null) {
            binding.textParams.visibility = View.VISIBLE
            binding.textParamsVal.text = data.params
            binding.imageResponseArrow.visibility = View.VISIBLE
        } else {
            binding.textParams.visibility = View.GONE
            binding.imageResponseArrow.visibility = View.GONE
        }

        binding.textParams.setOnClickListener {
            changeParamsVisible(binding)
        }
        binding.imageRequestArrow.setOnClickListener {
            changeParamsVisible(binding)
        }
        binding.textResponse.setOnClickListener {
            changeResponseVisible(binding)
        }
        binding.imageResponseArrow.setOnClickListener {
            changeResponseVisible(binding)
        }

        binding.imageRetry.setOnClickListener { RxBus.publish(RxBusEvent.RetryRequest(data)) }
        binding.root.setOnClickListener { itemClickListener?.onLazyItemClick(data) }
    }

    private fun changeResponseVisible(binding: RequestItemBinding) {
        binding.textResponseVal.visibility =
            if (binding.textResponseVal.visibility == View.GONE) View.VISIBLE else View.GONE
        if (binding.textResponseVal.visibility == View.GONE) binding.imageResponseArrow.setImageResource(
            R.drawable.ic_arrow_down
        ) else binding.imageResponseArrow.setImageResource(R.drawable.ic_arrow_up)
    }

    private fun changeParamsVisible(binding: RequestItemBinding) {
        binding.textParamsVal.visibility =
            if (binding.textParamsVal.visibility == View.GONE) View.VISIBLE else View.GONE
        if (binding.textParamsVal.visibility == View.GONE) binding.imageRequestArrow.setImageResource(
            R.drawable.ic_arrow_down
        ) else binding.imageRequestArrow.setImageResource(R.drawable.ic_arrow_up)
    }


    override fun getLayoutId(): Int = R.layout.request_item
}
