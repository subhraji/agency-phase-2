package com.example.agencyphase2.model.repository

import com.example.agencyphase2.model.pojo.edit_basic_info.EditBasicInfoRequest
import com.example.agencyphase2.model.pojo.edit_basic_info.EditBasicInfoResponse
import com.example.agencyphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class EditBasicInfoRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun editBasicInfo(
        phone: String? = null,
        legal_structure: String? = null,
        organization_type: String? = null,
        tax_id_or_ein_id: String? = null,
        street: String? = null,
        city_or_district: String? = null,
        state: String? = null,
        zip_code: String? = null,
        number_of_employee: Int? = null,
        years_in_business: Int? = null,
        country_of_business: String? = null,
        annual_business_revenue: String? = null,
        token: String
    ): Flow<EditBasicInfoResponse?> = flow{
        emit(apiInterface.editBasicInfo(EditBasicInfoRequest(
            phone, legal_structure, organization_type, tax_id_or_ein_id, street, city_or_district, state, zip_code, number_of_employee, years_in_business, country_of_business, annual_business_revenue
        ), token))
    }.flowOn(Dispatchers.IO)
}