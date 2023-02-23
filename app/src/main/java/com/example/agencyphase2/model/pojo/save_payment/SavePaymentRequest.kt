package com.example.agencyphase2.model.pojo.save_payment

data class SavePaymentRequest(
    val job_id: Int,
    val amount: Double,
    val customer_id: String,
    val caregiver_charge: Double,
    val peaceworc_percentage: Double,
    val peaceworc_charge: Double,
    val Payment_status: String
)