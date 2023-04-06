package com.example.agencyphase2.model.repository

import com.example.agencyphase2.model.pojo.get_upcomming_jobs.GetUpcommingJobResponse
import com.example.agencyphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetUpcommingJobRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun getUpcomminJobs(
        token: String,
        id: Int?
    ): Flow<GetUpcommingJobResponse?> = flow{
        emit(apiInterface.getUpcomminJobs(token, id))
    }.flowOn(Dispatchers.IO)
}