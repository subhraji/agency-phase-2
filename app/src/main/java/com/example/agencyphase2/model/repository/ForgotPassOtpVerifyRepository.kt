package com.example.agencyphase2.model.repository

import com.example.agencyphase2.model.pojo.forgot_pass_otv_verify.ForgotPassOtpVerifyRequest
import com.example.agencyphase2.model.pojo.forgot_pass_otv_verify.ForgotPassOtpVerifyResponse
import com.example.agencyphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ForgotPassOtpVerifyRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun verifyOtp(
        email: String,
        otp: String
    ): Flow<ForgotPassOtpVerifyResponse?> = flow{
        emit(apiInterface.forgotPassOtpVerify(
            ForgotPassOtpVerifyRequest(
            email, otp
            )))
    }.flowOn(Dispatchers.IO)
}