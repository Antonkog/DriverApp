package com.abona_erp.driverapp.data.model

enum class DataType(var dataType: Int) {
    TASK(0),//from FCM
    ALL_TASKS(1),
    COMPRESSED_TASK(2),
    ACTIVITY(20),  //for activity steps sync
    UNDO_ACTIVITY(21),
    ALL_DELAY_REASONS(30),
    DELAY_REASONS(31),
    TASK_CONFIRMATION(40),//to RestAPI
    DEVICE_PROFILE(60),//from FCM
    VEHICLE(80),//from FCM
    DOCUMENT(100),//from FCM
    LINK_WITH_ABONA(200);


    companion object {
        fun getDataTypeByCode(dataType: Int): DataType {
            for (type in values()) {
                if (type.dataType == dataType) return type
            }
            return TASK
        }
    }

}