package com.abona_erp.driver.app.data.local.db
import com.google.gson.annotations.SerializedName

enum class TaskStatus (var status: Int){ //
    @SerializedName("0") PENDING(0),
    @SerializedName("50") RUNNING(50),
    @SerializedName("51") BREAK(51),
    @SerializedName("90") CMR(90),
    @SerializedName("100") FINISHED(100)
}

