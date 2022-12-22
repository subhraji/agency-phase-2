package com.example.agencyphase2.model.repository

import com.example.agencyphase2.model.pojo.change_owner_phone.ChangeOwnerPhoneRequest
import com.example.agencyphase2.model.pojo.change_owner_phone.ChangeOwnerPhoneResponse
import com.example.agencyphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ChangeOwnerPhoneRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun changeOwnerPhone(
        phone: String,
        token: String
    ): Flow<ChangeOwnerPhoneResponse?> = flow{
        emit(apiInterface.changeOwnerPhone(
            ChangeOwnerPhoneRequest(
            phone,
        ), token))
    }.flowOn(Dispatchers.IO)
}