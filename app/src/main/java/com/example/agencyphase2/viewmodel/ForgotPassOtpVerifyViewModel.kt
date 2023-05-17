package com.example.agencyphase2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agencyphase2.model.pojo.forgot_pass_otv_verify.ForgotPassOtpVerifyResponse
import com.example.agencyphase2.model.repository.ForgotPassOtpVerifyRepository
import com.example.agencyphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPassOtpVerifyViewModel @Inject constructor(private val repository: ForgotPassOtpVerifyRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<ForgotPassOtpVerifyResponse?>?>()
    val response: LiveData<Outcome<ForgotPassOtpVerifyResponse?>?> = _response

    fun verifyOtp(
        email: String,
        otp: String
    ) = viewModelScope.launch {
        repository.verifyOtp(
            email, otp
        ).onStart {
            _response.value = Outcome.loading(true)
        }.catch {
            _response.value = Outcome.Failure(it)
        }.collect {
            _response.value = Outcome.success(it)
        }
    }

    fun navigationComplete(){
        _response.value = null
    }
}