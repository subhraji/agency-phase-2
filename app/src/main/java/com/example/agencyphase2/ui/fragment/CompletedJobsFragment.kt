package com.example.agencyphase2.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agencyphase2.adapter.CompletedJobAdapter
import com.example.agencyphase2.databinding.FragmentCompletedJobsBinding
import com.example.agencyphase2.model.pojo.get_complete_jobs.Data
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.viewmodel.GetCompleteJobViewModel
import com.user.caregiver.gone
import com.user.caregiver.isConnectedToInternet
import com.user.caregiver.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CompletedJobsFragment : Fragment() {
    private var _binding: FragmentCompletedJobsBinding? = null
    private val binding get() = _binding!!

    private val mGetCompleteJobViewModel: GetCompleteJobViewModel by viewModels()
    private lateinit var accessToken: String

    private var pageNumber = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCompletedJobsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //get token
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()

        //observe
        getCompleteJobsObserve()
    }

    override fun onResume() {
        super.onResume()
        binding.completedJobRecycler.gone()
        binding.completedJobsShimmerView.visible()
        binding.completedJobsShimmerView.startShimmer()
        binding.noDataLottie.gone()

        if(requireActivity().isConnectedToInternet()){
            mGetCompleteJobViewModel.getCompleteJob(accessToken, pageNumber, 0)
        }else{
            Toast.makeText(requireActivity(),"No internet connection.",Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCompleteJobsObserve(){
        mGetCompleteJobViewModel.response.observe(viewLifecycleOwner, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    binding.completedJobsShimmerView.stopShimmer()
                    binding.completedJobsShimmerView.gone()
                    if(outcome.data?.success == true){
                        if(outcome.data?.data != null && outcome.data?.data?.size != 0){
                            binding.completedJobRecycler.visible()
                            fillRecyclerView(outcome.data?.data!!)
                            binding.noDataLottie.gone()
                        }else{
                            binding.noDataLottie.visible()
                        }
                        mGetCompleteJobViewModel.navigationComplete()
                    }else{
                        Toast.makeText(requireActivity(),outcome.data!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Outcome.Failure<*> -> {
                    Toast.makeText(requireActivity(),outcome.e.message, Toast.LENGTH_SHORT).show()

                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })
    }

    private fun fillRecyclerView(list: List<Data>) {
        val linearlayoutManager = LinearLayoutManager(activity)
        binding.completedJobRecycler.apply {
            layoutManager = linearlayoutManager
            setHasFixedSize(true)
            isFocusable = false
            adapter = CompletedJobAdapter(list,requireActivity())
        }
    }
}