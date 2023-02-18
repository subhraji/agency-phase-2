package com.example.agencyphase2.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.agencyphase2.R
import com.example.agencyphase2.adapter.HomeViewPagerAdapter
import com.example.agencyphase2.databinding.FragmentHomeBinding
import com.example.agencyphase2.ui.activity.JobPostActivity
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val tabTitles = arrayListOf("     Post     "," Accepted ", "    Completed    ", " Canceled ")

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

        setUpTabLayoutWithViewPager()

        /*binding.userImageView.setOnClickListener {
            val intent = Intent(requireActivity(), JobPostActivity::class.java)
            startActivity(intent)
        }*/
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
}