package com.abona_erp.driver.app.ui.events

import android.net.Uri

class RxBusEvent {
//    data class ProgressEvent(
//        val showDialog: Boolean,
//        val message: String? = null
//    )
    data class LogOut(val logout: Boolean)
    data class FirebaseMessage(val message: String)
    data class DocumentMessage(val uri: Uri)
//    data class MessageEvent(val message: String)
}