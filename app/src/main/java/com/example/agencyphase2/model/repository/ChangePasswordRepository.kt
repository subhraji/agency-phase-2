package com.example.agencyphase2.model.repository

import com.example.agencyphase2.model.pojo.change_password.ChangePasswordRequest
import com.example.agencyphase2.model.pojo.change_password.ChangePasswordResponse
import com.example.agencyphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ChangePasswordRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun changePassword(
        current_password: String,
        password: String,
        confirm_password: String,
        token: String
    ): Flow<ChangePasswordResponse?> = flow{
        emit(apiInterface.changePassword(ChangePasswordRequest(
            current_password, password, confirm_password
        ), token))
    }.flowOn(Dispatchers.IO)
}