package com.example.agencyphase2.model.repository

import com.example.agencyphase2.model.pojo.forgot_pass_change.ForgotPassChangeRequest
import com.example.agencyphase2.model.pojo.forgot_pass_change.ForgotPassChangeResponse
import com.example.agencyphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ForgotPassChangeRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun forgotPassChange(
        email: String,
        password: String,
        confirm_password: String,
        fcm_token: String
    ): Flow<ForgotPassChangeResponse?> = flow{
        emit(apiInterface.updatePass(
            ForgotPassChangeRequest(
                email, password, confirm_password, fcm_token
            )
        ))
    }.flowOn(Dispatchers.IO)
}