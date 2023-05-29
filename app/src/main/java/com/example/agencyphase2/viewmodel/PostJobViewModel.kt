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
        client_id: Int,
        title: String,
        care_type: String,
        care_items: List<GenderAgeItemCountModel>,
        start_date: String,
        end_date: String,
        start_time: String,
        end_time: String,
        amount: String,
        address: String,
        description: String,
        medical_history: List<String>? = null,
        expertise: List<String>? = null,
        other_requirements: List<String>? = null,
        check_list: List<String>? = null,
        short_address: String,
        lat: String,
        long: String,
        street: String,
        city: String,
        state: String,
        zipcode: String,
        appartment_or_unit: String? = null,
        floor_no: String? = null,
        country: String,
        token: String
    ) = viewModelScope.launch {
        repository.postJob(
            client_id, title, care_type, care_items, start_date, end_date, start_time, end_time, amount, address, description, medical_history, expertise, other_requirements, check_list, short_address, lat, long, street, city, state, zipcode, appartment_or_unit, floor_no, country, token
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