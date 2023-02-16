package com.example.agencyphase2.retrofit

import com.example.agencyphase2.model.pojo.add_authorize_officer.AddAuthorizeOfficerRequest
import com.example.agencyphase2.model.pojo.add_authorize_officer.AddAuthorizeOfficerResponse
import com.example.agencyphase2.model.pojo.add_other_info.AddOtherInfoRequest
import com.example.agencyphase2.model.pojo.add_other_info.AddOtherInfoResponse
import com.example.agencyphase2.model.pojo.business_information.InsertBusinessInformationResponse
import com.example.agencyphase2.model.pojo.change_owner_phone.ChangeOwnerPhoneRequest
import com.example.agencyphase2.model.pojo.change_owner_phone.ChangeOwnerPhoneResponse
import com.example.agencyphase2.model.pojo.change_password.ChangePasswordRequest
import com.example.agencyphase2.model.pojo.change_password.ChangePasswordResponse
import com.example.agencyphase2.model.pojo.delete_auth_officer.DeleteAuthOfficerResponse
import com.example.agencyphase2.model.pojo.delete_job.DeleteJobResponse
import com.example.agencyphase2.model.pojo.edit_basic_info.EditBasicInfoRequest
import com.example.agencyphase2.model.pojo.edit_basic_info.EditBasicInfoResponse
import com.example.agencyphase2.model.pojo.get_authorize_officer.GetAuthOfficerResponse
import com.example.agencyphase2.model.pojo.get_care_type.GetCareTypeResponse
import com.example.agencyphase2.model.pojo.get_customer_id.GetCustomerIdResponse
import com.example.agencyphase2.model.pojo.get_email_verify_otp.GetOtpRequest
import com.example.agencyphase2.model.pojo.get_email_verify_otp.GetOtpResponse
import com.example.agencyphase2.model.pojo.get_ephemeral_key.GetEphemeralKeyResponse
import com.example.agencyphase2.model.pojo.get_ongoing_job.GetOngoingJobsResponse
import com.example.agencyphase2.model.pojo.get_payment_intent.GetPaymentIntentResponse
import com.example.agencyphase2.model.pojo.get_post_jobs.GetPostJobsResponse
import com.example.agencyphase2.model.pojo.get_profile.GetProfileResponse
import com.example.agencyphase2.model.pojo.get_upcomming_jobs.GetUpcommingJobResponse
import com.example.agencyphase2.model.pojo.job_post.JobPostRequest
import com.example.agencyphase2.model.pojo.job_post.JobPostResponse
import com.example.agencyphase2.model.pojo.login.LoginRequest
import com.example.agencyphase2.model.pojo.login.LoginResponse
import com.example.agencyphase2.model.pojo.logout.LogoutResponse
import com.example.agencyphase2.model.pojo.profile_completion_status.GetProfileCompletionStatusResponse
import com.example.agencyphase2.model.pojo.signup.SignUpRequest
import com.example.agencyphase2.model.pojo.signup.SignUpResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiInterface {
    /*@GET("todos")
    suspend fun getTodos(): GetTodosResponse?*/

    @POST("signup")
    suspend fun signup(@Body body: SignUpRequest?): SignUpResponse?

    @POST("login")
    suspend fun login(@Body body: LoginRequest?): LoginResponse?

    @POST("logout")
    suspend fun logout(@Header("Authorization") token: String): LogoutResponse?

    @Multipart
    @POST("business-profile/add-business-info")
    suspend fun addBusinessInfo(
        @Part photo: MultipartBody.Part?,
        @Part("phone") phone: RequestBody,
        @Part("email") email: RequestBody,
        @Part("tax_id_or_ein_id") tax_id_or_ein_id: RequestBody,
        @Part("street") street: RequestBody,
        @Part("city_or_district") city_or_district: RequestBody,
        @Part("state") state: RequestBody,
        @Part("zip_code") zip_code: RequestBody,
        @Header("Authorization") token: String
    ): InsertBusinessInformationResponse?

    @POST("business-profile/add-optional-info")
    suspend fun addOtherInfo(
        @Body body: AddOtherInfoRequest?,
        @Header("Authorization") token: String): AddOtherInfoResponse?

    @POST("business-profile/edit-basic-profile-details")
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
        @Header("Authorization") token: String,
        @Query("id") id: Int? = null,
    ): GetPostJobsResponse?

    @GET("information/status")
    suspend fun getProfileCompletionStatus(
        @Header("Authorization") token: String
    ): GetProfileCompletionStatusResponse?

    @GET("job/delete-job")
    suspend fun deleteJob(
        @Header("Authorization") token: String,
        @Query("id") id: Int?,
    ): DeleteJobResponse?

    @GET("authorize-officer/delete-officer")
    suspend fun deleteAuthOfficer(
        @Header("Authorization") token: String,
        @Query("id") id: Int?,
    ): DeleteAuthOfficerResponse?

    @POST("check-email-exist")
    suspend fun getEmailVerificationOtp(
        @Body body: GetOtpRequest?): GetOtpResponse?

    @POST("owner/change-password")
    suspend fun changePassword(
        @Body body: ChangePasswordRequest?,
        @Header("Authorization") token: String,
    ): ChangePasswordResponse?

    @POST("owner/edit-phone")
    suspend fun changeOwnerPhone(
        @Body body: ChangeOwnerPhoneRequest?,
        @Header("Authorization") token: String,
    ): ChangeOwnerPhoneResponse?

    @GET("business-profile/get-profile-details")
    suspend fun getProfile(
        @Header("Authorization") token: String,
    ): GetProfileResponse?

    @GET("job/care-types/get")
    suspend fun getCareType(
        @Header("Authorization") token: String,
    ): GetCareTypeResponse?

    @GET("job/accepted-job/upcoming")
    suspend fun getUpcomminJobs(
        @Header("Authorization") token: String,
    ): GetUpcommingJobResponse?

    @GET("job/accepted-job/ongoing")
    suspend fun getOngoingJob(
        @Header("Authorization") token: String,
    ): GetOngoingJobsResponse?
}