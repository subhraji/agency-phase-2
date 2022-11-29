package com.example.agencyphase2.model.pojo.add_other_info

data class AddOtherInfoRequest(
    val number_of_employee: String? = null,
    val annual_business_revenue: String? = null,
    val years_in_business: String? = null,
    val legal_structure: String? = null,
    val organization_type: String? = null,
    val country_of_business: String? = null
)