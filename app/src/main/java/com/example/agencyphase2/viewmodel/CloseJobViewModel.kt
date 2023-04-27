package com.example.agencyphase2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agencyphase2.model.pojo.close_job.CloseJobResponse
import com.example.agencyphase2.model.repository.CloseJobRepository
import com.example.agencyphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CloseJobViewModel @Inject constructor(private val repository: CloseJobRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<CloseJobResponse?>?>()
    val response: LiveData<Outcome<CloseJobResponse?>?> = _response

    fun closeJob(
        job_id: String,
        token: String,
    ) = viewModelScope.launch {
        repository.closeJob(
            job_id,token
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