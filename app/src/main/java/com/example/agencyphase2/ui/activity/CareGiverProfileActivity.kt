package com.example.agencyphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.ActivityCareGiverProfileBinding
import com.example.agencyphase2.databinding.ActivityJobPostBinding

class CareGiverProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCareGiverProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCareGiverProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}