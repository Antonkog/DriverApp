package com.abona_erp.driverapp.ui.utils

import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by Anton Kogan on 10/9/2020
 * akogan777@gmail.com
 */
object JsonParser {
    val TAG = "JsonParser"

    /**
     * parse json object
     * @param objJson
     * @return  Map<String, String>
     * @throws JSONException
     */
    @Throws(JSONException::class)
    fun parseJson(objJson: Any?, map: HashMap<String, String>): Map<String, String> {
        return parseJson(objJson, map, null)
    }

    /**
     * parse json object
     * @param objJson
     * @param include - all  keys, that you want to display
     * @return  Map<String, String>
     * @throws JSONException
     */
    @Throws(JSONException::class)
    fun parseJson(
        objJson: Any?,
        map: HashMap<String, String>,
        include: Array<String>?
    ): Map<String, String> {
        // If obj is a json array
        if (objJson is JSONArray) {
            for (i in 0 until objJson.length()) {
                parseJson(objJson[i], map, include)
            }
        } else if (objJson is JSONObject) {
            val it: Iterator<*> = objJson.keys()
            while (it.hasNext()) {
                val key = it.next().toString()
                // If you get an array
                when (val jobject = objJson[key]) {
                    is JSONArray -> {
                        Log.e(TAG, " JSONArray: $jobject")
                        parseJson(
                            jobject, map, include
                        )
                    }
                    is JSONObject -> {
                        Log.d(TAG, " JSONObject: $jobject")
                        parseJson(
                            jobject, map, include
                        )
                    }
                    else -> {
                        if (include == null || include.contains(key)) // here is check for include param
                        {
                            map[key] = jobject.toString()
                            Log.d(TAG, " adding to map: $key $jobject")
                        }
                    }
                }
            }
        }
        return map
    }
}