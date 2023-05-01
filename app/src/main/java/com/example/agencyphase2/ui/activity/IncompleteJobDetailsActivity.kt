package com.example.agencyphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agencyphase2.R
import com.example.agencyphase2.adapter.BulletPointAdapter
import com.example.agencyphase2.databinding.ActivityEmailVerificationBinding
import com.example.agencyphase2.databinding.ActivityIncompleteJobDetailsBinding
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.viewmodel.GetPostJobDetailsViewModel
import com.google.android.material.snackbar.Snackbar
import com.user.caregiver.gone
import com.user.caregiver.isConnectedToInternet
import com.user.caregiver.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IncompleteJobDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIncompleteJobDetailsBinding
    private val mGetPostJobsViewModel: GetPostJobDetailsViewModel by viewModels()
    private lateinit var accessToken: String
    private var id: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityIncompleteJobDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get token
        accessToken = "Bearer "+ PrefManager.getKeyAuthToken()

        val extras = intent.extras
        if (extras != null) {
            id = intent?.getIntExtra("id",0)!!
        }

        binding.medicalRecycler.gone()
        binding.medicalHisHtv.gone()
        binding.jobExpRecycler.gone()
        binding.jobExpHtv.gone()
        binding.otherReqRecycler.gone()
        binding.otherReqHtv.gone()
        binding.noCheckListTv.gone()
        binding.checkListRecycler.gone()

        //observer
        getPostJobsObserve()

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

    override fun onResume() {
        super.onResume()
        binding.shimmerView.visible()
        binding.shimmerView.startShimmer()
        binding.mainLay.gone()
        if(isConnectedToInternet()){
            mGetPostJobsViewModel.getPostJobDetails(token = accessToken, id = id)
        }else{
            Snackbar.make(binding.root,"Oops!! No internet connection", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun clickJobOverview(){
        binding.jobOverviewTv.setBackgroundResource(R.color.black)
        binding.jobOverviewTv.setTextColor(ContextCompat.getColor(this, R.color.white))

        binding.checkListTv.setBackgroundResource(R.color.white)
        binding.checkListTv.setTextColor(ContextCompat.getColor(this, R.color.black))

        binding.relativeLay1.visible()
        binding.relativeLay2.gone()
    }

    private fun clickCheckList(){
        binding.checkListTv.setBackgroundResource(R.color.black)
        binding.checkListTv.setTextColor(ContextCompat.getColor(this, R.color.white))

        binding.jobOverviewTv.setBackgroundResource(R.color.white)
        binding.jobOverviewTv.setTextColor(ContextCompat.getColor(this, R.color.black))

        binding.relativeLay2.visible()
        binding.relativeLay1.gone()
    }

    private fun getPostJobsObserve(){
        mGetPostJobsViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    if(outcome.data?.success == true){
                        if(outcome.data?.data != null){
                            binding.shimmerView.gone()
                            binding.shimmerView.stopShimmer()
                            binding.mainLay.visible()
                            binding.statusTv.text = outcome.data!!.data.status.toString()
                            binding.jobTitleTv.text = outcome.data!!.data.title.toString()
                            binding.jobDescTv.text = outcome.data!!.data.description.toString()
                            binding.dateHtv.text = outcome.data!!.data.start_date.toString()+" to "+outcome.data!!.data.end_date.toString()
                            binding.timeTv.text = outcome.data!!.data.start_time.toString()+" - "+outcome.data!!.data.end_time.toString()
                            binding.priceTv.text = "$"+outcome.data!!.data.amount.toString()
                            binding.personCountTv.text = outcome.data!!.data.care_items.size.toString()+" "+outcome.data!!.data.care_type
                            binding.locTv.text = outcome.data!!.data.address.toString()

                            var gen = ""
                            for(i in outcome.data!!.data.care_items){
                                if(gen.isEmpty()){
                                    gen = i.gender+": "+i.age
                                }else{
                                    gen = gen+", "+i.gender+": "+i.age
                                }
                            }
                            binding.personAgeTv.text = gen

                            /*setTimer(
                                getDurationHour(
                                    getCurrentDate(),
                                    parseDateToddMMyyyy("${outcome.data!!.data.start_date} ${outcome.data!!.data.start_time}")!!
                                )
                            )*/

                            if(outcome.data!!.data.medical_history.isNotEmpty() && outcome.data!!.data.medical_history != null){
                                binding.medicalRecycler.visible()
                                binding.medicalHisHtv.visible()
                                medicalHistoryFillRecycler(outcome.data!!.data.medical_history.toMutableList())
                            }

                            outcome.data!!.data.expertise?.let {
                                if(outcome.data!!.data.expertise.isNotEmpty() && outcome.data!!.data.expertise != null){
                                    binding.jobExpRecycler.visible()
                                    binding.jobExpHtv.visible()
                                    jobExpFillRecycler(outcome.data!!.data.expertise.toMutableList())
                                }
                            }

                            if(outcome.data!!.data.other_requirements.isNotEmpty() && outcome.data!!.data.other_requirements != null){
                                binding.otherReqRecycler.visible()
                                binding.otherReqHtv.visible()
                                otherFillRecycler(outcome.data!!.data.other_requirements.toMutableList())
                            }

                            if(outcome.data!!.data.check_list.isNotEmpty()){
                                binding.checkListRecycler.visible()
                                binding.noCheckListTv.gone()
                                checkListFillRecycler(outcome.data!!.data.check_list.toMutableList())
                            }else{
                                binding.noCheckListTv.visible()
                            }
                        }else{
                            Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        }
                        mGetPostJobsViewModel.navigationComplete()
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

    private fun medicalHistoryFillRecycler(list: MutableList<String>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.medicalRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = BulletPointAdapter(list,this@IncompleteJobDetailsActivity)
        }
    }

    private fun jobExpFillRecycler(list: MutableList<String>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.jobExpRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = BulletPointAdapter(list,this@IncompleteJobDetailsActivity)
        }
    }

    private fun otherFillRecycler(list: MutableList<String>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.otherReqRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = BulletPointAdapter(list,this@IncompleteJobDetailsActivity)
        }
    }

    private fun checkListFillRecycler(list: MutableList<String>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.checkListRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = BulletPointAdapter(list,this@IncompleteJobDetailsActivity)
        }
    }
}