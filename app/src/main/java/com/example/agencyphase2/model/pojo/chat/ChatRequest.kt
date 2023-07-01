package com.example.agencyphase2.model.pojo.chat

data class ChatRequest(
    val msg: String,
    val userId: String,
    val time: String,
    val image: String
)