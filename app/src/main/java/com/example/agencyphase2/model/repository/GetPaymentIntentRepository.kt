package com.example.agencyphase2.model.repository

import com.example.agencyphase2.di.qualifier.PaymentApiService
import com.example.agencyphase2.model.pojo.get_payment_intent.GetPaymentIntentResponse
import com.example.agencyphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetPaymentIntentRepository @Inject constructor(@PaymentApiService private val apiInterface: ApiInterface){

    fun getPaymentIntent(
        customer: String?,
        amount: String?,
        currency: String?,
        auto_pay_enable: String?,
        token: String?
    ): Flow<GetPaymentIntentResponse?> = flow{
        emit(apiInterface.getPaymentIntent(customer,amount,currency,auto_pay_enable,token))
    }.flowOn(Dispatchers.IO)
}