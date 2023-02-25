package com.example.agencyphase2.model.pojo.update_payment_status

data class UpdatePaymentStatusRequest(
    val job_id: Int,
    val amount: Double,
    val customer_id: String,
    val caregiver_charge: Double,
    val peaceworc_percentage: Int,
    val peaceworc_charge: Double,
    val payment_status: Int
)