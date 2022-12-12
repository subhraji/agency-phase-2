package com.example.agencyphase2.model.repository

import com.example.agencyphase2.model.pojo.delete_auth_officer.DeleteAuthOfficerResponse
import com.example.agencyphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DeleteAuthOfficerRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun deleteAuthOfficer(
        token: String,
        id: Int
    ): Flow<DeleteAuthOfficerResponse?> = flow{
        emit(apiInterface.deleteAuthOfficer(token,id))
    }.flowOn(Dispatchers.IO)
}