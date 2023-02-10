package com.example.agencyphase2.model.pojo.get_payment_intent

data class Card(
    val installments: Any,
    val mandate_options: Any,
    val network: Any,
    val request_three_d_secure: String
)