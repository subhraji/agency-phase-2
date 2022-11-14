package com.example.agencyphase2.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.agencyphase2.MainActivity
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.ActivitySplashBinding
import com.example.agencyphase2.utils.PrefManager
import com.user.caregiver.isConnectedToInternet

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView) ?: return
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        checkInternet()
    }

    private fun checkInternet(){

        Handler(Looper.getMainLooper()).postDelayed({

            if(isConnectedToInternet()){
                if(PrefManager.getLogInStatus() == true){
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    val intent = Intent(this, ChooseLoginRegActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }else{
                val intent = Intent(this, NoInternetActivity::class.java)
                startActivity(intent)
                finish()
            } }, 3000)

    }

}