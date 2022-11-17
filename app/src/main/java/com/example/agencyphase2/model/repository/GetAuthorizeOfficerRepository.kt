package com.example.agencyphase2.model.repository

import com.example.agencyphase2.model.pojo.get_authorize_officer.GetAuthOfficerResponse
import com.example.agencyphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetAuthorizeOfficerRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun getAuthOfficer(
        token: String
    ): Flow<GetAuthOfficerResponse?> = flow{
        emit(apiInterface.getAuthorizeOfficer(token))
    }.flowOn(Dispatchers.IO)
}