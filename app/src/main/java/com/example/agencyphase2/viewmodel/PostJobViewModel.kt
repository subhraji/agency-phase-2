package com.example.agencyphase2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agencyphase2.model.pojo.GenderAgeItemCountModel
import com.example.agencyphase2.model.pojo.job_post.JobPostResponse
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.model.repository.PostJobRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostJobViewModel @Inject constructor(private val repository: PostJobRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<JobPostResponse?>?>()
    val response: LiveData<Outcome<JobPostResponse?>?> = _response

    fun jobPost(
        title: String,
        care_type: String,
        care_items: List<GenderAgeItemCountModel>,
        date: String,
        start_time: String,
        end_time: String,
        amount: String,
        address: String,
        description: String,
        token: String
    ) = viewModelScope.launch {
        repository.postJob(
            title, care_type, care_items, date, start_time, end_time, amount, address, description, token
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