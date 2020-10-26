package com.abona_erp.driverapp.data.model

enum class DMSDocumentType(var documentType: Int) {
    NA(0),
    POD_CMR(24),
    PALLETS_NOTE(26),
    SAFETY_CERTIFICATE(27),
    SHIPMENT_IMAGE(28),
    DAMAGED_SHIPMENT_IMAGE(29),
    DAMAGED_VEHICLE_IMAGE(30);


    companion object {
        fun getDocumentTypeByCode(documentType: Int): DMSDocumentType {
            for (type in values()) {
                if (type.documentType == documentType) return type
            }
            return NA
        }

        fun getDocumentIntByType(documentType: DMSDocumentType): Int {
            for (type in values()) {
                if (type == documentType) return type.documentType
            }
            return 0
        }
    }

}