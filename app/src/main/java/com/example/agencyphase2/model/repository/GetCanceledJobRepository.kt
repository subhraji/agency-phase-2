package com.example.agencyphase2.model.repository

import com.example.agencyphase2.model.pojo.get_canceled_job.GetCanceledJobResponse
import com.example.agencyphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetCanceledJobRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun getCanceledJob(
        token: String,
    ): Flow<GetCanceledJobResponse?> = flow{
        emit(apiInterface.getCanceledJob(token))
    }.flowOn(Dispatchers.IO)
}