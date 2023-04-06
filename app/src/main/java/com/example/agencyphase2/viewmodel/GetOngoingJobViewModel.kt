package com.example.agencyphase2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agencyphase2.model.pojo.get_ongoing_job.GetOngoingJobsResponse
import com.example.agencyphase2.model.repository.GetOngoingJobRepository
import com.example.agencyphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetOngoingJobViewModel @Inject constructor(private val repository: GetOngoingJobRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<GetOngoingJobsResponse?>?>()
    val response: LiveData<Outcome<GetOngoingJobsResponse?>?> = _response

    fun getOnGoingJob(
        token: String,
        id: Int?
    ) = viewModelScope.launch {
        repository.getOngoingJob(token, id).onStart {
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