package com.abona_erp.driver.app.data.local.db
import com.google.gson.annotations.SerializedName

enum class TaskStatus{ //(var taskStatus: Int)
    @SerializedName("0") PENDING,
    @SerializedName("50") RUNNING,
    @SerializedName("51") BREAK,
    @SerializedName("90") CMR,
    @SerializedName("100") FINISHED
}

