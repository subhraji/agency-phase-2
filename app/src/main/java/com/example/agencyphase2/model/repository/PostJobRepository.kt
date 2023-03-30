package com.example.agencyphase2.model.repository

import com.example.agencyphase2.model.pojo.GenderAgeItemCountModel
import com.example.agencyphase2.model.pojo.job_post.JobPostRequest
import com.example.agencyphase2.model.pojo.job_post.JobPostResponse
import com.example.agencyphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class PostJobRepository @Inject constructor(private  val apiInterface: ApiInterface)  {
    fun postJob(
        title: String,
        care_type: String,
        care_items: List<GenderAgeItemCountModel>,
        date: String,
        start_time: String,
        end_time: String,
        amount: String,
        address: String,
        description: String,
        medical_history: List<String>? = null,
        expertise: List<String>? = null,
        other_requirements: List<String>? = null,
        check_list: List<String>? = null,
        short_address: String,
        lat: String,
        long: String,
        street: String,
        city: String,
        state: String,
        zipcode: String,
        appartment_or_unit: String? = null,
        floor_no: String? = null,
        country: String,
        token: String
    ): Flow<JobPostResponse?> = flow{
        emit(apiInterface.jobPost(
            JobPostRequest(
                title, care_type, care_items, date, start_time, end_time, amount, address, description, medical_history, expertise, other_requirements, check_list, short_address, lat, long, street, city, state, zipcode, appartment_or_unit, floor_no, country), token))
    }.flowOn(Dispatchers.IO)
}