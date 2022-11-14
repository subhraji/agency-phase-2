package com.example.agencyphase2.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.ActivityNoInternetBinding
import com.example.agencyphase2.databinding.ActivitySignUpBinding
import com.example.agencyphase2.databinding.ActivitySplashBinding
import com.example.agencyphase2.utils.PrefManager
import com.user.caregiver.isConnectedToInternet

class NoInternetActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoInternetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityNoInternetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkInternet()
    }

    private fun checkInternet(){
        if(isConnectedToInternet()){
            if(PrefManager.getLogInStatus() == true){
                val intent = Intent(this, ChooseLoginRegActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
                finish()
            }
        }else{
            Toast.makeText(this,"No internet connection, please retry.", Toast.LENGTH_SHORT).show()
        }
    }
}