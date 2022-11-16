package com.example.agencyphase2.model.pojo.job_post

data class JobPostResponse(
    val `data`: Any,
    val http_status_code: Int,
    val message: String,
    val success: Boolean,
    val token: Any
)