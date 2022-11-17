package com.example.agencyphase2.model.pojo.get_post_jobs

data class GetPostJobsResponse(
    val `data`: List<Data>,
    val http_status_code: Int,
    val message: String,
    val success: Boolean,
    val token: Any
)