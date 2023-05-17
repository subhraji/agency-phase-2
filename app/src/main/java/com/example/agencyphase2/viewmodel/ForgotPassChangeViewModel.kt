package com.example.agencyphase2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agencyphase2.model.pojo.forgot_pass_change.ForgotPassChangeResponse
import com.example.agencyphase2.model.repository.ForgotPassChangeRepository
import com.example.agencyphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class ForgotPassChangeViewModel @Inject constructor(private val repository: ForgotPassChangeRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<ForgotPassChangeResponse?>?>()
    val response: LiveData<Outcome<ForgotPassChangeResponse?>?> = _response

    fun changePass(
        email: String,
        password: String,
        confirm_password: String,
        fcm_token: String
    ) = viewModelScope.launch {
        repository.forgotPassChange(
            email, password, confirm_password, fcm_token
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