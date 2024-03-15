package com.example.agencyphase2.model.pojo.delete_client

data class DeleteClientResponse(
    val `data`: Any,
    val http_status_code: Int,
    val message: String,
    val success: Boolean,
    val token: Any
)