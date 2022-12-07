package com.example.agencyphase2.viewmodel

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agencyphase2.model.pojo.delete_job.DeleteJobResponse
import com.example.agencyphase2.model.repository.DeleteJobRepository
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.ui.activity.JobPostActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteJobViewModel @Inject constructor(private val repository: DeleteJobRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<DeleteJobResponse?>?>()
    val response: LiveData<Outcome<DeleteJobResponse?>?> = _response

    fun deleteJob(
        token: String,
        id: Int
    ) = viewModelScope.launch {
        repository.deleteJob(token,id).onStart {
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