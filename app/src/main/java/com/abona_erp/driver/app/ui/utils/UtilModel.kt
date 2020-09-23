package com.abona_erp.driver.app.ui.utils

import android.content.Context
import android.os.Build
import com.abona_erp.driver.app.BuildConfig
import com.abona_erp.driver.app.data.Constant
import com.abona_erp.driver.app.data.local.db.ActivityEntity
import com.abona_erp.driver.app.data.local.preferences.PrivatePreferences
import com.abona_erp.driver.app.data.model.*
import com.google.gson.annotations.SerializedName
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object UtilModel{



//    fun mapActivityToSend(activity: ActivityEntity): Activity {
//        return Activity(activity.activityId, activity.activityId, null, activity.)
//    }
//    @SerializedName("ActivityId")
//    val activityId: Int,
//    @SerializedName("CustomActivityId")
//    val customActivityId: Int,
//    @SerializedName("DelayReasons")
//    val delayReasons: Any,
//    @SerializedName("Description")
//    val description: String?,
//    @SerializedName("DeviceId")
//    val deviceId: String?,
//    @SerializedName("Finished")
//    val finished: String?,
//    @SerializedName("MandantId")
//    val mandantId: Int,
//    @SerializedName("Name")
//    val name: String?,
//    @SerializedName("RadiusGeoFence")
//    val radiusGeoFence: Int,
//    @SerializedName("Sequence")
//    val sequence: Int,
//    @SerializedName("Started")
//    val started: String?,
//    @SerializedName("Status")
//    val status: Int,
//    @SerializedName("TaskId")
//    val taskId: Int

    fun getCommActivityChangeItem(context: Context, activity: Activity): CommItem {
        val header = Header.Builder(DataType.ACTIVITY.dataType,DeviceUtils.getUniqueID(context)).build()
        return CommItem.Builder(header = header)
            .deviceProfileItem(getDeviceProfileItem(context))
            .activityItem(activity).build()
    }

    fun getCommDeviceProfileItem(context: Context): CommItem {
        val header = Header.Builder(DataType.DEVICE_PROFILE.dataType,DeviceUtils.getUniqueID(context)).build()
        return CommItem.Builder(header = header).deviceProfileItem(getDeviceProfileItem(context)).build()
    }



    private fun getDeviceProfileItem(context:Context) : DeviceProfileItem {
        val dfUtc: DateFormat = SimpleDateFormat(Constant.abonaDateFormat, Locale.getDefault())
        dfUtc.timeZone = TimeZone.getTimeZone(Constant.abonaTimeZone)
        val currentDate = dfUtc.format(Date())
        val deviceProfileItem = DeviceProfileItem()
        deviceProfileItem.instanceId = PrivatePreferences.getFCMToken(context)
        deviceProfileItem.deviceId = DeviceUtils.getUniqueID(context)
        deviceProfileItem.model = Build.MODEL
        deviceProfileItem.manufacturer = Build.MANUFACTURER

        deviceProfileItem.createdDate = currentDate
        deviceProfileItem.updatedDate = currentDate
        deviceProfileItem.languageCode = Locale.getDefault().toString().replace("_", "-")
        deviceProfileItem.versionCode = BuildConfig.VERSION_CODE
        deviceProfileItem.versionName = BuildConfig.VERSION_NAME
        return deviceProfileItem
    }
}
