package com.example.agencyphase2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agencyphase2.model.pojo.add_other_info.AddOtherInfoResponse
import com.example.agencyphase2.model.repository.AddOtherInfoRepository
import com.example.agencyphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddOtherInfoViewModel @Inject constructor(private val repository: AddOtherInfoRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<AddOtherInfoResponse?>?>()
    val response: LiveData<Outcome<AddOtherInfoResponse?>?> = _response

    fun addOtherInfo(
        number_of_employee: String,
        annual_business_revenue: String,
        years_in_business: String,
        legal_structure: String,
        organization_type: String,
        country_of_business: String,
        token: String
    ) = viewModelScope.launch {
        repository.addOtherInfo(
            number_of_employee, annual_business_revenue, years_in_business, legal_structure, organization_type, country_of_business, token
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