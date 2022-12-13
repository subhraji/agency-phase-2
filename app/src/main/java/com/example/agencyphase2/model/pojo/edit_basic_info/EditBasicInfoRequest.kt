package com.example.agencyphase2.model.pojo.edit_basic_info

data class EditBasicInfoRequest(
    val photo: String? = null,
    val phone: String? = null,
    val legal_structure: String? = null,
    val organization_type: String? = null,
    val tax_id_or_ein_id: String? = null,
    val street: String? = null,
    val city_or_district: String? = null,
    val state: String? = null,
    val zip_code: String? = null,
    val number_of_employee: Int? = null,
    val years_in_business: Int? = null,
    val country_of_business: String? = null,
    val annual_business_revenue: String? = null
)
