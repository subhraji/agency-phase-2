package com.example.agencyphase2.model.pojo.save_payment

data class SavePaymentsResponse(
    val `data`: Any,
    val http_status_code: Int,
    val message: String,
    val success: Boolean,
    val token: Any
)