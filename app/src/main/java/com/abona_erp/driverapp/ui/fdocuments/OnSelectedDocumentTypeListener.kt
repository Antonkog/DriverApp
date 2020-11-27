package com.abona_erp.driverapp.ui.fdocuments

import com.abona_erp.driverapp.data.model.DMSDocumentType

interface OnSelectedDocumentTypeListener {
    fun onSelectType(documentType: DMSDocumentType)
}