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
import com.example.agencyphase2.adapter.ClientListAdapter
import com.example.agencyphase2.databinding.ActivityClientListBinding
import com.example.agencyphase2.model.pojo.get_clients.Data
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.utils.EditDeleteClickListener
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.viewmodel.DeleteClientViewModel
import com.example.agencyphase2.viewmodel.GetClientsViewModel
import com.user.caregiver.gone
import com.user.caregiver.isConnectedToInternet
import com.user.caregiver.loadingDialog
import com.user.caregiver.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClientListActivity : AppCompatActivity(), EditDeleteClickListener {
    private lateinit var binding: ActivityClientListBinding
    private val mGetClientsViewModel: GetClientsViewModel by viewModels()
    private val mDeleteClientViewModel: DeleteClientViewModel by viewModels()
    private lateinit var loader: androidx.appcompat.app.AlertDialog

    private lateinit var accessToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityClientListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get token
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()

        loader = this.loadingDialog(true)

        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.addClientBtn.setOnClickListener {
            val intent = Intent(this, AddClientActivity::class.java)
            intent.putExtra("from", "other")
            startActivity(intent)
        }

        //observe
        getClientsObserve()
        deleteClientsObserve()
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

    private fun deleteClientsObserve(){
        mDeleteClientViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){

                        binding.clientListRecycler.gone()
                        binding.clientShimmerView.visible()
                        binding.clientShimmerView.startShimmer()
                        binding.noDataLottie.gone()
                        mGetClientsViewModel.getClients(accessToken)

                        mDeleteClientViewModel.navigationComplete()
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
            adapter = ClientListAdapter(list.toMutableList(),this@ClientListActivity,this@ClientListActivity)
        }
    }

    override fun onClick(view: View, id: Int) {
        showDeletePopUp(id)
    }

    private fun showDeletePopUp(id: Int){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete")
        builder.setMessage("Do you want to delete the client ?")
        builder.setIcon(R.drawable.ic_baseline_warning_amber_24)
        builder.setPositiveButton("Yes"){dialogInterface, which ->
            if(isConnectedToInternet()){
                mDeleteClientViewModel.deleteClient(id.toString(), accessToken)
                loader.show()
            }else{
                Toast.makeText(this,"Oops!! No internet connection", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("No"){dialogInterface, which ->
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}