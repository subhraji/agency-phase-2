package com.example.agencyphase2.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.agencyphase2.ui.fragment.*

class HomeViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PostFragment()
            1 -> AcceptedFragment()
            2 -> CompletedJobsFragment()
            else -> CanceledFragment()
        }
    }
}