package com.example.agencyphase2.model.pojo.get_email_verify_otp

data class GetOtpResponse (
    val `data`: Any,
    val http_status_code: String,
    val message: String,
    val success: Boolean,
    val token: String,
    val verified_user_id: String
)