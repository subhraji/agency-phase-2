package com.example.agencyphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.ActivityClientListBinding
import com.example.agencyphase2.databinding.ActivityCreateClientBinding

class CreateClientActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateClientBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCreateClientBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}