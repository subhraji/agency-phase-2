package com.example.agencyphase2

import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.agencyphase2.databinding.ActivityMainBinding
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.ui.activity.JobPostActivity
import com.example.agencyphase2.ui.activity.RegistrationActivity
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.utils.SocketHelper
import com.example.agencyphase2.viewmodel.GetProfileCompletionStatusViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.user.caregiver.isConnectedToInternet
import com.user.caregiver.loadingDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var CHANNEL_ID = "101"
    private val mGetProfileCompletionStatusViewModel: GetProfileCompletionStatusViewModel by viewModels()
    private lateinit var accessToken: String
    private lateinit var loader: androidx.appcompat.app.AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get token
        accessToken = "Bearer "+ PrefManager.getKeyAuthToken()

        //socket connect
        /*CoroutineScope(Dispatchers.IO).launch {
            SocketHelper.initSocket()
        }*/

        binding.bottomNavigation.itemIconTintList=null
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        createNotificationChannel()
        getToken()
        subscribeToTopic()

        //observe
        getProfileCompletionStatusObserver()

        binding.jobPostBtn.setOnClickListener {
            if(isConnectedToInternet()){
                mGetProfileCompletionStatusViewModel.getProfileCompletionStatus(accessToken)
                loader = loadingDialog()
                loader.show()
            }else{
                Toast.makeText(this,"No internet connection.", Toast.LENGTH_SHORT).show()
            }
        }
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
            val token = task.result

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

    private fun getProfileCompletionStatusObserver(){
        mGetProfileCompletionStatusViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        if(outcome.data?.data?.is_business_info_complete == 0){
                            showCompleteDialog("You have not added your business information",1)
                        }else if(outcome.data?.data?.is_authorize_info_added == 0){
                            showCompleteDialog("You have not added any authorize officer",3)
                        }else if(outcome.data?.data?.is_profile_approved == 0){
                            showCompleteDialog("Your profile is under review. It will take 24 to 48 hours to get the approval.",4)

                            /*else if(outcome.data?.data?.is_other_info_added == 0){
                                showCompleteDialog("You have not added other optional information",2)
                            }*/
                        }else{
                            val intent = Intent(this, JobPostActivity::class.java)
                            startActivity(intent)
                        }
                        mGetProfileCompletionStatusViewModel.navigationComplete()
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

    private fun showCompleteDialog(content: String, step: Int) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.profile_completion_dialog_layout)
        val complete = dialog.findViewById<TextView>(R.id.complete_btn)
        val content_tv = dialog.findViewById<TextView>(R.id.content_tv)

        content_tv.text = content

        if(step == 4){
            complete.text = "Ok"
        }
        complete.setOnClickListener {
            if(step == 4){
                dialog.dismiss()
                val intent = Intent(this, JobPostActivity::class.java)
                startActivity(intent)
            }else{
                dialog.dismiss()
                val intent = Intent(this, RegistrationActivity::class.java)
                intent.putExtra("step",step)
                startActivity(intent)
            }
        }

        dialog.getWindow()?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.show()
    }


    override fun onDestroy() {
        super.onDestroy()
        //SocketHelper.mSocket!!.disconnect()
    }
}