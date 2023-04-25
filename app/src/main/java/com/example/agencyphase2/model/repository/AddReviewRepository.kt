package com.example.agencyphase2.model.repository

import com.example.agencyphase2.model.pojo.add_review.AddReviewRequest
import com.example.agencyphase2.model.pojo.add_review.AddReviewResponse
import com.example.agencyphase2.retrofit.ApiInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
class AddReviewRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun addReview(
        caregiver_id: String,
        rating: String,
        review: String,
        token: String
    ): Flow<AddReviewResponse?> = flow{
        emit(apiInterface.addReview(AddReviewRequest(
            caregiver_id, rating, review
        ), token))
    }.flowOn(Dispatchers.IO)
}