package com.example.agencyphase2.model.repository

import com.example.agencyphase2.model.pojo.get_care_type.GetCareTypeResponse
import com.example.agencyphase2.model.pojo.get_complete_jobs.GetCompleteJobsResponse
import com.example.agencyphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetCompletedJobsRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun getCompleteJob(
        token: String,
        page: Int
    ): Flow<GetCompleteJobsResponse?> = flow{
        emit(apiInterface.getCompletedJob(token, page))
    }.flowOn(Dispatchers.IO)
}