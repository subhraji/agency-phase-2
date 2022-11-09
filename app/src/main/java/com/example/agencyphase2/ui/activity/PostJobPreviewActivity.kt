package com.example.agencyphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.ActivityJobPostBinding
import com.example.agencyphase2.databinding.ActivityPostJobPreviewBinding

class PostJobPreviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostJobPreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPostJobPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.nextStepBtn.setOnClickListener {
            Toast.makeText(this,"Job posted successfully.",Toast.LENGTH_LONG).show()
            finish()
        }
    }
}