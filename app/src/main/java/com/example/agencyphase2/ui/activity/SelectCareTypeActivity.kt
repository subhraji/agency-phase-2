package com.example.agencyphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.agencyphase2.databinding.ActivitySelectCareTypeBinding
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.viewmodel.EditBasicInfoViewModel

class SelectCareTypeActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectCareTypeBinding
    private val mEditBasicInfoViewModel: EditBasicInfoViewModel by viewModels()
    private lateinit var loader: androidx.appcompat.app.AlertDialog
    private lateinit var accessToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySelectCareTypeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get token
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()

        binding.backArrow.setOnClickListener {
            finish()
        }
    }
}