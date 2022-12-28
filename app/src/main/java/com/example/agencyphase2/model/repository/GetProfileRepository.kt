package com.example.agencyphase2.model.repository

import com.example.agencyphase2.model.pojo.get_profile.GetProfileResponse
import com.example.agencyphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetProfileRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun getProfile(
        token: String
    ): Flow<GetProfileResponse?> = flow{
        emit(apiInterface.getProfile(token))
    }.flowOn(Dispatchers.IO)
}