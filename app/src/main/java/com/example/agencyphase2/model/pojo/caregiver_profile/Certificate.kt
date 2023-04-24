package com.example.agencyphase2.model.pojo.caregiver_profile

data class Certificate(
    val certificate_or_course: String,
    val created_at: String,
    val document: String,
    val end_year: String,
    val id: Int,
    val start_year: String,
    val updated_at: String,
    val user_id: Int
)