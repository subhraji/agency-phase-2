package com.example.agencyphase2.model.pojo.job_post

import com.example.agencyphase2.model.pojo.GenderAgeItemCountModel

data class JobPostRequest(
    val title: String,
    val care_type: String,
    val care_items: List<GenderAgeItemCountModel>,
    val date: String,
    val start_time: String,
    val end_time: String,
    val amount: String,
    val address: String,
    val description: String
)