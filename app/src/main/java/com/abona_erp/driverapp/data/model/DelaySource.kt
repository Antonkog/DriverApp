package com.abona_erp.driverapp.data.model

import com.google.gson.*
import com.google.gson.annotations.JsonAdapter
import java.lang.reflect.Type

@JsonAdapter(DelaySource.Serializer::class)
enum class DelaySource(private val value: Int) {
    NA(0), DISPATCHER(1), CUSTOMER(2), DRIVER(3);

    internal class Serializer : JsonSerializer<DelaySource>, JsonDeserializer<DelaySource> {
        override fun serialize(
            src: DelaySource,
            typeOfSrc: Type,
            context: JsonSerializationContext
        ): JsonElement {
            return context.serialize(src.value)
        }

        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
        ): DelaySource {
            return try {
                getDelaySourceByCode(json.asNumber.toInt())
            } catch (e: JsonParseException) {
                NA
            }
        }
    }

    companion object {
        fun getDelaySourceByCode(delaySource: Int): DelaySource {
            for (ds in values()) {
                if (ds.value == delaySource) {
                    return ds
                }
            }
            return NA
        }
    }
}