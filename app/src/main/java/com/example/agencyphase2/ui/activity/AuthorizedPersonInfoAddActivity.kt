package com.example.agencyphase2.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
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
import com.user.caregiver.isConnectedToInternet
import com.user.caregiver.loadingDialog
import com.user.caregiver.showKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthorizedPersonInfoAddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthorizedPersonInfoAddBinding
    private val mAddAuthorizeOfficerViewModel: AddAuthorizeOfficerViewModel by viewModels()
    private val mGetAuthorizeOfficerViewModel: GetAuthorizeOfficerViewModel by viewModels()
    private lateinit var loader: androidx.appcompat.app.AlertDialog
    private lateinit var accessToken: String

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
            val validFirstName = binding.firstNameTxtLay.helperText == null
            val validLastName = binding.lastNameTxtLay.helperText == null
            val validEmail = binding.emailAddressTxtLay.helperText == null
            val validMobile = binding.mobileNumberTxtLay.helperText == null

            if(validFirstName){
                if(validLastName){
                    if(validEmail){
                        if(validMobile){
                            if(isConnectedToInternet()){
                                mAddAuthorizeOfficerViewModel.addAuthorizeInfo(
                                    binding.firstNameTxt.text.toString(),
                                    binding.lastNameTxt.text.toString(),
                                    binding.emailAddressTxt.text.toString(),
                                    binding.mobileNumberTxt.text.toString(),
                                    accessToken
                                )
                                loader = this.loadingDialog()
                                loader.show()
                            }else{
                                Toast.makeText(this,"No internet connection", Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            Toast.makeText(this,binding.mobileNumberTxtLay.helperText.toString(), Toast.LENGTH_SHORT).show()
                            binding.mobileNumberTxt.showKeyboard()
                        }
                    }else{
                        Toast.makeText(this,binding.emailAddressTxtLay.helperText.toString(), Toast.LENGTH_SHORT).show()
                        binding.emailAddressTxt.showKeyboard()
                    }
                }else{
                    Toast.makeText(this,binding.lastNameTxtLay.helperText.toString(), Toast.LENGTH_SHORT).show()
                    binding.lastNameTxt.showKeyboard()
                }
            }else{
                Toast.makeText(this,binding.firstNameTxtLay.helperText.toString(), Toast.LENGTH_SHORT).show()
                binding.firstNameTxt.showKeyboard()
            }

        }

        //validation
        emailFocusListener()
        mobileFocusListener()
        firstNameFocusListener()
        lastNameFocusListener()

        //observer
        addAuthorizeInoObserve()

        //getAuthorizeInoObserve()
        /*if(isConnectedToInternet()){
            mGetAuthorizeOfficerViewModel.getAuthOfficer(accessToken)
        }else{
            Toast.makeText(this,"No internet connection.", Toast.LENGTH_SHORT).show()
        }*/
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
        binding.firstNameTxt.doOnTextChanged { text, start, before, count ->
            binding.firstNameTxtLay.helperText = validFirstName()
        }
    }

    private fun validFirstName(): String? {
        val nameText = binding.firstNameTxt.text.toString()

        if(nameText.isEmpty()){
            return "Please provide first name"
        }
        return null
    }

    private fun lastNameFocusListener(){
        binding.lastNameTxt.doOnTextChanged { text, start, before, count ->
            binding.lastNameTxtLay.helperText = validLastName()
        }
    }

    private fun validLastName(): String? {
        val nameText = binding.lastNameTxt.text.toString()

        if(nameText.isEmpty()){
            return "Please provide last name"
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

    private fun getAuthorizeInoObserve(){
        mGetAuthorizeOfficerViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    if(outcome.data?.success == true){
                        if(outcome.data?.data != null && outcome.data?.data?.size != 0){
                            fillUpcomingRecyclerView(outcome.data?.data!!)
                            binding.addOfficerBtn.text = "Add Officer"
                        }else{
                            binding.addOfficerBtn.text = "Add More Officer"
                        }
                        mGetAuthorizeOfficerViewModel.navigationComplete()
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

    private fun fillUpcomingRecyclerView(list: List<Data>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.authOfficerRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = AuthorizeOfficerAdapter(list,this@AuthorizedPersonInfoAddActivity)
        }
    }

}