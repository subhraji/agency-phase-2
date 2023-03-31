package com.example.agencyphase2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agencyphase2.model.pojo.get_email_verify_otp.GetOtpResponse
import com.example.agencyphase2.model.repository.GetEmailVerificationOtpRepository
import com.example.agencyphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetEmailVerificationOtpViewModel @Inject constructor(private val repository: GetEmailVerificationOtpRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<GetOtpResponse?>?>()
    val response: LiveData<Outcome<GetOtpResponse?>?> = _response

    fun verifyOtp(
        email: String,
        otp: String,
        company_name: String
    ) = viewModelScope.launch {
        repository.getOtp(email, otp, company_name).onStart {
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