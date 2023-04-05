package com.example.agencyphase2.model.pojo.get_upcomming_jobs

data class Data(
    val address: String,
    val amount: String,
    val care_items: List<CareItem>,
    val care_type: String,
    val check_list: List<String>,
    val start_date: String,
    val end_date: String,
    val description: String,
    val end_time: String,
    val experties: List<String>,
    val job_accepted_by: JobAcceptedBy,
    val job_id: Int,
    val medical_history: List<String>,
    val other_requirements: List<String>,
    val short_address: String,
    val start_time: String,
    val status: String,
    val title: String
)