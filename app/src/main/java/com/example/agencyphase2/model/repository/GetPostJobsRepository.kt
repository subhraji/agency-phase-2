package com.example.agencyphase2.model.repository

import com.example.agencyphase2.model.pojo.get_post_jobs.GetPostJobsResponse
import com.example.agencyphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetPostJobsRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun getPostJobs(
        token: String
    ): Flow<GetPostJobsResponse?> = flow{
        emit(apiInterface.getPostJobs(token))
    }.flowOn(Dispatchers.IO)
}