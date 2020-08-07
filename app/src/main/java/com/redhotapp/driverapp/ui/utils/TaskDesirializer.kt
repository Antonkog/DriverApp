package com.redhotapp.driverapp.ui.utils

import android.graphics.drawable.shapes.Shape
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.redhotapp.driverapp.data.model.abona.TaskItem
import java.lang.reflect.Type

class TaskDesirializer : JsonDeserializer<TaskItem> {


    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): TaskItem {
        val jsonObject = json?.asJsonObject
        if(jsonObject!=null){

            val taskDueDateFinish = jsonObject["TaskDueDateFinish"]
            val taskDueDateStart = jsonObject["TaskDueDateStart"]

        }

        return TaskItem()
    }
}