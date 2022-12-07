package com.example.agencyphase2.model.repository

import com.example.agencyphase2.model.pojo.delete_job.DeleteJobResponse
import com.example.agencyphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DeleteJobRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun deleteJob(
        token: String,
        id: Int
    ): Flow<DeleteJobResponse?> = flow{
        emit(apiInterface.deleteJob(token,id))
    }.flowOn(Dispatchers.IO)
}