package com.example.agencyphase2.retrofit

import com.example.agencyphase2.model.pojo.signup.SignUpRequest
import com.example.agencyphase2.model.pojo.signup.SignUpResponse
import com.example.agencyphase2.model.pojo.todo.GetTodosResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiInterface {
    @GET("todos")
    suspend fun getTodos(): GetTodosResponse?

    @POST("signup")
    suspend fun signup(@Body body: SignUpRequest?): SignUpResponse?
}