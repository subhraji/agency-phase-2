package com.example.agencyphase2.model.pojo.business_information

data class BusinessInformationRequest(
   val phone: String,
   val legal_structure: String,
   val organization_type: String,
   val tax_id_or_ein_id: String,
   val street: String,
   val city_or_district: String,
   val state: String,
   val zip_code: String,
   val number_of_employee: Int? = null,
   val years_in_business: Int? = null,
   val country_of_business: String? = null,
   val annual_business_revenue: String? = null
)