package com.example.agencyphase2.model.pojo.get_email_verify_otp

data class GetOtpRequest(
    val email: String,
    val otp: String,
    val company_name: String
)