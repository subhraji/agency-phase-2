package com.example.agencyphase2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agencyphase2.model.pojo.get_post_jobs.GetPostJobsResponse
import com.example.agencyphase2.model.repository.GetPostJobsRepository
import com.example.agencyphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetPostJobsViewModel @Inject constructor(private val repository: GetPostJobsRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<GetPostJobsResponse?>?>()
    val response: LiveData<Outcome<GetPostJobsResponse?>?> = _response

    fun getPostJobs(
        token: String,
        id: Int? = null
    ) = viewModelScope.launch {
        repository.getPostJobs(token, id).onStart {
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