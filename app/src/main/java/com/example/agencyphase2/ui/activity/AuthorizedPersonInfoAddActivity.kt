package com.example.agencyphase2.ui.activity

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agencyphase2.adapter.AuthorizeOfficerAdapter
import com.example.agencyphase2.databinding.ActivityAuthorizedPersonInfoAddBinding
import com.example.agencyphase2.model.pojo.get_authorize_officer.Data
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.viewmodel.AddAuthorizeOfficerViewModel
import com.example.agencyphase2.viewmodel.GetAuthorizeOfficerViewModel
import com.user.caregiver.hideSoftKeyboard
import com.user.caregiver.isConnectedToInternet
import com.user.caregiver.loadingDialog
import com.user.caregiver.showKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthorizedPersonInfoAddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthorizedPersonInfoAddBinding
    val roleList: Array<String> =  arrayOf("Select role*", "Admin", "Operator")

    private val mAddAuthorizeOfficerViewModel: AddAuthorizeOfficerViewModel by viewModels()
    private lateinit var loader: androidx.appcompat.app.AlertDialog
    private lateinit var accessToken: String

    private var role: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAuthorizedPersonInfoAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get token
        accessToken = "Bearer "+ PrefManager.getKeyAuthToken()

        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.nextStepBtn.setOnClickListener {
            val validFirstName = binding.fullNameTxtLay.helperText == null && binding.fullNameTxt.text.toString().isNotEmpty()
            val validEmail = binding.emailAddressTxtLay.helperText == null && binding.emailAddressTxt.text.toString().isNotEmpty()
            val validMobile = binding.mobileNumberTxtLay.helperText == null && binding.mobileNumberTxt.text.toString().isNotEmpty()

            if(validFirstName){
                if(validEmail){
                    if(validMobile){
                        if(role.isNotEmpty()){
                            if(isConnectedToInternet()){
                                mAddAuthorizeOfficerViewModel.addAuthorizeInfo(
                                    binding.fullNameTxt.text.toString(),
                                    binding.emailAddressTxt.text.toString(),
                                    binding.mobileNumberTxt.text.toString(),
                                    role,
                                    accessToken
                                )
                                hideSoftKeyboard()
                                loader = this.loadingDialog()
                                loader.show()
                            }else{
                                Toast.makeText(this,"No internet connection.", Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            Toast.makeText(this,"Please select a role.", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        if(binding.mobileNumberTxtLay.helperText == null) binding.mobileNumberTxtLay.helperText = "required"
                        Toast.makeText(this,binding.mobileNumberTxtLay.helperText.toString(), Toast.LENGTH_SHORT).show()
                        binding.mobileNumberTxt.showKeyboard()
                    }
                }else{
                    if(binding.emailAddressTxtLay.helperText == null) binding.emailAddressTxtLay.helperText = "required"
                    Toast.makeText(this,binding.emailAddressTxtLay.helperText.toString(), Toast.LENGTH_SHORT).show()
                    binding.emailAddressTxt.showKeyboard()
                }
            }else{
                if(binding.fullNameTxtLay.helperText == null) binding.fullNameTxtLay.helperText = "required"
                Toast.makeText(this,binding.fullNameTxtLay.helperText.toString(), Toast.LENGTH_SHORT).show()
                binding.fullNameTxt.showKeyboard()
            }

        }

        //validation
        emailFocusListener()
        mobileFocusListener()
        firstNameFocusListener()

        //observer
        addAuthorizeInoObserve()

        //spinner
        setUpRoleSpinner()
    }

    private fun setUpRoleSpinner(){
        val arrayAdapter = ArrayAdapter(this, R.layout.simple_spinner_dropdown_item,roleList)
        binding.roleSpinner.adapter = arrayAdapter
        binding.roleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            AdapterView.OnItemClickListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(p2 == 0){
                    role = ""
                }else if(p2 == 1){
                    role = "4"
                }else if(p2 == 2){
                    role = "5"
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }
        }
    }


    private fun emailFocusListener(){
        binding.emailAddressTxt.doOnTextChanged { text, start, before, count ->
            binding.emailAddressTxtLay.helperText = validEmail()
        }
    }

    private fun validEmail(): String? {
        val emailText = binding.emailAddressTxt.text.toString()

        if(!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()){
            return "Invalid Email Address"
        }

        return null
    }

    private fun mobileFocusListener(){
        binding.mobileNumberTxt.doOnTextChanged { text, start, before, count ->
            binding.mobileNumberTxtLay.helperText = validMobile()
        }
    }

    private fun validMobile(): String? {
        val mobileText = binding.mobileNumberTxt.text.toString()

        if(mobileText.isEmpty()){
            return "Please provide mobile number"
        }
        if(mobileText.length != 10){
            return "Mobile number must be a 10 digit number"
        }
        if(mobileText.toDouble() == 0.00){
            return "Invalid mobile number"
        }
        return null
    }

    private fun firstNameFocusListener(){
        binding.fullNameTxt.doOnTextChanged { text, start, before, count ->
            binding.fullNameTxtLay.helperText = validFirstName()
        }
    }

    private fun validFirstName(): String? {
        val nameText = binding.fullNameTxt.text.toString()

        if(nameText.isEmpty()){
            return "Please provide first name"
        }
        return null
    }

    private fun addAuthorizeInoObserve(){
        mAddAuthorizeOfficerViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        //mGetAuthorizeOfficerViewModel.getAuthOfficer(accessToken)
                        mAddAuthorizeOfficerViewModel.navigationComplete()
                        finish()
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