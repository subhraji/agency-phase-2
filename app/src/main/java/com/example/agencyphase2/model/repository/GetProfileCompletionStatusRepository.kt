package com.example.agencyphase2.model.repository

import com.example.agencyphase2.model.pojo.profile_completion_status.GetProfileCompletionStatusResponse
import com.example.agencyphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetProfileCompletionStatusRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun getProfileCompletionStatus(
        token: String
    ): Flow<GetProfileCompletionStatusResponse?> = flow{
        emit(apiInterface.getProfileCompletionStatus(token))
    }.flowOn(Dispatchers.IO)
}