package com.example.agencyphase2.model.pojo.edit_basic_info

data class EditBasicInfoResponse(
    val `data`: Data,
    val http_status_code: Int,
    val message: String,
    val success: Boolean,
    val token: Any
)