package com.example.agencyphase2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agencyphase2.model.pojo.caregiver_profile.GetCaregiverProfileResponse
import com.example.agencyphase2.model.repository.GetCaregiverProfileRepository
import com.example.agencyphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetCaregiverProfileViewModel @Inject constructor(private val repository: GetCaregiverProfileRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<GetCaregiverProfileResponse?>?>()
    val response: LiveData<Outcome<GetCaregiverProfileResponse?>?> = _response

    fun getCaregiverProfile(
        token: String,
        job_id: String?
    ) = viewModelScope.launch {
        repository.getCaregiverProfile(token, job_id).onStart {
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