package com.example.agencyphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.ActivityAddClientBinding
import com.example.agencyphase2.databinding.ActivityAskLocationBinding

class AddClientActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddClientBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAddClientBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}