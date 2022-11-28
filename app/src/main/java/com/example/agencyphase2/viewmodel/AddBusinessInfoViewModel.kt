package com.example.agencyphase2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agencyphase2.model.pojo.business_information.InsertBusinessInformationResponse
import com.example.agencyphase2.model.repository.AddBusinessInfoRepository
import com.example.agencyphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class AddBusinessInfoViewModel  @Inject constructor(private val repository: AddBusinessInfoRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<InsertBusinessInformationResponse?>?>()
    val response: LiveData<Outcome<InsertBusinessInformationResponse?>?> = _response

    fun addBusinessInfo(
        photo: MultipartBody.Part?,
        phone: String,
        email: String,
        tax_id_or_ein_id: String,
        street: String,
        city_or_district: String,
        state: String,
        zip_code: String,
        token: String
    ) = viewModelScope.launch {
        repository.addBusinessInfo(
            photo, phone, email, tax_id_or_ein_id, street, city_or_district, state, zip_code, token
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