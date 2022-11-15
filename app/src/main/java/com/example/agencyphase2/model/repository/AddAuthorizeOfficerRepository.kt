package com.example.agencyphase2.model.repository

import com.example.agencyphase2.model.pojo.add_authorize_officer.AddAuthorizeOfficerRequest
import com.example.agencyphase2.model.pojo.add_authorize_officer.AddAuthorizeOfficerResponse
import com.example.agencyphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AddAuthorizeOfficerRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun addAuthorizeOfficer(
        firstname: String ,
        lastname: String,
        email: String,
        phone: String,
        token: String
    ): Flow<AddAuthorizeOfficerResponse?> = flow{
        emit(apiInterface.addAuthorizeOfficer(AddAuthorizeOfficerRequest(
            firstname, lastname, email, phone
        ), token))
    }.flowOn(Dispatchers.IO)
}