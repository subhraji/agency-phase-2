package com.example.agencyphase2.model.pojo.delete_job

data class DeleteJobResponse(
    val `data`: Any,
    val http_status_code: Int,
    val message: String,
    val success: Boolean,
    val token: Any
)