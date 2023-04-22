package com.example.agencyphase2.model.repository

import com.example.agencyphase2.model.pojo.caregiver_profile.GetCaregiverProfileResponse
import com.example.agencyphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetCaregiverProfileRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun getCaregiverProfile(
        token: String,
        job_id: String?
    ): Flow<GetCaregiverProfileResponse?> = flow{
        emit(apiInterface.getCaregiverProfile(token, job_id))
    }.flowOn(Dispatchers.IO)
}