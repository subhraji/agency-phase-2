package com.example.agencyphase2.model.pojo.add_authorize_officer

data class AddAuthorizeOfficerResponse(
    val `data`: List<Data>,
    val http_status_code: Int,
    val message: String,
    val success: Boolean,
    val token: Any
)