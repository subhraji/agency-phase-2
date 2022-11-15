package com.example.agencyphase2.retrofit

import com.example.agencyphase2.model.pojo.business_information.BusinessInformationRequest
import com.example.agencyphase2.model.pojo.business_information.InsertBusinessInformationResponse
import com.example.agencyphase2.model.pojo.login.LoginRequest
import com.example.agencyphase2.model.pojo.login.LoginResponse
import com.example.agencyphase2.model.pojo.logout.LogoutResponse
import com.example.agencyphase2.model.pojo.signup.SignUpRequest
import com.example.agencyphase2.model.pojo.signup.SignUpResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiInterface {
    /*@GET("todos")
    suspend fun getTodos(): GetTodosResponse?*/

    @POST("signup")
    suspend fun signup(@Body body: SignUpRequest?): SignUpResponse?

    @POST("login")
    suspend fun login(@Body body: LoginRequest?): LoginResponse?

    @POST("logout")
    suspend fun logout(@Header("Authorization") token: String): LogoutResponse?

    @POST("profile-registration")
    suspend fun addBusinessInfo(
        @Body body: BusinessInformationRequest?,
        @Header("Authorization") token: String): InsertBusinessInformationResponse?
}