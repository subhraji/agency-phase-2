package com.example.agencyphase2.model.repository

import com.example.agencyphase2.model.pojo.create_client.CreateClientResponse
import com.example.agencyphase2.retrofit.ApiInterface
import com.user.caregiver.toMultipartFormString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import javax.inject.Inject

class CreateClientRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun createClient(
        photo: MultipartBody.Part?,
        email: String,
        name: String,
        phone: String,
        address: String,
        token: String
    ): Flow<CreateClientResponse?> = flow{
        emit(apiInterface.createClient(
            photo,
            email.toMultipartFormString(),
            name.toMultipartFormString(),
            phone.toMultipartFormString(),
            address.toMultipartFormString(),
            token)
        )
    }.flowOn(Dispatchers.IO)
}