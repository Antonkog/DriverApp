package com.abona_erp.driverapp.data.remote.utils.gson

import com.abona_erp.driverapp.data.local.db.ConfirmationType
import com.abona_erp.driverapp.data.model.ConfirmationItem
import com.google.gson.*
import java.lang.reflect.Type


class ConfirmationAdapterJson : JsonDeserializer<ConfirmationItem?>, JsonSerializer<ConfirmationItem> {


    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): ConfirmationItem? {
        return json?.asJsonObject?.let { jsonObject ->
                ConfirmationItem.Builder(
                    timeStampConfirmationUTC = jsonObject["TimeStampConfirmationUTC"].asString,
                    confirmationType = ConfirmationType.getByCode(jsonObject["ConfirmationType"].asInt),
                    mandantId = jsonObject["MandantId"].asInt,
                    taskId = jsonObject["TaskId"].asInt,
                    taskChangeId = jsonObject["TaskChangeId"].asInt,
                    text = jsonObject["Text"]?.asString
                ).build()
        }
    }

    override fun serialize(
        src: ConfirmationItem?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        val result = JsonObject()
        result.addProperty("ConfirmationType", src?.confirmationType?.code)
        src?.timeStampConfirmationUTC.let {
            result.addProperty("TimeStampConfirmationUTC", it)
        }
        result.addProperty("MandantId", src?.mandantId)
        result.addProperty("TaskId", src?.taskId)
        result.addProperty("TaskChangeId", src?.taskChangeId)
        result.addProperty("Text", src?.text)
        return result
    }
}