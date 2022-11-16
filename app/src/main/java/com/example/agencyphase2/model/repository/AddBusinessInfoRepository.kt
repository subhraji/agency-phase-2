package com.example.agencyphase2.model.repository

import com.example.agencyphase2.model.pojo.business_information.BusinessInformationRequest
import com.example.agencyphase2.model.pojo.business_information.InsertBusinessInformationResponse
import com.example.agencyphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AddBusinessInfoRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun addBusinessInfo(
        phone: String,
        legal_structure: String,
        organization_type: String,
        tax_id_or_ein_id: String,
        street: String,
        city_or_district: String,
        state: String,
        zip_code: String,
        number_of_employee: Int? = null,
        years_in_business: Int? = null,
        country_of_business: String? = null,
        annual_business_revenue: String? = null,
        token: String
    ): Flow<InsertBusinessInformationResponse?> = flow{
        emit(apiInterface.addBusinessInfo(BusinessInformationRequest(
            phone, legal_structure, organization_type, tax_id_or_ein_id, street, city_or_district, state, zip_code, number_of_employee, years_in_business, country_of_business, annual_business_revenue
        ),token))
    }.flowOn(Dispatchers.IO)
}