package com.example.agencyphase2.model.pojo.login

data class LoginResponse(
    val `data`: Data,
    val http_status_code: Int,
    val message: String,
    val success: Boolean,
    val token: String
)