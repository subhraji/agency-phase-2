package com.example.agencyphase2.model.pojo.get_clients

data class GetClientsResponse(
    val `data`: List<Data>,
    val http_status_code: Int,
    val message: String,
    val success: Boolean,
    val token: Any
)