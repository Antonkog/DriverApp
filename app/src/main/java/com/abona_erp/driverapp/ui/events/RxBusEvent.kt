package com.abona_erp.driverapp.ui.events

import android.net.Uri
import com.abona_erp.driverapp.MainViewModel
import com.abona_erp.driverapp.data.local.db.ChangeHistory
import java.util.*

class RxBusEvent {
    data class RequestStatus(val status: MainViewModel.Status)
    data class RetryRequest(val changeHistory: ChangeHistory)
    data class AuthError(val message: String)
    data class FirebaseMessage(val message: String)
    data class DocumentMessage(val uri: Uri)
    data class LanguageUpdate(val locale: Locale)
//    data class MessageEvent(val message: String)
}