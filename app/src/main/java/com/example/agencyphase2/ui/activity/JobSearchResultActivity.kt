package com.example.agencyphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.ActivityJobSearchResultBinding
import com.example.agencyphase2.databinding.ActivitySearchBinding

class JobSearchResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJobSearchResultBinding
    private var job_type: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityJobSearchResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        if (extras != null) {
            job_type = intent?.getStringExtra("job_type")!!
        }

        binding.backBtn.setOnClickListener {
            finish()
        }
    }
}