package com.example.agencyphase2.retrofit

import com.example.agencyphase2.model.pojo.todo.GetTodosResponse
import retrofit2.http.GET

interface ApiInterface {
    @GET("todos")
    suspend fun getTodos(): GetTodosResponse?
}