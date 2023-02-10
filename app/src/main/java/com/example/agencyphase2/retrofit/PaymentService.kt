package com.example.agencyphase2.retrofit

import com.example.agencyphase2.model.pojo.get_customer_id.GetCustomerIdResponse
import com.example.agencyphase2.model.pojo.get_ephemeral_key.GetEphemeralKeyResponse
import com.example.agencyphase2.model.pojo.get_payment_intent.GetPaymentIntentResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface PaymentService {
    @POST("customers")
    suspend fun getCustomerId(
        @Header("Authorization") token: String,
    ): GetCustomerIdResponse?

    @FormUrlEncoded
    @POST("ephemeral_keys")
    suspend fun getEphemeralKey(
        @Field("customer") customer: String?,
        @Header("Authorization") token: String,
        @Header("Stripe-Version") stripeVersion: String
    ): GetEphemeralKeyResponse?

    @FormUrlEncoded
    @POST("payment_intents")
    suspend fun getPaymentIntent(
        @Field("customer") customer: String?,
        @Field("amount") amount: String?,
        @Field("currency") currency: String?,
        @Field("automatic_payment_methods[enabled]") auto_pay_enable: String?,
        @Header("Authorization") token: String?,
    ): GetPaymentIntentResponse?
}