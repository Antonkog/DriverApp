package com.abona_erp.driverapp.data.model

enum class DataType(var dataType: Int) {
    TASK(0),
    ALL_TASKS(1),
    COMPRESSED_TASK(2),
    ACTIVITY(20),
    UNDO_ACTIVITY(21),
    ALL_DELAY_REASONS(30),
    DELAY_REASONS(31),
    CONFIRMATION(40),
    DEVICE_PROFILE(60),
    VEHICLE(80),
    DOCUMENT(100),
    LINK_WITH_ABONA(200);


    companion object {
        fun getDataTypeByCode(dataType: Int): DataType {
            for (type in values()) {
                if (type.dataType == dataType) return type
            }
            return DataType.TASK
        }
    }

}