package com.example.agencyphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.ActivityBasicInformationBinding
import com.example.agencyphase2.databinding.ActivityRegistrationBinding
import com.user.caregiver.gone
import com.user.caregiver.visible

class RegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.relativeLay2.gone()
        binding.relativeLay3.gone()
        binding.skipBtn.gone()

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.businessNextStepBtn.setOnClickListener {
            binding.relativeLay1.gone()
            binding.relativeLay2.visible()
            binding.skipBtn.visible()
        }

        binding.otherNextStepBtn.setOnClickListener {
            binding.relativeLay2.gone()
            binding.relativeLay3.visible()
            binding.skipBtn.gone()
        }

        binding.authOfficerNextStepBtn.setOnClickListener {
            finish()
        }

        binding.skipBtn.setOnClickListener {
            binding.relativeLay2.gone()
            binding.relativeLay3.visible()
            binding.skipBtn.gone()
        }
    }
}