package com.example.agencyphase2.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.ActivityAuthorizedPersonInfoAddBinding
import com.example.agencyphase2.databinding.ActivityHomeAddressBinding

class AuthorizedPersonInfoAddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthorizedPersonInfoAddBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAuthorizedPersonInfoAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.nextStepBtn.setOnClickListener {
            val intent = Intent(this, JobPostActivity::class.java)
            startActivity(intent)
        }
    }
}