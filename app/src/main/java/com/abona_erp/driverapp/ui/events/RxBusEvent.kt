package com.abona_erp.driverapp.ui.events

import android.net.Uri
import com.abona_erp.driverapp.MainViewModel

class RxBusEvent {
    data class RequestStatus(val status: MainViewModel.Status)
    data class AuthError(val message: String)
    data class FirebaseMessage(val message: String)
    data class DocumentMessage(val uri: Uri)
//    data class MessageEvent(val message: String)
}