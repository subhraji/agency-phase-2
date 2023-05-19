package com.example.agencyphase2.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.setPadding
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.agencyphase2.R
import com.example.agencyphase2.adapter.ClientListAdapter
import com.example.agencyphase2.adapter.GenderAgeAdapter
import com.example.agencyphase2.databinding.ActivityClientListBinding
import com.example.agencyphase2.databinding.ActivityJobPostBinding
import com.example.agencyphase2.model.pojo.GenderAgeItemCountModel
import com.example.agencyphase2.model.pojo.TestModel
import com.example.agencyphase2.model.pojo.get_clients.Data
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.viewmodel.GetClientsViewModel
import com.example.agencyphase2.viewmodel.ResendOtpViewModel
import com.user.caregiver.gone
import com.user.caregiver.isConnectedToInternet
import com.user.caregiver.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClientListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClientListBinding
    private val mGetClientsViewModel: GetClientsViewModel by viewModels()
    private lateinit var accessToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityClientListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get token
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()

        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.addClientBtn.setOnClickListener {
            val intent = Intent(this, AddClientActivity::class.java)
            startActivity(intent)
        }

        //observe
        getClientsObserve()
    }

    override fun onResume() {
        super.onResume()
        binding.clientListRecycler.gone()
        binding.clientShimmerView.visible()
        binding.clientShimmerView.startShimmer()
        binding.noDataLottie.gone()

        if(isConnectedToInternet()){
            mGetClientsViewModel.getClients(accessToken)
        }else{
            Toast.makeText(this,"Oops!! No internet connection.",Toast.LENGTH_SHORT).show()
        }
    }

    private fun getClientsObserve(){
        mGetClientsViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    binding.clientShimmerView.stopShimmer()
                    binding.clientShimmerView.gone()
                    if(outcome.data?.success == true){
                        if(outcome.data?.data != null && outcome.data?.data?.size != 0){
                            binding.clientListRecycler.visible()
                            fillClientListRecycler(outcome.data?.data!!)
                            binding.noDataLottie.gone()
                        }else{
                            binding.clientListRecycler.gone()
                            binding.noDataLottie.visible()
                        }
                        mGetClientsViewModel.navigationComplete()
                    }else{
                        if(outcome.data?.http_status_code == 401){
                            PrefManager.clearPref()
                            startActivity(Intent(this, ChooseLoginRegActivity::class.java))
                            finish()
                        }else{
                            Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        }
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

    private fun fillClientListRecycler(list: List<Data>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.clientListRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = ClientListAdapter(list.toMutableList(),this@ClientListActivity)
        }
    }
}