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
import com.bumptech.glide.Glide
import com.example.agencyphase2.R
import com.example.agencyphase2.adapter.OngoingJobsAdapter
import com.example.agencyphase2.adapter.UpcomingJobsAdapter
import com.example.agencyphase2.databinding.FragmentAcceptedBinding
import com.example.agencyphase2.databinding.FragmentOngoingBinding
import com.example.agencyphase2.model.pojo.get_ongoing_job.Data
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.utils.Constants
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.viewmodel.GetOngoingJobViewModel
import com.example.agencyphase2.viewmodel.GetProfileViewModel
import com.example.agencyphase2.viewmodel.GetUpcommingJobViewModel
import com.user.caregiver.gone
import com.user.caregiver.isConnectedToInternet
import com.user.caregiver.visible
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel

@AndroidEntryPoint
class AcceptedFragment : Fragment() {
    private var _binding: FragmentAcceptedBinding? = null
    private val binding get() = _binding!!

    private lateinit var accessToken: String
    private val mGetOngoingJobViewModel: GetOngoingJobViewModel by viewModels()
    private val mGetUpcommingJobViewModel: GetUpcommingJobViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAcceptedBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //get token
        accessToken = "Bearer "+ PrefManager.getKeyAuthToken()

        //observe
        getOngoingJobObserver()
        getUpcomingJobObserver()

    }

    override fun onResume() {
        super.onResume()

        binding.ongoingShimmerView.visible()
        binding.upcomingShimmerView.visible()
        binding.ongoingJobRecycler.gone()
        binding.upcomingJobRecycler.gone()
        binding.ongoingShimmerView.startShimmer()
        binding.upcomingShimmerView.startShimmer()
        binding.noDataOngoingLottie.gone()
        binding.noDataUpcomingLottie.gone()

        if(requireActivity().isConnectedToInternet()){
            mGetOngoingJobViewModel.getOnGoingJob(accessToken, 0)
            mGetUpcommingJobViewModel.getUpcommingJob(accessToken, 0)
        }else{
            Toast.makeText(requireActivity(),"No internet connection", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getOngoingJobObserver(){
        mGetOngoingJobViewModel.response.observe(viewLifecycleOwner, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    if(outcome.data?.success == true){
                        binding.ongoingShimmerView.stopShimmer()
                        binding.ongoingShimmerView.gone()
                        if(outcome.data?.data!!.isNotEmpty()){
                            binding.ongoingJobRecycler.visible()
                            fillOngoingJobsRecycler(outcome.data?.data!!)
                            binding.noDataOngoingLottie.gone()
                        }else{
                            binding.ongoingJobRecycler.gone()
                            binding.noDataOngoingLottie.visible()
                        }
                        mGetOngoingJobViewModel.navigationComplete()
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

    private fun getUpcomingJobObserver(){
        mGetUpcommingJobViewModel.response.observe(viewLifecycleOwner, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    if(outcome.data?.success == true){
                        binding.upcomingShimmerView.stopShimmer()
                        binding.upcomingShimmerView.gone()
                        if(outcome.data?.data!!.isNotEmpty()){
                            binding.upcomingJobRecycler.visible()
                            fillUpcomingJobsRecycler(outcome.data?.data!!)
                            binding.noDataUpcomingLottie.gone()

                        }else{
                            binding.upcomingJobRecycler.gone()
                            binding.noDataUpcomingLottie.visible()

                        }
                        mGetUpcommingJobViewModel.navigationComplete()
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

    private fun fillOngoingJobsRecycler(list: List<Data>) {
        val gridLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.ongoingJobRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = OngoingJobsAdapter(list,requireActivity())
        }
    }

    private fun fillUpcomingJobsRecycler(list: List<com.example.agencyphase2.model.pojo.get_upcomming_jobs.Data>) {
        val gridLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.upcomingJobRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = UpcomingJobsAdapter(list,requireActivity())
        }
    }
}