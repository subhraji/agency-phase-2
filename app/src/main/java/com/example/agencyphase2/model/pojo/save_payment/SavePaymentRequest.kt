package com.example.agencyphase2.model.pojo.save_payment

data class SavePaymentRequest(
    val job_id: Int,
    val amount: String,
    val customer_id: String,
    val caregiver_charge: String,
    val peaceworc_percentage: String,
    val peaceworc_charge: String,
    val Payment_status: String
)