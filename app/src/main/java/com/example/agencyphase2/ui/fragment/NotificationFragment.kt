package com.example.agencyphase2.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agencyphase2.R
import com.example.agencyphase2.adapter.CompletedJobAdapter
import com.example.agencyphase2.adapter.NotificationListAdapter
import com.example.agencyphase2.databinding.FragmentLoginBinding
import com.example.agencyphase2.databinding.FragmentNotificationBinding
import com.example.agencyphase2.model.pojo.TestModel
import com.example.agencyphase2.model.pojo.get_complete_jobs.Data
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.viewmodel.GetCompleteJobViewModel
import com.example.agencyphase2.viewmodel.GetNotificationsViewModel
import com.example.agencyphase2.viewmodel.MarkReadNotificationViewModel
import com.google.android.material.snackbar.Snackbar
import com.user.caregiver.gone
import com.user.caregiver.isConnectedToInternet
import com.user.caregiver.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationFragment : Fragment() {
    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    private lateinit var accessToken: String
    private val mMarkReadNotificationViewModel: MarkReadNotificationViewModel by viewModels()
    private val mGetNotificationsViewModel: GetNotificationsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onResume() {
        binding.notificationRecycler.gone()
        binding.completedJobsShimmerView.visible()
        binding.completedJobsShimmerView.startShimmer()
        binding.noDataLottie.gone()

        if(requireActivity().isConnectedToInternet()){
            mGetNotificationsViewModel.getNotifications(accessToken)
        }else{
            Toast.makeText(requireActivity(),"No internet connection.",Toast.LENGTH_SHORT).show()
        }
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //get token
        accessToken = "Bearer "+ PrefManager.getKeyAuthToken()

        //observe
        markReadNotificationsObserve()
        getNotificationsObserve()
    }

    private fun fillRecyclerView(list: MutableList<com.example.agencyphase2.model.pojo.get_notifications.Data>) {
        val linearlayoutManager = LinearLayoutManager(activity)
        binding.notificationRecycler.apply {
            layoutManager = linearlayoutManager
            setHasFixedSize(true)
            isFocusable = false
            adapter = NotificationListAdapter(list,requireActivity())
        }
    }

    private fun markReadNotificationsObserve(){
        mMarkReadNotificationViewModel.response.observe(viewLifecycleOwner, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    if(outcome.data?.success == true){

                        mMarkReadNotificationViewModel.navigationComplete()
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

    private fun getNotificationsObserve(){
        mGetNotificationsViewModel.response.observe(viewLifecycleOwner, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    binding.completedJobsShimmerView.stopShimmer()
                    binding.completedJobsShimmerView.gone()
                    if(outcome.data?.success == true){
                        if(outcome.data?.data != null && outcome.data?.data?.size != 0){
                            binding.notificationRecycler.visible()
                            fillRecyclerView(outcome.data?.data!!.toMutableList())
                            binding.noDataLottie.gone()
                        }else{
                            binding.noDataLottie.visible()
                        }
                        mGetNotificationsViewModel.navigationComplete()
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

}