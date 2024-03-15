package com.example.agencyphase2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agencyphase2.model.pojo.get_notifications.GetNotificationsResponse
import com.example.agencyphase2.model.repository.GetNotificationsRepository
import com.example.agencyphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetNotificationsViewModel @Inject constructor(private val repository: GetNotificationsRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<GetNotificationsResponse?>?>()
    val response: MutableLiveData<Outcome<GetNotificationsResponse?>?> = _response
    fun getNotifications(
        token: String,
    ) = viewModelScope.launch {
        repository.getNotifications(
            token
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