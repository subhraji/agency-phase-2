package com.example.agencyphase2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agencyphase2.model.pojo.get_clients.GetClientsResponse
import com.example.agencyphase2.model.repository.GetClientsRepository
import com.example.agencyphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class GetClientsViewModel @Inject constructor(private val repository: GetClientsRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<GetClientsResponse?>?>()
    val response: LiveData<Outcome<GetClientsResponse?>?> = _response

    fun getClients(
        token: String
    ) = viewModelScope.launch {
        repository.getClients(token).onStart {
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