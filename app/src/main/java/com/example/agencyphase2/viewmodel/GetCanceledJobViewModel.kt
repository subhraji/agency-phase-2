package com.example.agencyphase2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agencyphase2.model.pojo.get_canceled_job.GetCanceledJobResponse
import com.example.agencyphase2.model.repository.GetCanceledJobRepository
import com.example.agencyphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetCanceledJobViewModel @Inject constructor(private val repository: GetCanceledJobRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<GetCanceledJobResponse?>?>()
    val response: LiveData<Outcome<GetCanceledJobResponse?>?> = _response

    fun getCanceledJob(
        token: String,
        page_no: Int
    ) = viewModelScope.launch {
        repository.getCanceledJob(token, page_no).onStart {
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