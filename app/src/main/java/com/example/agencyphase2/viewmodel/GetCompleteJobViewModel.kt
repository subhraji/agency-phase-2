package com.example.agencyphase2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agencyphase2.model.pojo.get_complete_jobs.GetCompleteJobsResponse
import com.example.agencyphase2.model.repository.GetCompletedJobsRepository
import com.example.agencyphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetCompleteJobViewModel @Inject constructor(private val repository: GetCompletedJobsRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<GetCompleteJobsResponse?>?>()
    val response: LiveData<Outcome<GetCompleteJobsResponse?>?> = _response

    fun getCompleteJob(
        token: String
    ) = viewModelScope.launch {
        repository.getCompleteJob(token).onStart {
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