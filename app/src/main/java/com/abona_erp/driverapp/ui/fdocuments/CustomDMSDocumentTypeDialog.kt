package com.abona_erp.driverapp.ui.fdocuments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.data.model.DMSDocumentType

import com.abona_erp.driverapp.ui.utils.adapter.LazyAdapter
import com.abona_erp.driverapp.ui.utils.adapter.initWithLinLay
import kotlinx.android.synthetic.main.custom_dms_layout.*

class CustomDMSDocumentTypeDialog(
    context: Context,
    var documentTypeListener: OnSelectedDocumentTypeListener
) : Dialog(context), LazyAdapter.OnItemClickListener<DocumentTypeItem> {
    var documentType = DMSDocumentType.NA
    lateinit var documentTypeItemAdapter: DocumentTypeItemAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_dms_layout)
        ok.setOnClickListener {
            documentTypeListener.onSelectType(documentType)
            dismiss()
        }
        documentTypeItemAdapter = DocumentTypeItemAdapter(this)
        document_type_list.initWithLinLay(
            LinearLayoutManager.VERTICAL,
            documentTypeItemAdapter,
            getDocumentType(context.getString(R.string.document_type_pod))
        )
    }

    private fun getDocumentType(currentType: String): ArrayList<DocumentTypeItem> {
        val documentItemList = ArrayList<DocumentTypeItem>()
        val documentType = getDocumentType()
        for (type in documentType) {
            if (currentType == type) {
                documentItemList.add(DocumentTypeItem(type, true))
            } else {
                documentItemList.add(DocumentTypeItem(type, false))
            }
        }
        return documentItemList
    }

    private fun getDocumentType(): ArrayList<String> {
        val documentType = ArrayList<String>()
        documentType.add(context.getString(R.string.document_type_pod))
        documentType.add(context.getString(R.string.document_type_pn))
        documentType.add(context.getString(R.string.document_type_sc))
        documentType.add(context.getString(R.string.document_type_si))
        documentType.add(context.getString(R.string.document_type_dsi))
        documentType.add(context.getString(R.string.document_type_dvi))
        return documentType
    }

    override fun onLazyItemClick(data: DocumentTypeItem) {
        when (data.type) {
            context.getString(R.string.document_type_pod) -> {
                documentType = DMSDocumentType.POD_CMR
            }
            context.getString(R.string.document_type_pn) -> {
                documentType = DMSDocumentType.PALLETS_NOTE
            }
            context.getString(R.string.document_type_sc) -> {
                documentType = DMSDocumentType.SAFETY_CERTIFICATE
            }
            context.getString(R.string.document_type_si) -> {
                documentType = DMSDocumentType.SHIPMENT_IMAGE
            }
            context.getString(R.string.document_type_dsi) -> {
                documentType = DMSDocumentType.DAMAGED_SHIPMENT_IMAGE
            }
            context.getString(R.string.document_type_dvi) -> {
                documentType = DMSDocumentType.DAMAGED_VEHICLE_IMAGE
            }
            else -> {
                documentType = DMSDocumentType.NA
            }
        }
        documentTypeItemAdapter.swapData(getDocumentType(data.type))
    }
}