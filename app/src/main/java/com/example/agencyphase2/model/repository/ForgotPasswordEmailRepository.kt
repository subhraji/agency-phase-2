package com.example.agencyphase2.model.repository

import com.example.agencyphase2.model.pojo.forgotpass_send_email.ForgotPassSendEmailRequest
import com.example.agencyphase2.model.pojo.forgotpass_send_email.ForgotPassSendEmailResponse
import com.example.agencyphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ForgotPasswordEmailRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun sendEmail(
        email: String
    ): Flow<ForgotPassSendEmailResponse?> = flow{
        emit(apiInterface.forgotPasswordEmail(ForgotPassSendEmailRequest(
            email
        )))
    }.flowOn(Dispatchers.IO)
}