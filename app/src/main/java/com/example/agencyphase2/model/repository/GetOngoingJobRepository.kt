package com.example.agencyphase2.model.repository

import com.example.agencyphase2.model.pojo.get_ongoing_job.GetOngoingJobsResponse
import com.example.agencyphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetOngoingJobRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun getOngoingJob(
        token: String
    ): Flow<GetOngoingJobsResponse?> = flow{
        emit(apiInterface.getOngoingJob(token))
    }.flowOn(Dispatchers.IO)
}