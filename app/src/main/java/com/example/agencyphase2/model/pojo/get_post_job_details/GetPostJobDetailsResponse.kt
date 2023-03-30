package com.example.agencyphase2.model.pojo.get_post_job_details

data class GetPostJobDetailsResponse(
    val `data`: Data,
    val http_status_code: Int,
    val message: String,
    val success: Boolean,
    val token: Any
)