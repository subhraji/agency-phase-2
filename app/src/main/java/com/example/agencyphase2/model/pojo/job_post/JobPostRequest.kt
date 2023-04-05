package com.example.agencyphase2.model.pojo.job_post

import com.example.agencyphase2.model.pojo.GenderAgeItemCountModel

data class JobPostRequest(
    val title: String,
    val care_type: String,
    val care_items: List<GenderAgeItemCountModel>,
    val start_date: String,
    val end_date: String,
    val start_time: String,
    val end_time: String,
    val amount: String,
    val address: String,
    val description: String,
    val medical_history: List<String>? = null,
    val expertise: List<String>? = null,
    val other_requirements: List<String>? = null,
    val check_list: List<String>? = null,
    val short_address: String,
    val lat: String,
    val long: String,
    val street: String,
    val city: String,
    val state: String,
    val zip_code: String,
    val appartment_or_unit: String? = null,
    val floor_no: String? = null,
    val country: String
)