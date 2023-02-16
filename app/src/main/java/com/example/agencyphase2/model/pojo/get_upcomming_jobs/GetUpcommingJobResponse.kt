package com.example.agencyphase2.model.pojo.get_upcomming_jobs

data class GetUpcommingJobResponse(
    val `data`: List<Data>,
    val http_status_code: Int,
    val message: String,
    val success: Boolean,
    val token: Any
)