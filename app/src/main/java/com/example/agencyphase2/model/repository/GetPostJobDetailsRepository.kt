package com.example.agencyphase2.model.repository

import com.example.agencyphase2.model.pojo.get_post_job_details.GetPostJobDetailsResponse
import com.example.agencyphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetPostJobDetailsRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun getPostJobDetails(
        token: String,
        id: Int
    ): Flow<GetPostJobDetailsResponse?> = flow{
        emit(apiInterface.getPostJobDetails(token, id))
    }.flowOn(Dispatchers.IO)
}