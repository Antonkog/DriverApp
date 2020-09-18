package com.abona_erp.driver.app.ui.events

class RxBusEvent {
//    data class ProgressEvent(
//        val showDialog: Boolean,
//        val message: String? = null
//    )
    data class LogOut(val logout: Boolean)
    data class FirebaseMessage(val message: String)
//    data class MessageEvent(val message: String)
}