package com.example.agencyphase2.model.repository

import com.example.agencyphase2.model.pojo.save_payment.SavePaymentRequest
import com.example.agencyphase2.model.pojo.save_payment.SavePaymentResponse
import com.example.agencyphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SavePaymentRepository @Inject constructor(private  val apiInterface: ApiInterface)  {
    fun savePayment(
        job_id: Int,
        amount: String,
        customer_id: String,
        caregiver_charge: String,
        peaceworc_percentage: String,
        peaceworc_charge: String,
        Payment_status: String,
        token: String
    ): Flow<SavePaymentResponse?> = flow{
        emit(apiInterface.savePayment(
            SavePaymentRequest(
                job_id, amount, customer_id, caregiver_charge, peaceworc_percentage, peaceworc_charge, Payment_status
            ), token))
    }.flowOn(Dispatchers.IO)
}