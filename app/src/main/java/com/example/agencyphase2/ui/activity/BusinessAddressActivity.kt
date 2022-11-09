package com.example.agencyphase2.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.ActivityBasicInformationBinding
import com.example.agencyphase2.databinding.ActivityHomeAddressBinding

class BusinessAddressActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeAddressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityHomeAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.nextStepBtn.setOnClickListener {
            val intent = Intent(this, AuthorizedPersonInfoAddActivity::class.java)
            startActivity(intent)
        }
    }
}