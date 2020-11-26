package com.abona_erp.driverapp.ui.fdocuments

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TaskData(var orderNo: Int?, var taskId: Int?, var mandantId: Int?) : Parcelable