package com.example.agencyphase2.model.repository

import com.example.agencyphase2.model.pojo.create_client.CreateClientResponse
import com.example.agencyphase2.model.pojo.get_clients.GetClientsResponse
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
        short_address: String,
        street: String,
        appartment_or_unit: String?,
        floor_no: String?,
        city: String,
        zip_code: String,
        state: String,
        country: String,
        lat: String,
        long: String,
        age: String,
        gender: String,
        token: String
    ): Flow<GetClientsResponse?> = flow{
        emit(apiInterface.createClient(
            photo,
            email.toMultipartFormString(),
            name.toMultipartFormString(),
            phone.toMultipartFormString(),
            address.toMultipartFormString(),
            short_address.toMultipartFormString(),
            street.toMultipartFormString(),
            appartment_or_unit?.toMultipartFormString(),
            floor_no?.toMultipartFormString(),
            city.toMultipartFormString(),
            zip_code.toMultipartFormString(),
            state.toMultipartFormString(),
            country.toMultipartFormString(),
            lat.toMultipartFormString(),
            long.toMultipartFormString(),
            age.toMultipartFormString(),
            gender.toMultipartFormString(),
            token
        )
        )
    }.flowOn(Dispatchers.IO)
}