package com.abona_erp.driverapp.ui.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import com.abona_erp.driverapp.BuildConfig
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.data.Constant
import com.abona_erp.driverapp.data.local.db.ActivityEntity
import com.abona_erp.driverapp.data.local.db.ConfirmationType
import com.abona_erp.driverapp.data.local.db.DangerousGoodsClass
import com.abona_erp.driverapp.data.local.db.DelayReasonEntity
import com.abona_erp.driverapp.data.local.preferences.PrivatePreferences
import com.abona_erp.driverapp.data.model.*
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

object UtilModel {

    private const val TAG = "UtilModel"

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
        val header =
            Header.Builder(DataType.ACTIVITY.dataType, DeviceUtils.getUniqueID(context)).build()
        return CommItem.Builder(header = header)
            .deviceProfileItem(getDeviceProfileItem(context))
            .activityItem(activity).build()
    }

    fun getCommDeviceProfileItem(context: Context): CommItem {
        val header =
            Header.Builder(DataType.DEVICE_PROFILE.dataType, DeviceUtils.getUniqueID(context))
                .build()
        return CommItem.Builder(header = header).deviceProfileItem(getDeviceProfileItem(context))
            .build()
    }


    fun ActivityEntity.toActivity(deviceId: String): Activity {
        val reasons = delayReasons?.map { item -> item.toDelayReason() }
        return Activity(
            activityId,
            activityId,
            reasons,
            description,
            deviceId,
            finished,
            mandantId,
            name,
            radiusGeoFence,
            sequence,
            started,
            activityStatus.status,
            taskpId
        )
    }


     fun Activity.toActivityEntity(): ActivityEntity {
        val reasons = delayReasons?.map { item -> item.toDelayReasonEntity() }
        return ActivityEntity(
            activityId,
            activityId,
            reasons,
            description,
            finished,
            mandantId,
            name,
            radiusGeoFence,
            sequence,
            taskId,
            started,
            ActivityStatus.getActivityStatus(status),
            ConfirmationType.RECEIVED
        )
    }

    fun getActivityStatusResId(type: ActivityStatus): Int {
        for (lt in ActivityStatus.values()) {
            if (lt == type) {
                return lt.resId
            }
        }
        return 0
    }

    private fun DelayReasonEntity.toDelayReason(): DelayReasonItem {
        return DelayReasonItem(
            waitingReasongId,
            activityId,
            reasonText,
            translatedReasonText,
            code,
            subcode,
            mandantId,
            taskId,
            timestampUtc,
            delayInMinutes,
            delaySource,
            comment
        )
    }

    fun DelayReasonItem.toDelayReasonEntity(): DelayReasonEntity {
        return DelayReasonEntity(
            waitingReasongId,
            activityId,
            reasonText,
            translatedReasonText,
            code,
            subCode,
            mandantId,
            taskId,
            timestampUtc,
            delayInMinutes,
            delaySource,
            comment
        )
    }

    fun Int.toDangerousGoodsClass(): DangerousGoodsClass {
        return DangerousGoodsClass.getActionType(this)
    }

    fun DangerousGoodsClass.getImageResource(mResources: Resources): Drawable? {
        return when (this) {
            DangerousGoodsClass.CLASS_1_EXPLOSIVES -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_1_explosives,
                null
            )
            DangerousGoodsClass.CLASS_1_1_EXPLOSIVES -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_1_explosives_1_1,
                null
            )
            DangerousGoodsClass.CLASS_1_2_EXPLOSIVES -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_1_explosives_1_2,
                null
            )
            DangerousGoodsClass.CLASS_1_3_EXPLOSIVES -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_1_explosives_1_3,
                null
            )
            DangerousGoodsClass.CLASS_1_4_EXPLOSIVES -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_1_explosives_1_4,
                null
            )
            DangerousGoodsClass.CLASS_1_5_EXPLOSIVES -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_1_explosives_1_5,
                null
            )
            DangerousGoodsClass.CLASS_1_6_EXPLOSIVES -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_1_explosives_1_6,
                null
            )
            DangerousGoodsClass.CLASS_2_FLAMMABLE_GAS -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_2_flammable_gas,
                null
            )
            DangerousGoodsClass.CLASS_2_NON_FLAMMABLE_GAS -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_2_non_flammable_gas,
                null
            )
            DangerousGoodsClass.CLASS_2_POISON_GAS -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_2_poison_gas,
                null
            )
            DangerousGoodsClass.CLASS_3_FLAMMABLE_LIQUID -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_3_flammable_liquid,
                null
            )
            DangerousGoodsClass.CLASS_4_1_FLAMMABLE_SOLIDS -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_4_flammable_solid,
                null
            )
            DangerousGoodsClass.CLASS_4_2_SPONTANEOUSLY_COMBUSTIBLE -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_4_spontaneously_combustible,
                null
            )
            DangerousGoodsClass.CLASS_4_3_DANGEROUSE_WHEN_WET -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_4_dangerous_when_wet,
                null
            )
            DangerousGoodsClass.CLASS_5_1_OXIDIZER -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_5_1_oxidizer,
                null
            )
            DangerousGoodsClass.CLASS_5_2_ORAGNIC_PEROXIDES -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_5_2_organic_peroxides,
                null
            )
            DangerousGoodsClass.CLASS_6_1_POISON -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_6_poison,
                null
            )
            DangerousGoodsClass.CLASS_6_2_INFECTIOUS_SUBSTANCE -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_6_2_infectious_substance,
                null
            )
            DangerousGoodsClass.CLASS_7_FISSILE -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_7_fissile,
                null
            )
            DangerousGoodsClass.CLASS_7_RADIOACTIVE_I -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_7_radioactive_i,
                null
            )
            DangerousGoodsClass.CLASS_7_RADIOACTIVE_II -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_7_radioactive_ii,
                null
            )
            DangerousGoodsClass.CLASS_7_RADIOACTIVE_III -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_7_radioactive_iii,
                null
            )
            DangerousGoodsClass.CLASS_8_CORROSIVE -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_8_corrosive,
                null
            )
            DangerousGoodsClass.CLASS_9_MISCELLANEOUS -> ResourcesCompat.getDrawable(
                mResources,
                R.drawable.ic_class_9_miscellaneus,
                null
            )
            else -> ResourcesCompat.getDrawable(mResources, R.drawable.ic_risk, null)
        }
    }


    private fun serverDateFormat(): DateFormat {
        val dfUtc: DateFormat =
            SimpleDateFormat(Constant.abonaCommunicationDateFormat, Locale.getDefault())
        dfUtc.timeZone = TimeZone.getTimeZone(Constant.abonaTimeZone)
        return dfUtc
    }

    private fun uiDateFormat(): DateFormat {
        val dfUtc: DateFormat = SimpleDateFormat(Constant.abonaUIDateFormat, Locale.getDefault())
        dfUtc.timeZone = TimeZone.getTimeZone(Constant.abonaTimeZone)
        return dfUtc
    }

    private fun formatLongDateTime(date: Date): String {
        return serverDateFormat().format(date)
    }

    fun serverTimeShortener(date: String): String {
        return try {
            val dateOpt = serverDateFormat().parse(date)
            dateOpt.let {uiDateFormat().format(it)}
        } catch (e: ParseException) {
            e.printStackTrace()
            date
        }
    }

    fun getCurrentDateServerFormat(): String {
        return formatLongDateTime(Date())
    }


    /**
     * This method will try to find order num from activity order string
     * @param someString that string should contain value that is less [java.lang.Integer.MAX_VALUE])
     * @return 0 or some number 8998ffff7788sfsf will return 89987788
     * @author Anton Kogan
     */
    fun parseInt(someString: String): Int {
        var result = 0
        val partsBuffer = StringBuilder()
        val p = Pattern.compile("\\d+")
        val m = p.matcher(someString)
        while (m.find()) {
            partsBuffer.append(m.group())
        }
        try {
            result = partsBuffer.toString().toInt()
        } catch (e: NumberFormatException) {
            Log.e(
                TAG,
                " wrong input in getInt(String someString)"
            )
        }
        return result
    }


    /**
     *
     * this is for providing ui with dividers from order number that is date with addition
     * @param orderNo - Long value
     * @return "-" if int is zero
     */
    fun parseOrderNo(orderNo: Long): String? {
        return if (orderNo > 0) {
            try {
                val numString = orderNo.toString().toCharArray()
                val parsedNum = java.lang.StringBuilder()
                for (counter in numString.indices) {
                    if (counter == 4 || counter == 6) {
                        parsedNum.append("/")
                    }
                    parsedNum.append(numString[counter])
                }
                parsedNum.toString()
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    " parsing exception : string from server based on :$orderNo"
                )
                "-"
            }
        } else "-"
    }

    private fun getDeviceProfileItem(context: Context): DeviceProfileItem {
        val deviceProfileItem = DeviceProfileItem()
        deviceProfileItem.instanceId = PrivatePreferences.getFCMToken(context)
        deviceProfileItem.deviceId = DeviceUtils.getUniqueID(context)
        deviceProfileItem.model = Build.MODEL
        deviceProfileItem.manufacturer = Build.MANUFACTURER

        deviceProfileItem.createdDate = getCurrentDateServerFormat()
        deviceProfileItem.updatedDate = getCurrentDateServerFormat()
        deviceProfileItem.languageCode = Locale.getDefault().toString().replace("_", "-")
        deviceProfileItem.versionCode = BuildConfig.VERSION_CODE
        deviceProfileItem.versionName = BuildConfig.VERSION_NAME
        return deviceProfileItem
    }
}
