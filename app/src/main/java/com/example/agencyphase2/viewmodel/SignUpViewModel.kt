package com.example.agencyphase2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agencyphase2.model.pojo.signup.SignUpResponse
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.model.repository.SignUpRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(private val repository: SignUpRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<SignUpResponse?>?>()
    val response: LiveData<Outcome<SignUpResponse?>?> = _response

    fun signup(
        company_name: String,
        name: String,
        email: String,
        password: String,
        con_password: String,
        token: String
    ) = viewModelScope.launch {
        repository.signup(company_name, name, email, password, con_password, token).onStart {
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