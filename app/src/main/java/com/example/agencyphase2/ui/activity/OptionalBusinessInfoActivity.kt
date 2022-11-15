package com.example.agencyphase2.ui.activity

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import com.example.agencyphase2.databinding.ActivityOptionalBusinessInfoBinding
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.viewmodel.AddBusinessInfoViewModel
import com.example.agencyphase2.viewmodel.EditBasicInfoViewModel
import com.user.caregiver.gone
import com.user.caregiver.isConnectedToInternet
import com.user.caregiver.loadingDialog
import com.user.caregiver.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OptionalBusinessInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOptionalBusinessInfoBinding
    private val mEditBasicInfoViewModel: EditBasicInfoViewModel by viewModels()
    private lateinit var loader: androidx.appcompat.app.AlertDialog
    private lateinit var accessToken: String
    private var country: String? = null
    private var number_of_employee: Int? = null
    private var years_in_business: Int? = null
    private var revenue: String? = null

    val countryList: Array<String> =  arrayOf("Country of citizenship","Usa","India","England","Canada","Russia")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityOptionalBusinessInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get token
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()

        binding.backArrow.setOnClickListener {
            finish()
        }

        //spinner
        setUpCountrySpinner()

        //observer
        editBasicInfoObserve()

        binding.nextStepBtn.setOnClickListener {

            if(binding.numberOfEmployeesTxt.text.toString().isEmpty()){
                number_of_employee = null
            }else{
                number_of_employee = binding.numberOfEmployeesTxt.text.toString().toInt()
            }

            if(binding.yearsInBusinessTxt.text.toString().isEmpty()){
                years_in_business = null
            }else{
                years_in_business = binding.yearsInBusinessTxt.text.toString().toInt()
            }

            if(binding.businessRevenueTxt.text.toString().isEmpty()){
                revenue = null
            }else{
                revenue =binding.businessRevenueTxt.text.toString()
            }


            if(isConnectedToInternet()){
                mEditBasicInfoViewModel.editBasicInfo(
                    number_of_employee = number_of_employee,
                    years_in_business = years_in_business,
                    country_of_business = country,
                    annual_business_revenue = revenue,
                    token = accessToken
                )
                loader = this.loadingDialog()
                loader.show()
            }else{

            }
        }
    }


    private fun setUpCountrySpinner(){
        val arrayAdapter = ArrayAdapter(this, R.layout.simple_spinner_dropdown_item,countryList)
        binding.countrySpinner.adapter = arrayAdapter
        binding.countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            AdapterView.OnItemClickListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(p2 == 0){
                    country = null
                }else{
                    country = countryList[p2]
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }
        }
    }

    private fun editBasicInfoObserve(){
        mEditBasicInfoViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        val intent = Intent(this, AuthorizedPersonInfoAddActivity::class.java)
                        startActivity(intent)
                        mEditBasicInfoViewModel.navigationComplete()
                    }else{
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Outcome.Failure<*> -> {
                    Toast.makeText(this,outcome.e.message, Toast.LENGTH_SHORT).show()

                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })
    }

}