package com.example.agencyphase2.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.ActivityBasicInformationBinding
import com.example.agencyphase2.databinding.ActivityPostJobPreviewBinding

class BasicInformationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBasicInformationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityBasicInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.nextStepBtn.setOnClickListener {
            val intent = Intent(this, BusinessAddressActivity::class.java)
            startActivity(intent)
        }

        binding.backArrow.setOnClickListener {
            finish()
        }
    }
}