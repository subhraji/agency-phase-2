package com.example.agencyphase2.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.ActivityRegistrationBinding
import com.example.agencyphase2.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private val jobTypeList: Array<String> =  arrayOf("Search by job status", "Quick Call", "Bidding Started", "Open Job")
    private lateinit var job_type: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //spinner
        setUpSpinner()

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.searchBtn.setOnClickListener {
            if(!job_type.isEmpty()){
                val intent = Intent(this, JobSearchResultActivity::class.java)
                intent.putExtra("job_type",job_type)
                startActivity(intent)
            }else{
                Toast.makeText(this,"Please select a job type", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUpSpinner(){
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,jobTypeList)
        binding.careTypeSpinner.adapter = arrayAdapter

        binding.careTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            AdapterView.OnItemClickListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(p2 != 0){
                    job_type = jobTypeList[p2]
                }else{
                    job_type = ""
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }

        }
    }

}