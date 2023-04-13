package com.example.agencyphase2.model.repository

import com.example.agencyphase2.model.pojo.edit_basic_info.EditBasicInfoRequest
import com.example.agencyphase2.model.pojo.edit_basic_info.EditBasicInfoResponse
import com.example.agencyphase2.retrofit.ApiInterface
import com.user.caregiver.toMultipartFormString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import javax.inject.Inject

class EditBasicInfoRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun editBasicInfo(
        photo: MultipartBody.Part? = null,
        phone: String? = null,
        legal_structure: String? = null,
        organization_type: String? = null,
        tax_id_or_ein_id: String? = null,
        street: String? = null,
        city_or_district: String? = null,
        state: String? = null,
        zip_code: String? = null,
        number_of_employee: String? = null,
        years_in_business: String? = null,
        country_of_business: String? = null,
        annual_business_revenue: String? = null,
        token: String
    ): Flow<EditBasicInfoResponse?> = flow{
        emit(apiInterface.editBasicInfo(
            photo,
            phone?.toMultipartFormString(),
            legal_structure?.toMultipartFormString(),
            organization_type?.toMultipartFormString(),
            tax_id_or_ein_id?.toMultipartFormString(),
            street?.toMultipartFormString(),
            city_or_district?.toMultipartFormString(),
            state?.toMultipartFormString(),
            zip_code?.toMultipartFormString(),
            number_of_employee?.toMultipartFormString(),
            years_in_business?.toMultipartFormString(),
            country_of_business?.toMultipartFormString(),
            annual_business_revenue?.toMultipartFormString(),
            token
        ))
    }.flowOn(Dispatchers.IO)
}