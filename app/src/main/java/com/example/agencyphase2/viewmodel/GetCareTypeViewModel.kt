package com.example.agencyphase2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agencyphase2.model.pojo.get_care_type.GetCareTypeResponse
import com.example.agencyphase2.model.repository.DeleteJobRepository
import com.example.agencyphase2.model.repository.GetCareTypeRepository
import com.example.agencyphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetCareTypeViewModel @Inject constructor(private val repository: GetCareTypeRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<GetCareTypeResponse?>?>()
    val response: LiveData<Outcome<GetCareTypeResponse?>?> = _response

    fun getCareType(
        token: String
    ) = viewModelScope.launch {
        repository.getCareType(token).onStart {
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