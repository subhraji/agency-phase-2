package com.example.agencyphase2.model.pojo.signup

data class SignUpResponse(
    val `data`: Any,
    val http_status_code: String,
    val message: String,
    val success: Boolean,
    val token: Any
)