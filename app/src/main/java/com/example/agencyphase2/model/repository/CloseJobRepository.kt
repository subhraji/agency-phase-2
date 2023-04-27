package com.example.agencyphase2.model.repository

import com.example.agencyphase2.model.pojo.close_job.CloseJobRequest
import com.example.agencyphase2.model.pojo.close_job.CloseJobResponse
import com.example.agencyphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CloseJobRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun closeJob(
        job_id: String,
        token: String
    ): Flow<CloseJobResponse?> = flow{
        emit(apiInterface.closeJob(
            CloseJobRequest(
                job_id
        ), token))
    }.flowOn(Dispatchers.IO)
}