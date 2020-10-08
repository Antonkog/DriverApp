package com.abona_erp.driver.app.ui.utils

import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object JsonParser {

    val TAG = "JsonParser"
    /**
     * parse json object
     * @param objJson
     * @return  Map<String, String>
     * @throws JSONException
     */
    @Throws(JSONException::class)
    fun parseJson(objJson: Any?, map :HashMap<String, String>): Map<String, String> {
        // If obj is a json array
        if (objJson is JSONArray) {
            for (i in 0 until objJson.length()) {
                parseJson(objJson[i], map)
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
                            jobject, map
                        )
                    }
                    is JSONObject -> {
                        Log.e(TAG, " JSONObject: $jobject")
                        parseJson(
                            jobject, map
                        )
                    }
                    else -> {
//                        Log.e(TAG, " adding to map: $key $jobject")
                        map[key] = jobject.toString()
                    }
                }
            }
        }
        return map
    }
}