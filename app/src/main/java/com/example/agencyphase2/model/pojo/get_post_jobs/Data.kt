package com.example.agencyphase2.model.pojo.get_post_jobs

data class Data(
    val current_page: Int,
    val `data`: List<DataX>,
    val first_page_url: String,
    val from: Int,
    val last_page: Int,
    val last_page_url: String,
    val links: List<Link>,
    val next_page_url: Any,
    val path: String,
    val per_page: String,
    val prev_page_url: Any,
    val to: Int,
    val total: Int
)