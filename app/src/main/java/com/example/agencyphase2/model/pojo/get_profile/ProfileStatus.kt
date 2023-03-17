package com.example.agencyphase2.model.pojo.get_profile

data class ProfileStatus(
    val is_business_info_complete: Int,
    val is_other_info_added: Int,
    val is_authorize_info_added: Int,
    val is_profile_approved: Int
)
