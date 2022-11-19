package com.example.agencyphase2.model.pojo.profile_completion_status

data class Data(
    val created_at: String,
    val id: Int,
    val is_authorize_info_added: Int,
    val is_profile_approved: Int,
    val is_registration_complete: Int,
    val updated_at: String,
    val user_id: Int
)