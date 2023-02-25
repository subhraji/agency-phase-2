package com.example.agencyphase2.model.repository

import com.example.agencyphase2.model.pojo.update_payment_status.UpdatePaymentStatusRequest
import com.example.agencyphase2.model.pojo.update_payment_status.UpdatePaymentStatusResponse
import com.example.agencyphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UpdatePaymentStatusRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun updatePaymentStatus(
        job_id: Int,
        amount: Double,
        customer_id: String,
        caregiver_charge: Double,
        peaceworc_percentage: Int,
        peaceworc_charge: Double,
        payment_status: Int,
        token: String
    ): Flow<UpdatePaymentStatusResponse?> = flow{
        emit(apiInterface.updatePaymentStatus(UpdatePaymentStatusRequest(job_id,amount,customer_id,caregiver_charge,peaceworc_percentage,peaceworc_charge,payment_status),token))
    }.flowOn(Dispatchers.IO)
}