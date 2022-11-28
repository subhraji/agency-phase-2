package com.example.agencyphase2.model.pojo.business_information

data class BusinessInformationRequest(
   val phone: String,
   val email: String,
   val tax_id_or_ein_id: String,
   val street: String,
   val city_or_district: String,
   val state: String,
   val zip_code: String,
)