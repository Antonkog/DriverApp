package com.abona_erp.driver.app.data

object Constant {
    const val SUCCESS_CODE: Int = 200
    const val ERROR_NULL_CODE: Int = 408
    const val ERROR_COMMON: Int = 410
    const val userAgentAppName = "ABONA DriverApp"
    const val baseRabbitUrl: String = "http://yms-test.abona-erp.com:50517/"
    const val grantTypeToken: String = "password"
    const val testMandantId: Int = 3
    const val defaultApiUrl = "https://213.144.11.162:5000"
    const val baseAuthUrl = "http://endpoint.abona-erp.com"
    const val abonaDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    const val abonaTimeZone = "UTC"
    const val tokenUpdateHours = 48


    //preferences
    const val preferencesId = "com.abona_erp.driver.app.preferences"
    const val preferencesEndpoint = "endpoint"
    const val token = "token"
    const val token_created = "token_created"
    const val prefShowAll = "show_all"
    const val tokenFcm = "tokenFcm"
    const val currentVisibleTaskid = "current_visible_taskId"
    const val currentVisibleOrderId = "current_visible_OrderId"
    const val mandantId = "mandantId"

}