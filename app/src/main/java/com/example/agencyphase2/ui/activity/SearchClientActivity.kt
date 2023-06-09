package com.example.agencyphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agencyphase2.adapter.SearchClientAdapter
import com.example.agencyphase2.databinding.ActivityCreateClientBinding
import com.example.agencyphase2.model.pojo.get_clients.Data
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.utils.AddClientClickListener
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.viewmodel.SearchClientViewModel
import com.user.caregiver.gone
import com.user.caregiver.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchClientActivity : AppCompatActivity(), AddClientClickListener {
    private lateinit var binding: ActivityCreateClientBinding

    private lateinit var accessToken: String
    lateinit var searchClientAdapter: SearchClientAdapter

    private val mSearchClientViewModel: SearchClientViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateClientBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpRecyclerView()
        binding.clearBtn.gone()
        binding.progressBar.gone()

        //get token
        accessToken = "Bearer " + PrefManager.getKeyAuthToken()

        //observe
        searchClientObserver()

        mSearchClientViewModel.searchClient(accessToken,"")
        binding.progressBar.visible()

        binding.clearBtn.setOnClickListener {
            setUpRecyclerView()
            binding.etSearch.text = null
        }

        binding.backArrow.setOnClickListener {
            finish()
        }

        var job: Job? = null
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                job?.cancel()
                job = MainScope().launch {
                    delay(500L)
                    editable?.let {
                        if (editable.toString().isNotEmpty()) {
                            binding.clearBtn.visible()
                            mSearchClientViewModel.searchClient(accessToken,editable.toString())
                            binding.progressBar.visible()
                        }else{
                            binding.clearBtn.gone()
                        }
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
    }

    private fun setUpRecyclerView(){
        searchClientAdapter = SearchClientAdapter(this)
        binding.rvSearchNews.apply {
            adapter = searchClientAdapter
            layoutManager = LinearLayoutManager(this@SearchClientActivity)
        }
    }

    private fun searchClientObserver(){
        mSearchClientViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    binding.progressBar.gone()
                    if(outcome.data?.success == true){
                        searchClientAdapter.differ.submitList(outcome.data?.data)
                        mSearchClientViewModel.navigationComplete()
                    }else{
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Outcome.Failure<*> -> {
                    Toast.makeText(this,outcome.e.message, Toast.LENGTH_SHORT).show()
                    binding.progressBar.gone()

                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })
    }

    override fun onAddClick(view: View, data: Data) {
        JobPostActivity.clientItem = data
        finish()
    }

}