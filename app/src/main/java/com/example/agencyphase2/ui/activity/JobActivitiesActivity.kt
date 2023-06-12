package com.example.agencyphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.ActivityIncompleteJobDetailsBinding
import com.example.agencyphase2.databinding.ActivityJobActivitiesBinding

class JobActivitiesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJobActivitiesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityJobActivitiesBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}