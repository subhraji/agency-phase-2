package com.example.agencyphase2.model.pojo.create_client

data class CreateClientResponse(
    val `data`: Any,
    val http_status_code: Int,
    val message: String,
    val success: Boolean,
    val token: Any
)