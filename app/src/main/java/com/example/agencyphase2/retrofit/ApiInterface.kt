package com.example.agencyphase2.retrofit

import com.example.agencyphase2.model.pojo.add_authorize_officer.AddAuthorizeOfficerRequest
import com.example.agencyphase2.model.pojo.add_authorize_officer.AddAuthorizeOfficerResponse
import com.example.agencyphase2.model.pojo.business_information.BusinessInformationRequest
import com.example.agencyphase2.model.pojo.business_information.InsertBusinessInformationResponse
import com.example.agencyphase2.model.pojo.edit_basic_info.EditBasicInfoRequest
import com.example.agencyphase2.model.pojo.edit_basic_info.EditBasicInfoResponse
import com.example.agencyphase2.model.pojo.get_authorize_officer.GetAuthOfficerResponse
import com.example.agencyphase2.model.pojo.get_post_jobs.GetPostJobsResponse
import com.example.agencyphase2.model.pojo.job_post.JobPostRequest
import com.example.agencyphase2.model.pojo.job_post.JobPostResponse
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

    @POST("profile/registration")
    suspend fun addBusinessInfo(
        @Body body: BusinessInformationRequest?,
        @Header("Authorization") token: String): InsertBusinessInformationResponse?

    @POST("profile/edit-registration")
    suspend fun editBasicInfo(
        @Body body: EditBasicInfoRequest?,
        @Header("Authorization") token: String): EditBasicInfoResponse?

    @POST("authorize-officer/create-officer")
    suspend fun addAuthorizeOfficer(
        @Body body: AddAuthorizeOfficerRequest?,
        @Header("Authorization") token: String): AddAuthorizeOfficerResponse?

    @POST("job/create")
    suspend fun jobPost(
        @Body body: JobPostRequest?,
        @Header("Authorization") token: String): JobPostResponse?

    @GET("authorize-officer/get-officer")
    suspend fun getAuthorizeOfficer(
        @Header("Authorization") token: String
    ): GetAuthOfficerResponse?

    @GET("job/get-job")
    suspend fun getPostJobs(
        @Header("Authorization") token: String
    ): GetPostJobsResponse?
}