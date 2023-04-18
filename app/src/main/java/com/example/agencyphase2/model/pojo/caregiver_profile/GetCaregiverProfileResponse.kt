package com.example.agencyphase2.model.pojo.caregiver_profile

data class GetCaregiverProfileResponse(
    val `data`: Data,
    val http_status_code: Int,
    val message: String,
    val success: Boolean,
    val token: Any
)