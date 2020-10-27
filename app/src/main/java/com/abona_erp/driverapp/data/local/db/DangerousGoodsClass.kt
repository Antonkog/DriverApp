package com.abona_erp.driverapp.data.local.db

import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import com.abona_erp.driverapp.R
import com.google.gson.annotations.SerializedName

enum class DangerousGoodsClass(val typeCode: Int) {
    @SerializedName("0")
    NA(0),
    @SerializedName("10")
    CLASS_1_EXPLOSIVES(10),
    @SerializedName("11")
    CLASS_1_1_EXPLOSIVES(11),
    @SerializedName("12")
    CLASS_1_2_EXPLOSIVES(12),
    @SerializedName("13")
    CLASS_1_3_EXPLOSIVES(13),
    @SerializedName("14")
    CLASS_1_4_EXPLOSIVES(14),
    @SerializedName("15")
    CLASS_1_5_EXPLOSIVES(15),
    @SerializedName("16")
    CLASS_1_6_EXPLOSIVES(16),
    @SerializedName("20")
    CLASS_2_FLAMMABLE_GAS(20),
    @SerializedName("21")
    CLASS_2_NON_FLAMMABLE_GAS(21),
    @SerializedName("22")
    CLASS_2_POISON_GAS(22),
    @SerializedName("30")
    CLASS_3_FLAMMABLE_LIQUID(30),
    @SerializedName("41")
    CLASS_4_1_FLAMMABLE_SOLIDS(41),
    @SerializedName("42")
    CLASS_4_2_SPONTANEOUSLY_COMBUSTIBLE(42),
    @SerializedName("43")
    CLASS_4_3_DANGEROUSE_WHEN_WET(43),
    @SerializedName("51")
    CLASS_5_1_OXIDIZER(51),
    @SerializedName("52")
    CLASS_5_2_ORAGNIC_PEROXIDES(52),
    @SerializedName("61")
    CLASS_6_1_POISON(61),
    @SerializedName("62")
    CLASS_6_2_INFECTIOUS_SUBSTANCE(62),
    @SerializedName("70")
    CLASS_7_FISSILE(70),
    @SerializedName("71")
    CLASS_7_RADIOACTIVE_I(71),
    @SerializedName("72")
    CLASS_7_RADIOACTIVE_II(72),
    @SerializedName("73")
    CLASS_7_RADIOACTIVE_III(73),
    @SerializedName("80")
    CLASS_8_CORROSIVE(80),
    @SerializedName("90")
    CLASS_9_MISCELLANEOUS(90),
    @SerializedName("-1")
    ENUM_ERROR(-1);


    companion object {
        fun getActionType(typeCode: Int): DangerousGoodsClass {
            for (lt in values()) {
                if (lt.typeCode == typeCode) {
                    return lt
                }
            }
            return ENUM_ERROR
        }
    }
}

