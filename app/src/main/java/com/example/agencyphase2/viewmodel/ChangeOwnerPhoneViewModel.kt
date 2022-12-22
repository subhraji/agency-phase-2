package com.example.agencyphase2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agencyphase2.model.pojo.change_owner_phone.ChangeOwnerPhoneResponse
import com.example.agencyphase2.model.repository.ChangeOwnerPhoneRepository
import com.example.agencyphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangeOwnerPhoneViewModel @Inject constructor(private val repository: ChangeOwnerPhoneRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<ChangeOwnerPhoneResponse?>?>()
    val response: LiveData<Outcome<ChangeOwnerPhoneResponse?>?> = _response

    fun changeOwnerPhone(
        phone: String,
        token: String,
    ) = viewModelScope.launch {
        repository.changeOwnerPhone(
            phone,token
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