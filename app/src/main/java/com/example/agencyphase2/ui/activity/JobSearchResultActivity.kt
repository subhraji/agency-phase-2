package com.example.agencyphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agencyphase2.R
import com.example.agencyphase2.adapter.SearchJobAdapter
import com.example.agencyphase2.databinding.ActivityJobSearchResultBinding
import com.example.agencyphase2.databinding.ActivitySearchBinding
import com.example.agencyphase2.model.pojo.search_job.Data
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.viewmodel.SearchJobViewModel
import com.user.caregiver.gone
import com.user.caregiver.isConnectedToInternet
import com.user.caregiver.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JobSearchResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJobSearchResultBinding
    private var job_type: String? = null

    private lateinit var accessToken: String
    private val mSearchJobViewModel: SearchJobViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityJobSearchResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get token
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()

        val extras = intent.extras
        if (extras != null) {
            job_type = intent?.getStringExtra("job_type")!!
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        //observe
        searchJobObserver()
    }

    override fun onResume() {
        super.onResume()
        binding.jobsRecycler.gone()
        binding.noDataLottie.gone()
        binding.jobsShimmerView.visible()
        binding.jobsShimmerView.startShimmer()
        if(isConnectedToInternet()){
            mSearchJobViewModel.searchJob(
                job_type,
                accessToken
            )
        }else{
            Toast.makeText(this,"Oops! No internet connection.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun searchJobObserver(){
        mSearchJobViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    binding.jobsShimmerView.gone()
                    binding.jobsShimmerView.stopShimmer()
                    if(outcome.data?.success == true){
                        if(outcome.data?.data!!.size > 0 && outcome.data?.data != null){
                            binding.jobsRecycler.visible()
                            binding.noDataLottie.gone()
                            fillRecycler(outcome.data?.data!!)
                        }else{
                            binding.jobsRecycler.gone()
                            binding.noDataLottie.visible()
                        }
                        mSearchJobViewModel.navigationComplete()
                    }else{
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Outcome.Failure<*> -> {
                    Toast.makeText(this,outcome.e.message, Toast.LENGTH_SHORT).show()

                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })
    }

    private fun fillRecycler(list: List<Data>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.jobsRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = SearchJobAdapter(list.toMutableList(),this@JobSearchResultActivity)
        }
    }
}