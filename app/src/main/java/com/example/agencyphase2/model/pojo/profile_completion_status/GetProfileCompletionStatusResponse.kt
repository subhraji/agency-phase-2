package com.example.agencyphase2.model.pojo.profile_completion_status

data class GetProfileCompletionStatusResponse(
    val `data`: Data,
    val http_status_code: Int,
    val message: String,
    val success: Boolean,
    val token: Any
)