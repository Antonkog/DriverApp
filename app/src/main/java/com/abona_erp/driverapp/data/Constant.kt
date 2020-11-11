package com.abona_erp.driverapp.data

object Constant {
    const val SUCCESS_CODE: Int = 200
    const val ERROR_NULL_CODE: Int = 408
    const val ERROR_COMMON: Int = 410

    const val CONNECTION_VELOCITY_SEC = 10L

    /**
     * that pause asked from Jaimon since we can't update Abona tables fast
     */
    const val PAUSE_SERVER_REQUEST_MIN = 1L

    const val OPEN_DOC_REQUEST_CODE = 122
    const val userAgentAppName = "ABONA DriverApp"
    const val baseRabbitUrl: String = "http://yms-test.abona-erp.com:50517/"
    const val grantTypeToken: String = "password"
    const val testMandantId: Int = 3
    const val defaultApiUrl = "https://213.144.11.162:5000"
    const val baseAuthUrl = "http://endpoint.abona-erp.com"
    const val abonaCommunicationDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    const val abonaCommunicationDateVarTwo = "yyyy-MM-dd'T'HH:mm:ss.SSS"
    const val abonaCommunicationDateVarThree = "yyyy-MM-dd'T'HH:mm:ss"


    const val abonaUIDateFormat = "yyyy-MM-dd"
    const val abonaUITimeFormat = "yyyy-MM-dd HH:mm"
    const val abonaUiDateLongFormat = "yyyy-MM-dd'T'HH:mm:ss"

    const val abonaTimeZone = "UTC"
    const val tokenUpdateHours = 48


    const val ERROR_REST_AUTH: Int = 401
    const val ERROR_REST_TIMEOUT: Int = 408


    //preferences
    const val preferencesId = "com.abona_erp.driverapp.preferences"
    const val preferencesEndpoint = "endpoint"
    const val token = "token"
    const val token_created = "token_created"
    const val tokenFcm = "tokenFcm"
    const val currentVechicle = "currentVechicle"
    const val currentVisibleTaskid = "current_visible_taskId"
    const val currentVisibleOrderId = "current_visible_OrderId"
    const val mandantId = "mandantId"

}