package com.example.agencyphase2.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.doBeforeTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import com.example.agencyphase2.databinding.ActivitySignUpBinding
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.viewmodel.SignUpViewModel
import com.user.caregiver.hideSoftKeyboard
import com.user.caregiver.isConnectedToInternet
import com.user.caregiver.loadingDialog
import com.user.caregiver.showKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private val signUpViewModel: SignUpViewModel by viewModels()
    private lateinit var loader: androidx.appcompat.app.AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //validation
        companyNameFocusListener()
        fullNameFocusListener()
        emailFocusListener()
        passwordFocusListener()
        conPasswordFocusListener()

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.registerBtn.setOnClickListener {

            val validName = binding.companyNameTxtLay.helperText == null && binding.companyNameTxt.text.toString().isNotEmpty()
            val validFullName = binding.fullNameTxtLay.helperText == null && binding.fullNameTxt.text.toString().isNotEmpty()
            val validEmail = binding.emailTxtLay.helperText == null && binding.emailTxt.text.toString().isNotEmpty()
            val validPassword = binding.passwordTxtLay.helperText == null && binding.passwordTxt.text.toString().isNotEmpty()
            val validConPassword = binding.conPasswordTxtLay.helperText == null && binding.conPasswordTxt.text.toString().isNotEmpty()

            if(validName){
                if(validFullName){
                    if(validEmail){
                        if(validPassword){
                            if(validConPassword){
                                if(binding.passwordTxt.text.toString() == binding.conPasswordTxt.text.toString()){
                                    hideSoftKeyboard()
                                    if(isConnectedToInternet()){
                                        signUpViewModel.signup(
                                            binding.companyNameTxt.text.toString(),
                                            binding.fullNameTxt.text.toString(),
                                            binding.emailTxt.text.toString(),
                                            binding.passwordTxt.text.toString(),
                                            binding.conPasswordTxt.text.toString()
                                        )
                                        loader = this.loadingDialog()
                                        loader.show()
                                        Toast.makeText(this,"Clicked",Toast.LENGTH_SHORT).show()
                                    }else{
                                        Toast.makeText(this,"No Internet Connection",Toast.LENGTH_SHORT).show()
                                    }
                                }else{
                                    Toast.makeText(this,"Confirm password mismatch with the new password.", Toast.LENGTH_SHORT).show()
                                    binding.conPasswordTxt.showKeyboard()
                                }
                            }else{
                                if(binding.conPasswordTxtLay.helperText == null) binding.conPasswordTxtLay.helperText = "required"
                                Toast.makeText(this,binding.conPasswordTxtLay.helperText.toString(),Toast.LENGTH_SHORT).show()
                                binding.conPasswordTxt.showKeyboard()
                            }
                        }else{
                            if(binding.passwordTxtLay.helperText == null) binding.passwordTxtLay.helperText = "required"
                            Toast.makeText(this,binding.passwordTxtLay.helperText.toString(),Toast.LENGTH_SHORT).show()
                            binding.passwordTxt.showKeyboard()
                        }
                    }else{
                        if(binding.emailTxtLay.helperText == null) binding.emailTxtLay.helperText = "required"
                        Toast.makeText(this,binding.emailTxtLay.helperText.toString(),Toast.LENGTH_SHORT).show()
                        binding.emailTxt.showKeyboard()
                    }
                }else{
                    if(binding.fullNameTxtLay.helperText == null) binding.fullNameTxtLay.helperText = "required"
                    Toast.makeText(this,binding.fullNameTxtLay.helperText.toString(),Toast.LENGTH_SHORT).show()
                    binding.fullNameTxt.showKeyboard()
                }
            }else{
                if(binding.companyNameTxtLay.helperText == null) binding.companyNameTxtLay.helperText = "required"
                Toast.makeText(this,binding.companyNameTxtLay.helperText.toString(),Toast.LENGTH_SHORT).show()
                binding.companyNameTxt.showKeyboard()
            }

        }

        binding.loginBtn.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }

        signObserve()
    }

    private fun companyNameFocusListener(){
        binding.companyNameTxt.doOnTextChanged { text, start, before, count ->
            binding.companyNameTxtLay.helperText = validCompanyName()
        }
    }

    private fun validCompanyName(): String? {
        val nameText = binding.companyNameTxt.text.toString()

        if(nameText.isEmpty()){
            return "Provide company name."
        }
        return null
    }

    private fun fullNameFocusListener(){
        binding.fullNameTxt.doOnTextChanged { text, start, before, count ->
            binding.fullNameTxtLay.helperText = validFullName()
        }
    }

    private fun validFullName(): String? {
        val nameText = binding.fullNameTxt.text.toString()

        if(nameText.isEmpty()){
            return "Provide full name."
        }
        return null
    }

    private fun emailFocusListener(){
        binding.emailTxt.doOnTextChanged { text, start, before, count ->
            binding.emailTxtLay.helperText = validEmail()
        }
    }

    private fun validEmail(): String? {
        val emailText = binding.emailTxt.text.toString()

        if(!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()){
            return "Invalid Email Address"
        }

        return null
    }

    private fun passwordFocusListener() {
        binding.passwordTxt.doOnTextChanged { text, start, before, count ->
            binding.passwordTxtLay.helperText = validPassword()
        }
    }

    private fun validPassword(): String? {
        val passwordText = binding.passwordTxt.text.toString()
        if(passwordText.length < 6){
            return "Minimum 6 characters required."
        }
        if(!passwordText.matches(".*[A-Z].*".toRegex())){
            return "Must contain 1 upper case character."
        }
        if(!passwordText.matches(".*[a-z].*".toRegex())){
            return "Must contain 1 lower case character."
        }
        if(!passwordText.matches(".*[0-9].*".toRegex())){
            return "Must contain at least 1 number."
        }
        if(!passwordText.matches(".*[!@$#%&*_-].*".toRegex())){
            return "Must contain 1 special character (!@$#%&*_-)."
        }
        return  null
    }

    private fun conPasswordFocusListener() {
        binding.conPasswordTxt.doOnTextChanged { text, start, before, count ->
            binding.conPasswordTxtLay.helperText = validConPassword()
        }
    }
    private fun validConPassword(): String? {
        val new_passwordText = binding.passwordTxt.text.toString()
        val con_passwordText = binding.conPasswordTxt.text.toString()

        if(con_passwordText != new_passwordText){
            return "Password mismatch with the new password."
        }
        return  null
    }

    private fun signObserve(){
        signUpViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        if (outcome.data!!.token != null) {
                            outcome.data!!.token?.let { PrefManager.setKeyAuthToken(it) }
                        }
                        PrefManager.setLogInStatus(true)
                        val intent = Intent(this, AskLocationActivity::class.java)
                        startActivity(intent)
                        finish()
                        signUpViewModel.navigationComplete()
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