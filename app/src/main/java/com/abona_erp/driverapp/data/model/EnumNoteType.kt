package com.abona_erp.driverapp.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Anton Kogan on 10/9/2020
 * akogan777@gmail.com
 */
@Parcelize
enum class EnumNoteType(var typeInt: Int) : Parcelable {
    STANDARD(0),
    HIGH(1),
    WRONG_VALUE(-1);

    companion object {
        fun getNoteTypeByCode(intType: Int): EnumNoteType {
            for (note in values()) {
                if (note.typeInt == intType) return note
            }
            return EnumNoteType.WRONG_VALUE
        }

        fun getNoteIntByType(noteType: EnumNoteType): Int {
            for (note in EnumNoteType.values()) {
                if (note == noteType) return note.typeInt
            }
            return 0
        }
    }

}