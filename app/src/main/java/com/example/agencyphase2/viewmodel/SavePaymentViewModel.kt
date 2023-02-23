package com.example.agencyphase2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agencyphase2.model.pojo.save_payment.SavePaymentsResponse
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.model.repository.SavePaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavePaymentViewModel @Inject constructor(private val repository: SavePaymentRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<SavePaymentsResponse?>?>()
    val response: LiveData<Outcome<SavePaymentsResponse?>?> = _response

    fun savePayment(
        job_id: Int,
        amount: Double,
        customer_id: String,
        caregiver_charge: Double,
        peaceworc_percentage: Double,
        peaceworc_charge: Double,
        Payment_status: String,
        token: String
    ) = viewModelScope.launch {
        repository.savePayment(job_id, amount, customer_id, caregiver_charge, peaceworc_percentage, peaceworc_charge, Payment_status, token).onStart {
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