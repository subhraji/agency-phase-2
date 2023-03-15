package com.example.agencyphase2.ui.activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agencyphase2.R
import com.example.agencyphase2.adapter.AuthorizeOfficerAdapter
import com.example.agencyphase2.databinding.ActivityAskLocationBinding
import com.example.agencyphase2.databinding.ActivityAuthOfficerListBinding
import com.example.agencyphase2.databinding.ActivityAuthorizedPersonInfoAddBinding
import com.example.agencyphase2.model.pojo.get_authorize_officer.Data
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.utils.EditDeleteClickListener
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.viewmodel.DeleteAuthOfficerViewModel
import com.example.agencyphase2.viewmodel.GetAuthorizeOfficerViewModel
import com.user.caregiver.gone
import com.user.caregiver.isConnectedToInternet
import com.user.caregiver.loadingDialog
import com.user.caregiver.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthOfficerListActivity : AppCompatActivity(), EditDeleteClickListener {
    private lateinit var binding: ActivityAuthOfficerListBinding
    private lateinit var accessToken: String
    private val mGetAuthorizeOfficerViewModel: GetAuthorizeOfficerViewModel by viewModels()
    private val mDeleteAuthOfficerViewModel: DeleteAuthOfficerViewModel by viewModels()
    private lateinit var loader: androidx.appcompat.app.AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAuthOfficerListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //get token
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()
        loader = this@AuthOfficerListActivity.loadingDialog()

        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.addAuthOfficerBtn.setOnClickListener {
            val intent = Intent(this, AuthorizedPersonInfoAddActivity::class.java)
            startActivity(intent)
        }

        //observe
        getAuthorizeInoObserve()
        deleteAuthOfficerObserver()
    }

    override fun onResume() {
        super.onResume()
        if(isConnectedToInternet()){
            binding.getAuthOfficerShimmerView.visible()
            binding.getAuthOfficerShimmerView.startShimmer()
            binding.authOfficerRecycler.visible()
            mGetAuthorizeOfficerViewModel.getAuthOfficer(accessToken)
        }else{
            Toast.makeText(this,"Oops!! No internet connection.", Toast.LENGTH_SHORT).show()
        }
    }
    private fun getAuthorizeInoObserve(){
        mGetAuthorizeOfficerViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    binding.getAuthOfficerShimmerView.gone()
                    binding.getAuthOfficerShimmerView.stopShimmer()
                    if(outcome.data?.success == true){
                        if(outcome.data?.data != null && outcome.data?.data?.size != 0){
                            binding.authOfficerRecycler.visible()
                            val list = outcome.data?.data?.filterIndexed { index, data -> index!=0 }
                            list?.let {
                                fillAuthRecyclerView(it)
                            }
                        }
                        mGetAuthorizeOfficerViewModel.navigationComplete()
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

    private fun fillAuthRecyclerView(list: List<Data>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.authOfficerRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = AuthorizeOfficerAdapter(list,this@AuthOfficerListActivity, this@AuthOfficerListActivity)
        }
    }

    override fun onClick(view: View, id: Int) {
        showRemoveAuthOfficerDialog(id)
    }

    private fun showRemoveAuthOfficerDialog(id: Int){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Remove Authorize Officer")
        builder.setMessage("Do you want to remove the authorize officer ?")
        builder.setIcon(R.drawable.ic_baseline_warning_amber_24)
        builder.setPositiveButton("Yes"){dialogInterface, which ->
            if(isConnectedToInternet()){
                mDeleteAuthOfficerViewModel.deleteAuthOfficer(accessToken,id)
                loader.show()
            }else{
                Toast.makeText(this,"No internet connection.",Toast.LENGTH_LONG).show()
            }
        }
        builder.setNegativeButton("No"){dialogInterface, which ->
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun deleteAuthOfficerObserver(){
        mDeleteAuthOfficerViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        mDeleteAuthOfficerViewModel.navigationComplete()
                        binding.getAuthOfficerShimmerView.visible()
                        binding.getAuthOfficerShimmerView.startShimmer()
                        binding.authOfficerRecycler.gone()
                        mGetAuthorizeOfficerViewModel.getAuthOfficer(accessToken)
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