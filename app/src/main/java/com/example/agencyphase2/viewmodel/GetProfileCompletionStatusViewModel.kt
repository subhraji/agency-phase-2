package com.example.agencyphase2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agencyphase2.model.pojo.profile_completion_status.GetProfileCompletionStatusResponse
import com.example.agencyphase2.model.repository.GetProfileCompletionStatusRepository
import com.example.agencyphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetProfileCompletionStatusViewModel @Inject constructor(private val repository: GetProfileCompletionStatusRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<GetProfileCompletionStatusResponse?>?>()
    val response: LiveData<Outcome<GetProfileCompletionStatusResponse?>?> = _response

    fun getProfileCompletionStatus(
        token: String
    ) = viewModelScope.launch {
        repository.getProfileCompletionStatus(token).onStart {
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