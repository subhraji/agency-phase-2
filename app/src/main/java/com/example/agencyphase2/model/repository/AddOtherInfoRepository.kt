package com.example.agencyphase2.model.repository

import com.example.agencyphase2.model.pojo.add_other_info.AddOtherInfoRequest
import com.example.agencyphase2.model.pojo.add_other_info.AddOtherInfoResponse
import com.example.agencyphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AddOtherInfoRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun addOtherInfo(
        number_of_employee: String,
        annual_business_revenue: String,
        years_in_business: String,
        legal_structure: String,
        organization_type: String,
        country_of_business: String,
        token: String
    ): Flow<AddOtherInfoResponse?> = flow{
        emit(apiInterface.addOtherInfo(AddOtherInfoRequest(number_of_employee, annual_business_revenue, years_in_business, legal_structure, organization_type, country_of_business), token))
    }.flowOn(Dispatchers.IO)
}