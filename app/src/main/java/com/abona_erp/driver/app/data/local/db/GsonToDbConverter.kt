package com.abona_erp.driver.app.data.local.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GsonToDbConverter {
    @TypeConverter
    fun stringToSomeObjectList(data: String?): List<Any> {
        if (data == null) {
            return emptyList()
        }
        val listType =
            object : TypeToken<List<Any>>() {}.type
        return Gson().fromJson(data, listType)
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
    fun someObjectListToString(someObjects: List<Any>): String {
        return Gson().toJson(someObjects)
    }

    @TypeConverter
    fun intListToString(someObjects: List<Int>): String {
        return Gson().toJson(someObjects)
    }
    @TypeConverter
    fun contactListToString(someObjects: List<Contact>): String {
        return Gson().toJson(someObjects)
    }  @TypeConverter

    fun goodsToString(someObjects: List<DangerousGoods>): String {
        return Gson().toJson(someObjects)
    }
}