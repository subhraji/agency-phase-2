package com.example.agencyphase2.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.ActivityJobPostBinding
import com.example.agencyphase2.databinding.ActivitySignUpBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class JobPostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJobPostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityJobPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.careTypeBtn.setOnClickListener {
            showCareTypeBottomSheet()
        }

        binding.dateTimeBtn.setOnClickListener {
            showDateTimeBottomSheet()
        }

        binding.nextStepBtn.setOnClickListener {
            val intent = Intent(this, PostJobPreviewActivity::class.java)
            startActivity(intent)
        }

        binding.backArrow.setOnClickListener {
            finish()
        }
    }

    private fun showCareTypeBottomSheet(){
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.care_type_bottomsheet, null)

        val btnSave = view.findViewById<CardView>(R.id.save_btn)
        val btnClear = view.findViewById<ImageView>(R.id.clear_btn)

        btnClear.setOnClickListener {
            dialog.dismiss()
        }
        btnSave.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setCancelable(false)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun showDateTimeBottomSheet(){
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.select_date_time_bottomsheet, null)

        val btnSave = view.findViewById<TextView>(R.id.save_btn)
        val btnClear = view.findViewById<ImageView>(R.id.clear_btn)

        btnClear.setOnClickListener {
            dialog.dismiss()
        }
        btnSave.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setCancelable(false)
        dialog.setContentView(view)
        dialog.show()
    }

}