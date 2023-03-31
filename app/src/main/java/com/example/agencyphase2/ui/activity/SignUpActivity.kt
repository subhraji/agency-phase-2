package com.example.agencyphase2.ui.activity

import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.util.Patterns
import android.view.ViewGroup
import android.view.Window
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.NotificationCompat
import androidx.core.widget.doBeforeTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.ActivitySignUpBinding
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.viewmodel.GetEmailVerificationOtpViewModel
import com.example.agencyphase2.viewmodel.SignUpViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.user.caregiver.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private val signUpViewModel: SignUpViewModel by viewModels()
    private lateinit var loader: androidx.appcompat.app.AlertDialog
    var cTimer: CountDownTimer? = null
    private var isAgree = false

    private lateinit var token: String
    private var CHANNEL_ID = "101"


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

        binding.privacyTv1.gone()
        binding.checkBox1.gone()

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

                                    if(isAgree){
                                        if(isConnectedToInternet()){
                                            signUpViewModel.signup(
                                                binding.companyNameTxt.text.toString(),
                                                binding.fullNameTxt.text.toString(),
                                                binding.emailTxt.text.toString(),
                                                binding.passwordTxt.text.toString(),
                                                binding.conPasswordTxt.text.toString(),
                                                token
                                            )
                                            loader = this.loadingDialog()
                                            loader.show()
                                        }else{
                                            Toast.makeText(this,"No Internet Connection",Toast.LENGTH_SHORT).show()
                                        }
                                    }else{
                                        showPrivacyPolicyDialog()
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

        //observer
        getOtpObserver()

        createNotificationChannel()
        getToken()
        subscribeToTopic()
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

    private fun getOtpObserver(){
        signUpViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        Toast.makeText(this,outcome.data!!.message.toString(), Toast.LENGTH_LONG).show()
                        val intent = Intent(this, EmailVerificationActivity::class.java)
                        intent.putExtra("email",binding.emailTxt.text.toString())
                        intent.putExtra("name",binding.fullNameTxt.text.toString())
                        intent.putExtra("company_name",binding.companyNameTxt.text.toString())
                        startActivity(intent)
                        signUpViewModel.navigationComplete()
                    }else{
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Outcome.Failure<*> -> {
                    Toast.makeText(this,outcome.e.message, Toast.LENGTH_SHORT).show()
                    loader.dismiss()
                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })
    }


    private fun showPrivacyPolicyDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.privacy_policy_dialog)

        val webview = dialog.findViewById<WebView>(R.id.privacy_webview)
        val agree_btn = dialog.findViewById<TextView>(R.id.check_agree)
        val progressBar = dialog.findViewById<ProgressBar>(R.id.progress_bar)

        agree_btn.setOnClickListener {
            isAgree = true
            binding.privacyTv1.visible()
            binding.checkBox1.visible()
            dialog.dismiss()
        }

        webview.loadUrl("https://www.peaceworc.ekodusproject.tech")
        webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)

                progressBar.visible()
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }
            override fun onPageCommitVisible(view: WebView?, url: String?) {
                super.onPageCommitVisible(view, url)
                progressBar.gone()
            }
        }

        dialog.getWindow()?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.show()
    }


    //notification subscribe
    private fun subscribeToTopic(){
        FirebaseMessaging.getInstance().subscribeToTopic("cloud")
            .addOnCompleteListener { task ->
                var msg = "Done"
                if (!task.isSuccessful) {
                    msg = "Failed"
                }
            }
    }

    //get token
    private fun getToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("Token", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            token = task.result

            // Log and toast
            //val msg = getString(R.string.msg_token_fmt, token)
            Log.e("Token", token)
            //Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
        })
    }

    private fun createNotificationChannel() {

        NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_circle_notifications_24)
            .setContentTitle("textTitle")
            .setContentText("textContent")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "firebaseNotifChannel"
            val descriptionText = "this is a channel to receive firebase notification."
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }
    }
}