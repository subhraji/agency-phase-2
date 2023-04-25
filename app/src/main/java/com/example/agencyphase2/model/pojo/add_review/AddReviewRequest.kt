package com.example.agencyphase2.model.pojo.add_review

data class AddReviewRequest(
    val caregiver_id: String,
    val rating: String,
    val review: String
)