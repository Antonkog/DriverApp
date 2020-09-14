package com.abona_erp.driver.app.data.model


import com.google.gson.annotations.SerializedName

data class TokenResponse(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("as:client_id")
    val asClientId: String,
    @SerializedName(".expires")
    val expires: String,
    @SerializedName("expires_in")
    val expiresIn: Int,
    @SerializedName(".issued")
    val issued: String,
    @SerializedName("refresh_token")
    val refreshToken: String,
    @SerializedName("refresh_token.expires")
    val refreshTokenExpires: String,
    @SerializedName("token_type")
    val tokenType: String
)