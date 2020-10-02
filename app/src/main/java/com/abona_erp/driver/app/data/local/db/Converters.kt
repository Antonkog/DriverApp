package com.abona_erp.driver.app.data.local.db

import androidx.room.TypeConverter
import com.abona_erp.driver.app.data.model.DelayReasonItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
     fun getTaskStatus(ordinal: Int): TaskStatus? {
        for (lt in TaskStatus.values()) {
            if (lt.ordinal == ordinal) {
                return lt
            }
        }
        return null
    }

    @TypeConverter
    fun getTaskStatusInt(type: TaskStatus?): Int? {
        return type?.ordinal
    }

    @TypeConverter
    fun getConfirmType(numeral: Int): ConfirmationType? {
        for (lt in ConfirmationType.values()) {
            if (lt.code == numeral) {
                return lt
            }
        }
        return null
    }

    @TypeConverter
    fun getConfirmTypeInt(type: ConfirmationType?): Int? {
        return type?.code
    }

    @TypeConverter
    fun stringToDelayReasons(json: String?): List<DelayReasonEntity>? {
        if (json == null) {
            return emptyList()
        }
        val type = object : TypeToken<List<DelayReasonEntity?>?>() {}.type
        return Gson().fromJson<List<DelayReasonEntity>>(json, type)
    }

    @TypeConverter
    fun delayReasonsToString(list: List<DelayReasonEntity>?): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun stringToIntList(data: String?): List<Int> {
        if (data == null) {
            return emptyList()
        }
        val listType =
            object : TypeToken<List<Int>>() {}.type
        return Gson().fromJson(data, listType)
    }


    @TypeConverter
    fun intListToString(someObjects: List<Int>): String {
        return Gson().toJson(someObjects)
    }

    @TypeConverter
    fun stringToContactsList(data: String?): List<Contact> {
        if (data == null) {
            return emptyList()
        }
        val listType =
            object : TypeToken<List<Contact>>() {}.type
        return Gson().fromJson(data, listType)
    }

    @TypeConverter
    fun stringToGoods(data: String?): List<DangerousGoods> {
        if (data == null) {
            return emptyList()
        }
        val listType =
            object : TypeToken<List<DangerousGoods>>() {}.type
        return Gson().fromJson(data, listType)
    }

    @TypeConverter
    fun contactListToString(someObjects: List<Contact>): String {
        return Gson().toJson(someObjects)
    }  @TypeConverter

    fun goodsToString(someObjects: List<DangerousGoods>): String {
        return Gson().toJson(someObjects)
    }
}