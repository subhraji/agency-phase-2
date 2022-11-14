package com.example.agencyphase2.ui.activity

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.agencyphase2.databinding.ActivityBasicInformationBinding

class BasicInformationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBasicInformationBinding
    val legalStructureList: Array<String> =  arrayOf("Select legal structure", "Solo Proprietorship", "Partnership", "Corporation", "Limited Liability Company")
    val orgTypeList: Array<String> =  arrayOf("Select organisation type", "For profit", "Non profit")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityBasicInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.nextStepBtn.setOnClickListener {
            val intent = Intent(this, OptionalBusinessInfoActivity::class.java)
            startActivity(intent)
        }

        binding.backArrow.setOnClickListener {
            finish()
        }

        //spinner
        setUpLsSpinner()
        setUpOrgType()
    }

    private fun setUpLsSpinner(){
        val arrayAdapter = ArrayAdapter(this, R.layout.simple_spinner_dropdown_item,legalStructureList)
        binding.lsSpinner.adapter = arrayAdapter
        binding.lsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            AdapterView.OnItemClickListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }

        }
    }

    private fun setUpOrgType(){
        val arrayAdapter = ArrayAdapter(this, R.layout.simple_spinner_dropdown_item,orgTypeList)
        binding.orgTypeSpinner.adapter = arrayAdapter
        binding.orgTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            AdapterView.OnItemClickListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }

        }
    }
}