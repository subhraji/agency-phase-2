package com.example.agencyphase2.di

import com.example.agencyphase2.BuildConfig
import com.example.agencyphase2.di.qualifier.AppApiService
import com.example.agencyphase2.di.qualifier.BaseUrl
import com.example.agencyphase2.di.qualifier.PaymentApiService
import com.example.agencyphase2.di.qualifier.PaymentBaseUrl
import com.example.agencyphase2.retrofit.ApiInterface
import com.example.agencyphase2.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    private val mLoggingInterceptor = HttpLoggingInterceptor()

    private fun getHttpLogClientWithToken(): OkHttpClient {
        if (BuildConfig.DEBUG) {
            mLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(mLoggingInterceptor)
            .connectTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build()
    }


    @Singleton
    @Provides
    @BaseUrl
    fun providesApiRetrofit() : Retrofit {

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        return Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getHttpLogClientWithToken())
            .build()
    }

    @Singleton
    @Provides
    @PaymentBaseUrl
    fun providesPaymentRetrofit() : Retrofit {

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        return Retrofit.Builder().baseUrl(Constants.PAYMENT_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getHttpLogClientWithToken())
            .build()
    }

    @Singleton
    @Provides
    fun providesAppAPI(@BaseUrl retrofit: Retrofit) : ApiInterface {
        return retrofit.create(ApiInterface::class.java)
    }

    @Singleton
    @Provides
    @PaymentApiService
    fun providesPaymentAPI(@PaymentBaseUrl retrofit: Retrofit) : ApiInterface {
        return retrofit.create(ApiInterface::class.java)
    }
}