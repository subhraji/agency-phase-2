package com.example.agencyphase2.model.repository

import com.example.agencyphase2.di.PaymentApiClient
import com.example.agencyphase2.model.pojo.get_customer_id.GetCustomerIdResponse
import com.example.agencyphase2.model.pojo.get_ephemeral_key.GetEphemeralKeyResponse
import com.example.agencyphase2.retrofit.ApiInterface
import com.example.agencyphase2.retrofit.PaymentService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetEphemeralKeyRepository @Inject constructor(){
    private val apiService = PaymentApiClient.getInstance()

    fun getEphemeralKey(
        customer: String?,
        token: String,
        stripeVersion: String,
    ): Flow<GetEphemeralKeyResponse?> = flow{
        emit(apiService.getEphemeralKey(customer, token, stripeVersion))
    }.flowOn(Dispatchers.IO)
}