package com.example.agencyphase2.model.repository.signup

import com.example.agencyphase2.model.pojo.signup.SignUpRequest
import com.example.agencyphase2.model.pojo.signup.SignUpResponse
import com.example.agencyphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SignUpRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun signup(
        name: String,
        email: String,
        password: String,
        con_password: String
    ): Flow<SignUpResponse?> = flow{
        emit(apiInterface.signup(SignUpRequest(name,email,password,con_password)))
    }.flowOn(Dispatchers.IO)
}