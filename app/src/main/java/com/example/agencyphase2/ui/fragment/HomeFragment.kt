package com.example.agencyphase2.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.agencyphase2.R
import com.example.agencyphase2.adapter.HomeViewPagerAdapter
import com.example.agencyphase2.databinding.FragmentHomeBinding
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.ui.activity.AskLocationActivity
import com.example.agencyphase2.ui.activity.JobPostActivity
import com.example.agencyphase2.ui.activity.ProfileActivity
import com.example.agencyphase2.utils.Constants
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.viewmodel.GetProfileViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.user.caregiver.gone
import com.user.caregiver.isConnectedToInternet
import com.user.caregiver.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val tabTitles = arrayListOf("     Post     "," Accepted ", "    Completed    ", " Canceled ")

    private val mGetProfileViewModel: GetProfileViewModel by viewModels()
    private lateinit var accessToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //get token
        accessToken = "Bearer "+ PrefManager.getKeyAuthToken()

        setUpTabLayoutWithViewPager()

        binding.userImageView.setOnClickListener {
            val intent = Intent(requireActivity(), ProfileActivity::class.java)
            startActivity(intent)
        }

        //observer
        getProfileObserver()
    }

    override fun onResume() {
        super.onResume()
        if(requireActivity().isConnectedToInternet()){
            mGetProfileViewModel.getProfile(accessToken)
        }else{
            Toast.makeText(requireActivity(),"No internet connection", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setUpTabLayoutWithViewPager() {
        binding.viewPager.adapter = HomeViewPagerAdapter(this)
        binding.viewPager.setUserInputEnabled(false)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        for (i in 0..3) {
            val textView = LayoutInflater.from(requireContext())
                .inflate(R.layout.tab_title_layout, null) as TextView
            binding.tabLayout.getTabAt(i)?.customView = textView
            textView.text = tabTitles[i]
        }
    }

    private fun getProfileObserver(){
        mGetProfileViewModel.response.observe(viewLifecycleOwner, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    if(outcome.data?.success == true){

                        if(outcome.data!!.data != null){
                            Glide.with(requireActivity()).load(Constants.PUBLIC_URL+ outcome.data!!.data.photo)
                                .placeholder(R.color.color_grey)
                                .into(binding.userImageView)
                        }

                        mGetProfileViewModel.navigationComplete()
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