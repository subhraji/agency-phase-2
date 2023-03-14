package com.example.agencyphase2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agencyphase2.model.pojo.update_auth_officer_status.UpdateAuthOfficerStatusResponse
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.model.repository.UpdateAuthOfficerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateAuthOfficerStatusViewModel @Inject constructor(private val repository: UpdateAuthOfficerRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<UpdateAuthOfficerStatusResponse?>?>()
    val response: LiveData<Outcome<UpdateAuthOfficerStatusResponse?>?> = _response

    fun updateAuthOfficerStatus(
        token: String
    ) = viewModelScope.launch {
        repository.updateAuthOfficerStatus(token).onStart {
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