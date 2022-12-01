package com.example.agencyphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.ActivitySelectCareTypeBinding
import com.example.agencyphase2.model.pojo.GenderAgeItemCountModel
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.viewmodel.EditBasicInfoViewModel

class SelectCareTypeActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectCareTypeBinding
    private val mEditBasicInfoViewModel: EditBasicInfoViewModel by viewModels()
    private lateinit var loader: androidx.appcompat.app.AlertDialog
    private lateinit var accessToken: String
    private var numberOfPerson: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySelectCareTypeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get token
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()

        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.addBtn.setOnClickListener {
            JobPostActivity.genderAgeList.add(GenderAgeItemCountModel("male","67"))
            finish()
        }

        binding.tv1.setOnClickListener {
            backgroundGrey()
            binding.tv1.background.setTint(ContextCompat.getColor(this, R.color.black))
            binding.tv1.setTextColor(resources.getColor(R.color.white, null))
            numberOfPerson = 1
        }
        binding.tv2.setOnClickListener {
            backgroundGrey()
            binding.tv2.background.setTint(ContextCompat.getColor(this, R.color.black))
            binding.tv2.setTextColor(resources.getColor(R.color.white, null))
            numberOfPerson = 2
        }
        binding.tv3.setOnClickListener {
            backgroundGrey()
            binding.tv3.background.setTint(ContextCompat.getColor(this, R.color.black))
            binding.tv3.setTextColor(resources.getColor(R.color.white, null))
            numberOfPerson = 3
        }
        binding.tv4.setOnClickListener {
            backgroundGrey()
            binding.tv4.background.setTint(ContextCompat.getColor(this, R.color.black))
            binding.tv4.setTextColor(resources.getColor(R.color.white, null))
            numberOfPerson = 4
        }
        binding.tv5.setOnClickListener {
            backgroundGrey()
            binding.tv5.background.setTint(ContextCompat.getColor(this, R.color.black))
            binding.tv5.setTextColor(resources.getColor(R.color.white, null))
            numberOfPerson = 5
        }
    }

    private fun backgroundGrey(){
        binding.tv1.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
        binding.tv1.setTextColor(resources.getColor(R.color.black, null))
        binding.tv2.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
        binding.tv2.setTextColor(resources.getColor(R.color.black, null))
        binding.tv3.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
        binding.tv3.setTextColor(resources.getColor(R.color.black, null))
        binding.tv4.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
        binding.tv4.setTextColor(resources.getColor(R.color.black, null))
        binding.tv5.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
        binding.tv5.setTextColor(resources.getColor(R.color.black, null))
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}