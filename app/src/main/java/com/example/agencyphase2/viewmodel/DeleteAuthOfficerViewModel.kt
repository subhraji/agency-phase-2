package com.example.agencyphase2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agencyphase2.model.pojo.delete_auth_officer.DeleteAuthOfficerResponse
import com.example.agencyphase2.model.repository.DeleteAuthOfficerRepository
import com.example.agencyphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteAuthOfficerViewModel @Inject constructor(private val repository: DeleteAuthOfficerRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<DeleteAuthOfficerResponse?>?>()
    val response: LiveData<Outcome<DeleteAuthOfficerResponse?>?> = _response

    fun deleteAuthOfficer(
        token: String,
        id: Int
    ) = viewModelScope.launch {
        repository.deleteAuthOfficer(token,id).onStart {
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