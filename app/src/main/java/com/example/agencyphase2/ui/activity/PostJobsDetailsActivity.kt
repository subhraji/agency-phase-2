package com.example.agencyphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.ActivityPostJobsDetailsBinding
import com.user.caregiver.gone
import com.user.caregiver.visible

class PostJobsDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostJobsDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPostJobsDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        clickJobOverview()

        binding.jobOverviewCard.setOnClickListener {
            clickJobOverview()
        }

        binding.checklistCard.setOnClickListener {
            clickCheckList()
        }

        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    private fun clickJobOverview(){
        binding.jobOverviewTv.setBackgroundResource(R.color.theme_blue)
        binding.jobOverviewTv.setTextColor(ContextCompat.getColor(this, R.color.white))

        binding.checkListTv.setBackgroundResource(R.color.white)
        binding.checkListTv.setTextColor(ContextCompat.getColor(this, R.color.theme_blue))

        binding.relativeLay1.visible()
        binding.relativeLay2.gone()

    }

    private fun clickCheckList(){
        binding.checkListTv.setBackgroundResource(R.color.theme_blue)
        binding.checkListTv.setTextColor(ContextCompat.getColor(this, R.color.white))

        binding.jobOverviewTv.setBackgroundResource(R.color.white)
        binding.jobOverviewTv.setTextColor(ContextCompat.getColor(this, R.color.theme_blue))

        binding.relativeLay2.visible()
        binding.relativeLay1.gone()
    }
}