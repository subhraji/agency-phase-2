package com.example.agencyphase2.ui.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer
import com.example.agencyphase2.MainActivity
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.ActivityChooseLoginRegBinding
import com.example.agencyphase2.databinding.ActivityEmailVerificationBinding
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.viewmodel.GetEmailVerificationOtpViewModel
import com.example.agencyphase2.viewmodel.ResendOtpViewModel
import com.example.agencyphase2.viewmodel.SignUpViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.user.caregiver.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmailVerificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmailVerificationBinding
    private val mGetEmailVerificationOtpViewModel: GetEmailVerificationOtpViewModel by viewModels()
    private val mResendOtpViewModel: ResendOtpViewModel by viewModels()

    private lateinit var loader: androidx.appcompat.app.AlertDialog
    var cTimer: CountDownTimer? = null

    private lateinit var token: String
    private var CHANNEL_ID = "101"
    private var email = ""
    private var name = ""
    private var company_name = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityEmailVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        if (extras != null) {
            email = extras.getString("email").toString()
            name = extras.getString("name").toString()
            company_name = extras.getString("company_name").toString()
        }

        loader = this.loadingDialog()

        startTimer()
        binding.resendTv.gone()

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.edTxt1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(binding.edTxt1.text.length == 1){
                    binding.edTxt2.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.edTxt2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(binding.edTxt2.text.length == 1){
                    binding.edTxt3.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.edTxt3.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(binding.edTxt3.text.length == 1){
                    binding.edTxt4.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.edTxt4.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(binding.edTxt4.text.length == 1){
                    binding.edTxt5.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.edTxt5.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(binding.edTxt5.text.length == 1){
                    binding.edTxt6.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.verifyBtn.setOnClickListener {
            val otp = "${binding.edTxt1.text}${binding.edTxt2.text}${binding.edTxt3.text}${binding.edTxt4.text}${binding.edTxt5.text}${binding.edTxt6.text}"
            hideSoftKeyboard()
            if(otp.length == 6){
                if(isConnectedToInternet()){
                    mGetEmailVerificationOtpViewModel.verifyOtp(email, otp, company_name)
                    loader.show()

                }else{
                    Toast.makeText(this,"No internet connection.", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"Invalid OTP.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.resendTv.setOnClickListener {
            hideSoftKeyboard()
            if(isConnectedToInternet()){
                mResendOtpViewModel.resendOtp(email)
                loader = this.loadingDialog()
                loader.show()
            }else{
                Toast.makeText(this,"No internet connection.", Toast.LENGTH_SHORT).show()
            }
        }

        createNotificationChannel()
        getToken()
        subscribeToTopic()

        //observer
        getOtpObserver()
        resendOtpObserver()
    }

    fun startTimer() {
        cTimer = object : CountDownTimer(180000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.timerTv.setText("OTP well be expired in: " + millisUntilFinished / 1000 +" sec");
            }
            override fun onFinish() {
                cancelTimer()
                binding.resendTv.visible()
            }
        }
        (cTimer as CountDownTimer).start()
    }

    fun cancelTimer() {
        if (cTimer != null) cTimer!!.cancel()
    }

    private fun getOtpObserver(){
        mGetEmailVerificationOtpViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        Toast.makeText(this,outcome.data!!.message.toString(), Toast.LENGTH_LONG).show()
                        startTimer()
                        binding.resendTv.gone()

                        binding.edTxt1.text = null
                        binding.edTxt2.text = null
                        binding.edTxt3.text = null
                        binding.edTxt4.text = null
                        binding.edTxt5.text = null
                        binding.edTxt6.text = null
                        binding.edTxt1.showKeyboard()

                        outcome.data?.token?.let {
                            PrefManager.setKeyAuthToken(it)
                            PrefManager.setUserFullName(name)
                            PrefManager.setLogInStatus(true)
                            val intent = Intent(this, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finishAffinity()
                        }
                        mGetEmailVerificationOtpViewModel.navigationComplete()
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

    private fun resendOtpObserver(){
        mResendOtpViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        Toast.makeText(this,outcome.data!!.message.toString(), Toast.LENGTH_LONG).show()
                        startTimer()
                        binding.resendTv.gone()

                        binding.edTxt1.text = null
                        binding.edTxt2.text = null
                        binding.edTxt3.text = null
                        binding.edTxt4.text = null
                        binding.edTxt5.text = null
                        binding.edTxt6.text = null
                        binding.edTxt1.showKeyboard()

                        mResendOtpViewModel.navigationComplete()
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

    override fun onDestroy() {
        cancelTimer()
        super.onDestroy()
    }
}