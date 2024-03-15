package com.example.agencyphase2.model.repository

import com.example.agencyphase2.model.pojo.delete_auth_officer.DeleteAuthOfficerResponse
import com.example.agencyphase2.model.pojo.delete_client.DeleteClientRequest
import com.example.agencyphase2.model.pojo.delete_client.DeleteClientResponse
import com.example.agencyphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DeleteClientRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun deleteClient(
        id: String,
        token: String,
    ): Flow<DeleteClientResponse?> = flow{
        emit(apiInterface.deleteClient(DeleteClientRequest(
            id
        ), token))
    }.flowOn(Dispatchers.IO)
}