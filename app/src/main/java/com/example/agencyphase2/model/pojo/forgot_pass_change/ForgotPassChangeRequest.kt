package com.example.agencyphase2.model.pojo.forgot_pass_change

data class ForgotPassChangeRequest(
    val email: String,
    val password: String,
    val confirm_password: String,
    val fcm_token: String
)