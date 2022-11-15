package com.example.agencyphase2.model.pojo.business_information

data class InsertBusinessInformationResponse(
    val `data`: Any,
    val http_status_code: Int,
    val message: String,
    val success: Boolean,
    val token: Any
)