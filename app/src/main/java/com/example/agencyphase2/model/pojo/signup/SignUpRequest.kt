package com.example.agencyphase2.model.pojo.signup

data class SignUpRequest(
    val otp: Int,
    val company_name: String,
    val name: String,
    val email: String,
    val password: String,
    val confirm_password: String,
    val fcm_token: String
)