package com.example.agencyphase2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agencyphase2.model.pojo.get_upcomming_jobs.GetUpcommingJobResponse
import com.example.agencyphase2.model.repository.GetUpcommingJobRepository
import com.example.agencyphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetUpcommingJobViewModel @Inject constructor(private val repository: GetUpcommingJobRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<GetUpcommingJobResponse?>?>()
    val response: LiveData<Outcome<GetUpcommingJobResponse?>?> = _response

    fun getUpcommingJob(
        token: String
    ) = viewModelScope.launch {
        repository.getUpcomminJobs(token).onStart {
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