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
import com.abona_erp.driverapp.data.local.db.*
import com.abona_erp.driverapp.data.local.preferences.PrivatePreferences
import com.abona_erp.driverapp.data.model.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern


object UtilModel {

    private const val TAG = "UtilModel"

    data class AuthModel(
        val grantType: String,
        val userName: String,
        val password: String
    )

    fun getCommActivityChangeItem(context: Context, activity: Activity): CommItem {
        val header =
            Header.Builder(DataType.ACTIVITY.dataType, DeviceUtils.getUniqueID(context)).build()
        return CommItem.Builder(header = header)
            .deviceProfileItem(getDeviceProfileItem(context))
            .activityItem(activity).build()
    }

    fun getCommDelayChangeItem(context: Context, delayReasonItems: List<DelayReasonItem>): CommItem {
        val header = Header.Builder(DataType.DELAY_REASONS.dataType, DeviceUtils.getUniqueID(context)).build()
        return CommItem.Builder(header = header)
            .delayReasonItems(delayReasonItems)
            .build()
    }

    fun getCommDeviceProfileItem(context: Context): CommItem {
        val header =
            Header.Builder(DataType.DEVICE_PROFILE.dataType, DeviceUtils.getUniqueID(context))
                .build()
        return CommItem.Builder(header = header).deviceProfileItem(getDeviceProfileItem(context))
            .build()
    }


    fun getTaskConfirmation(context: Context, task: TaskEntity): CommItem {

        val header =
            Header.Builder(DataType.TASK_CONFIRMATION.dataType, DeviceUtils.getUniqueID(context))
                .build()
        return CommItem.Builder(header = header)
            .confirmationItem(getInnerTaskConfirmation(task)).build()
    }

    fun ActivityEntity.toActivity(deviceId: String): Activity {
        val reasons = delayReasons?.map { item -> item.toDelayReason() }
        return Activity(
            activityId,
            activityId,
            reasons,
            description,
            deviceId,
            mandantId,
            name,
            radiusGeoFence,
            sequence,
            Date(started),
            Date(finished),
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
            mandantId,
            name,
            radiusGeoFence,
            sequence,
            taskId,
            started.time,
            finished.time,
            ActivityStatus.getActivityStatus(status),
            ActivityConfirmationType.RECEIVED
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
            waitingReasonType,
            activityId,
            reasonText,
            translatedReasonText,
            code,
            subcode,
            mandantId,
            taskId,
            Date(timestampUtc),
            delayInMinutes,
            delaySource,
            comment
        )
    }

    fun DelayReasonItem.toDelayReasonEntity(): DelayReasonEntity {
        return DelayReasonEntity(
            waitingReasonType,
            activityId,
            reasonText,
            translatedReasonText,
            code,
            subCode,
            mandantId,
            taskId,
timestampUtc?.time ?: 0L,
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




    fun formatTimeDifference(difference: Long, context: Context): String {
        return String.format(
                context.resources.getString(R.string.task_duein_format),
                TimeUnit.MILLISECONDS.toDays(difference),
                TimeUnit.MILLISECONDS.toHours(difference) % TimeUnit.HOURS.toHours(1),
                TimeUnit.MILLISECONDS.toMinutes(difference) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(difference) % TimeUnit.MINUTES.toSeconds(1)
            )
    }

    private fun uiTimeFormat(): DateFormat {
        val dfUtc: DateFormat = SimpleDateFormat(Constant.abonaUITimeFormat, Locale.getDefault())
        dfUtc.timeZone = TimeZone.getTimeZone(Constant.abonaTimeZone)
        return dfUtc
    }

    fun formatLongTime(dateMills: Long): String {
        return uiTimeFormat().format(Date(dateMills))
    }

    fun getCurrentDateServerFormat(): String {
        val dfUtc: DateFormat = SimpleDateFormat(Constant.abonaCommunicationDateVarThree, Locale.getDefault())
        dfUtc.timeZone = TimeZone.getTimeZone(Constant.abonaTimeZone)
        return dfUtc.format(Date())
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

    private fun getInnerTaskConfirmation(taskItem: TaskEntity): ConfirmationItem {
        val confirmationItem = ConfirmationItem.Builder(
            confirmationType = taskItem.confirmationType,
            timeStampConfirmationUTC =  getCurrentDateServerFormat(),
            mandantId = taskItem.mandantId,
            taskId = taskItem.taskId,
            taskChangeId = taskItem.taskId, //todo: ask Tilman what is taskChangeId and why we use it
            text = null//todo: ask Tilman what is text and where we use text, why we send it user don`t put text anywhere
        )
        return confirmationItem.build()
    }

    fun getResIdByTaskActionType(task: TaskEntity): Int {
        return when (task.actionType) {
            ActionType.PICK_UP -> R.string.action_type_pick_up
            ActionType.DROP_OFF -> R.string.action_type_drop_off
            ActionType.GENERAL -> R.string.action_type_general
            ActionType.TRACTOR_SWAP -> R.string.action_type_tractor_swap
            ActionType.DELAY -> R.string.action_type_delay
            ActionType.UNKNOWN -> R.string.action_type_unknown
            ActionType.ENUM_ERROR -> R.string.action_type_unknown
        }
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
