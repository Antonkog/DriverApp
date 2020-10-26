package com.abona_erp.driverapp.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NotesItem(
    @SerializedName("NoteType")
    val noteType: EnumNoteType?,
    @SerializedName("Note")
    val note: String?
) : Parcelable