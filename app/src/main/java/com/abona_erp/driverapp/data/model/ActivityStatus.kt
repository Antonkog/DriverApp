package com.abona_erp.driverapp.data.model

import com.abona_erp.driverapp.R

enum class ActivityStatus(val status: Int, val resId: Int) {
    PENDING(0, R.string.pending),
    RUNNING(1, R.string.running),
    FINISHED(2, R.string.completed);

    companion object {
        fun getActivityStatus(status: Int): ActivityStatus {
            for (lt in values()) {
                if (lt.status == status) {
                    return lt
                }
            }
            return PENDING
        }
    }
}

