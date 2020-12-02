package com.abona_erp.driverapp.ui.events

import android.net.Uri
import com.abona_erp.driverapp.MainViewModel
import com.abona_erp.driverapp.data.local.db.ChangeHistory
import com.abona_erp.driverapp.ui.fdocuments.UploadDocumentItem
import java.util.*
import kotlin.collections.ArrayList

class RxBusEvent {
    data class RequestStatus(val status: MainViewModel.Status)
    data class RetryRequest(val changeHistory: ChangeHistory)
    data class AuthError(val message: String)
    data class FirebaseMessage(val message: String)
    data class DocumentMessage(val documents: ArrayList<UploadDocumentItem>)
    data class LanguageUpdate(val locale: Locale)
    data class DocumentCropMessage(val uri: Uri)
//    data class MessageEvent(val message: String)
}