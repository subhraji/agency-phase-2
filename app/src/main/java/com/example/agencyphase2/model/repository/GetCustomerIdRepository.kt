package com.example.agencyphase2.model.repository

import com.example.agencyphase2.di.qualifier.PaymentApiService
import com.example.agencyphase2.model.pojo.get_customer_id.GetCustomerIdResponse
import com.example.agencyphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetCustomerIdRepository @Inject constructor(@PaymentApiService private val apiInterface: ApiInterface){

    fun getCustomerId(
        token: String
    ): Flow<GetCustomerIdResponse?> = flow{
        emit(apiInterface.getCustomerId(token))
    }.flowOn(Dispatchers.IO)
}