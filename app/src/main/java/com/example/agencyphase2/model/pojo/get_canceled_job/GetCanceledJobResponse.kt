package com.example.agencyphase2.model.pojo.get_canceled_job

data class GetCanceledJobResponse(
    val `data`: Data,
    val http_status_code: Int,
    val message: String,
    val success: Boolean,
    val token: Any
)