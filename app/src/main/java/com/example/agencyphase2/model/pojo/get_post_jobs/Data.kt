package com.example.agencyphase2.model.pojo.get_post_jobs

data class Data(
    val address: String,
    val amount: String,
    val care_items: List<CareItem>,
    val care_type: String,
    val check_list: List<Any>,
    val created_at: String,
    val date: String,
    val description: String,
    val end_time: String,
    val experties: List<Any>,
    val id: Int,
    val medical_history: List<String>,
    val other_requirements: List<Any>,
    val start_time: String,
    val title: String,
    val updated_at: String,
    val user_id: Int,
    val status: String,
    val short_address: String,
    val lat: String,
    val long: String
)