package com.example.agencyphase2.model.repository

import com.example.agencyphase2.di.qualifier.PaymentApiService
import com.example.agencyphase2.model.pojo.get_ephemeral_key.GetEphemeralKeyResponse
import com.example.agencyphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetEphemeralKeyRepository @Inject constructor(@PaymentApiService private val apiInterface: ApiInterface){

    fun getEphemeralKey(
        customer: String?,
        token: String,
        stripeVersion: String,
    ): Flow<GetEphemeralKeyResponse?> = flow{
        emit(apiInterface.getEphemeralKey(customer, token, stripeVersion))
    }.flowOn(Dispatchers.IO)
}