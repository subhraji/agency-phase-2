package com.example.agencyphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.ActivityJobPostBinding
import com.example.agencyphase2.databinding.ActivitySignUpBinding

class JobPostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJobPostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityJobPostBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}