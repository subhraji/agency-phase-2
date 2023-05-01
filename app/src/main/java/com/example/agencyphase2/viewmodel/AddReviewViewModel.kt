package com.example.agencyphase2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agencyphase2.model.pojo.add_review.AddReviewResponse
import com.example.agencyphase2.model.repository.AddReviewRepository
import com.example.agencyphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddReviewViewModel @Inject constructor(private val repository: AddReviewRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<AddReviewResponse?>?>()
    val response: LiveData<Outcome<AddReviewResponse?>?> = _response

    fun addReview(
        caregiver_id: String,
        rating: String,
        review: String,
        token: String
    ) = viewModelScope.launch {
        repository.addReview(
            caregiver_id, rating, review, token
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