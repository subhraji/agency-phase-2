package com.example.agencyphase2.model.repository

import com.example.agencyphase2.model.pojo.get_care_type.GetCareTypeResponse
import com.example.agencyphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetCareTypeRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun getCareType(
        token: String
    ): Flow<GetCareTypeResponse?> = flow{
        emit(apiInterface.getCareType(token))
    }.flowOn(Dispatchers.IO)
}