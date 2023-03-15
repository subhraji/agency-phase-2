package com.example.agencyphase2.model.pojo.get_post_jobs

data class DataX(
    val address: String,
    val amount: String,
    val bidding_end_time: Any,
    val bidding_start_time: Any,
    val care_items: List<CareItem>,
    val care_type: String,
    val check_list: List<String>,
    val created_at: String,
    val date: String,
    val deleted_at: Any,
    val description: String,
    val end_time: String,
    val experties: List<String>,
    val id: Int,
    val lat: String,
    val long: String,
    val medical_history: List<String>,
    val other_requirements: List<String>,
    val payment_status: Int,
    val short_address: String,
    val start_time: String,
    val status: String,
    val title: String,
    val updated_at: String,
    val user_id: Int
)