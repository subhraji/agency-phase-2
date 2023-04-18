package com.example.agencyphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.ActivityCareGiverProfileBinding
import com.example.agencyphase2.databinding.ActivityJobPostBinding
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.viewmodel.ChangePasswordViewModel
import com.example.agencyphase2.viewmodel.GetCaregiverProfileViewModel
import com.user.caregiver.isConnectedToInternet
import com.user.caregiver.loadingDialog

class CareGiverProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCareGiverProfileBinding

    private lateinit var accessToken: String
    private lateinit var loader: androidx.appcompat.app.AlertDialog
    private val mGetCaregiverProfileViewModel: GetCaregiverProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCareGiverProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get token
        accessToken = "Bearer "+ PrefManager.getKeyAuthToken()
        loader = this.loadingDialog()

        //observe
        getCaregiverProfileObserve()
    }

    override fun onResume() {
        super.onResume()
        if(isConnectedToInternet()){

        }else{

        }
    }

    private fun getCaregiverProfileObserve(){
        mGetCaregiverProfileViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){

                        mGetCaregiverProfileViewModel.navigationComplete()
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

}