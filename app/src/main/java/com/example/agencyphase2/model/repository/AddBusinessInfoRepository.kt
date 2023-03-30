package com.example.agencyphase2.model.repository

import com.example.agencyphase2.model.pojo.business_information.InsertBusinessInformationResponse
import com.example.agencyphase2.retrofit.ApiInterface
import com.user.caregiver.toMultipartFormString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import javax.inject.Inject

class AddBusinessInfoRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun addBusinessInfo(
        photo: MultipartBody.Part?,
        phone: String,
        email: String,
        tax_id_or_ein_id: String,
        street: String,
        city_or_district: String,
        state: String,
        zip_code: String,
        country: String,
        token: String
    ): Flow<InsertBusinessInformationResponse?> = flow{
        emit(apiInterface.addBusinessInfo(
            photo,
            phone.toMultipartFormString(),
            email.toMultipartFormString(),
            tax_id_or_ein_id.toMultipartFormString(),
            street.toMultipartFormString(),
            city_or_district.toMultipartFormString(),
            state.toMultipartFormString(),
            zip_code.toMultipartFormString(),
            country.toMultipartFormString(),
            token
        ))
    }.flowOn(Dispatchers.IO)
}