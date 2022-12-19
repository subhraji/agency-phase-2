package com.example.agencyphase2.model.repository

import com.example.agencyphase2.model.pojo.get_email_verify_otp.GetOtpRequest
import com.example.agencyphase2.model.pojo.get_email_verify_otp.GetOtpResponse
import com.example.agencyphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetEmailVerificationOtpRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun getOtp(
        email: String
    ): Flow<GetOtpResponse?> = flow{
        emit(apiInterface.getEmailVerificationOtp(GetOtpRequest(email)))
    }.flowOn(Dispatchers.IO)
}