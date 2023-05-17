package com.example.agencyphase2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agencyphase2.model.pojo.create_client.CreateClientResponse
import com.example.agencyphase2.model.repository.CreateClientRepository
import com.example.agencyphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class CreateClientViewModel @Inject constructor(private val repository: CreateClientRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<CreateClientResponse?>?>()
    val response: LiveData<Outcome<CreateClientResponse?>?> = _response

    fun createClient(
        photo: MultipartBody.Part?,
        email: String,
        name: String,
        phone: String,
        address: String,
        token: String
    ) = viewModelScope.launch {
        repository.createClient(
            photo, email, name, phone, address, token
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