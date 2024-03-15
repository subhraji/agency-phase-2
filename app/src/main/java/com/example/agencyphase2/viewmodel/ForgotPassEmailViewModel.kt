package com.example.agencyphase2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agencyphase2.model.pojo.forgotpass_send_email.ForgotPassSendEmailResponse
import com.example.agencyphase2.model.repository.ForgotPasswordEmailRepository
import com.example.agencyphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPassEmailViewModel @Inject constructor(private val repository: ForgotPasswordEmailRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<ForgotPassSendEmailResponse?>?>()
    val response: LiveData<Outcome<ForgotPassSendEmailResponse?>?> = _response

    fun sendEmail(
        email: String,
    ) = viewModelScope.launch {
        repository.sendEmail(email).onStart {
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