package com.example.agencyphase2.model.repository

import com.example.agencyphase2.model.pojo.search_job.SearchJobRequest
import com.example.agencyphase2.model.pojo.search_job.SearchJobResponse
import com.example.agencyphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SearchJobRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun searchJob(
        job_status:String?,
        token: String
    ): Flow<SearchJobResponse?> = flow{
        emit(apiInterface.searchJob(
            SearchJobRequest(
            job_status
        ), token))
    }.flowOn(Dispatchers.IO)
}