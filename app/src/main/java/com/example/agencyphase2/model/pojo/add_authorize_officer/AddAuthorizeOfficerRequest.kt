package com.example.agencyphase2.model.pojo.add_authorize_officer

data class AddAuthorizeOfficerRequest(
    val fullname: String,
    val email: String,
    val phone: String,
    val role : String
)
