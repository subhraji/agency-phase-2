package com.example.agencyphase2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agencyphase2.model.pojo.update_payment_status.UpdatePaymentStatusResponse
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.model.repository.UpdatePaymentStatusRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdatePaymentStatusViewModel @Inject constructor(private val repository: UpdatePaymentStatusRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<UpdatePaymentStatusResponse?>?>()
    val response: LiveData<Outcome<UpdatePaymentStatusResponse?>?> = _response

    fun updatePaymentStatus(
        job_id: Int,
        amount: Double,
        customer_id: String,
        caregiver_charge: Double,
        peaceworc_percentage: Int,
        peaceworc_charge: Double,
        payment_status: Int,
        token: String
    ) = viewModelScope.launch {
        repository.updatePaymentStatus(job_id, amount, customer_id, caregiver_charge, peaceworc_percentage, peaceworc_charge, payment_status, token).onStart {
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