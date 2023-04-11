package com.example.agencyphase2.model.pojo.get_profile

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Data(
    val annual_business_revenue: Any,
    val city_or_district: String,
    val company_name: String,
    val country_of_business: String,
    val created_at: String,
    val email: String,
    val id: Int,
    val legal_structure: String,
    val number_of_employee: String,
    val organization_type: String,
    val phone: String,
    val photo: String,
    val state: String,
    val status: String,
    val street: String,
    val tax_id_or_ein_id: String,
    val updated_at: String,
    val user_id: Int,
    val years_in_business: Int,
    val zip_code: String,
    val profile_completion_status: ProfileStatus,
    val revenue_count: Int,
    val rating_count: Int,
    val job_count: Int
): Parcelable