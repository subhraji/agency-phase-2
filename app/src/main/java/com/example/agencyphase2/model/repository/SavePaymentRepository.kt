package com.example.agencyphase2.model.repository

import com.example.agencyphase2.model.pojo.save_payment.SavePaymentRequest
import com.example.agencyphase2.model.pojo.save_payment.SavePaymentsResponse
import com.example.agencyphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SavePaymentRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun savePayment(
        job_id: Int,
        amount: Double,
        customer_id: String,
        caregiver_charge: Double,
        peaceworc_percentage:Double,
        peaceworc_charge: Double,
        Payment_status:String,
        token: String
    ): Flow<SavePaymentsResponse?> = flow{
        emit(apiInterface.savePayment(SavePaymentRequest(
            job_id,
            amount,
            customer_id,
            caregiver_charge,
            peaceworc_percentage,
            peaceworc_charge,
            Payment_status
        ), token))
    }.flowOn(Dispatchers.IO)
}