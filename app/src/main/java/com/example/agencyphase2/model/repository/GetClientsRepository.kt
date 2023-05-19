package com.example.agencyphase2.model.repository

import com.example.agencyphase2.model.pojo.get_clients.GetClientsResponse
import com.example.agencyphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetClientsRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun getClients(
        token: String
    ): Flow<GetClientsResponse?> = flow{
        emit(apiInterface.getClients(token))
    }.flowOn(Dispatchers.IO)
}