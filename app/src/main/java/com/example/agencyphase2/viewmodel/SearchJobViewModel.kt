package com.example.agencyphase2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agencyphase2.model.pojo.search_job.SearchJobResponse
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.model.repository.SearchJobRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchJobViewModel @Inject constructor(private val repository: SearchJobRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<SearchJobResponse?>?>()
    val response: LiveData<Outcome<SearchJobResponse?>?> = _response

    fun searchJob(
        job_status: String?,
        token: String
    ) = viewModelScope.launch {
        repository.searchJob(job_status, token).onStart {
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