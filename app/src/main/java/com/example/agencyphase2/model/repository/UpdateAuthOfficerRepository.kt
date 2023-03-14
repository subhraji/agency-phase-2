package com.example.agencyphase2.model.repository

import com.example.agencyphase2.model.pojo.update_auth_officer_status.UpdateAuthOfficerStatusResponse
import com.example.agencyphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UpdateAuthOfficerRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun updateAuthOfficerStatus(
        token: String
    ): Flow<UpdateAuthOfficerStatusResponse?> = flow{
        emit(apiInterface.updateAuthOfficerStatus(token))
    }.flowOn(Dispatchers.IO)
}