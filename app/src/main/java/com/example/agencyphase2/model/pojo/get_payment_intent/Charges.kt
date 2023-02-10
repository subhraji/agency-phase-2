package com.example.agencyphase2.model.pojo.get_payment_intent

data class Charges(
    val `data`: List<Any>,
    val has_more: Boolean,
    val `object`: String,
    val total_count: Int,
    val url: String
)