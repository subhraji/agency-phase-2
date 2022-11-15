package com.example.agencyphase2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agencyphase2.model.pojo.edit_basic_info.EditBasicInfoResponse
import com.example.agencyphase2.model.repository.EditBasicInfoRepository
import com.example.agencyphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditBasicInfoViewModel @Inject constructor(private val repository: EditBasicInfoRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<EditBasicInfoResponse?>?>()
    val response: LiveData<Outcome<EditBasicInfoResponse?>?> = _response

    fun editBasicInfo(
        phone: String? = null,
        legal_structure: String? = null,
        organization_type: String? = null,
        tax_id_or_ein_id: String? = null,
        street: String? = null,
        city_or_district: String? = null,
        state: String? = null,
        zip_code: String? = null,
        number_of_employee: Int? = null,
        years_in_business: Int? = null,
        country_of_business: String? = null,
        annual_business_revenue: String? = null,
        token: String
    ) = viewModelScope.launch {
        repository.editBasicInfo(
            phone, legal_structure, organization_type, tax_id_or_ein_id, street, city_or_district, state, zip_code, number_of_employee, years_in_business, country_of_business, annual_business_revenue, token
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