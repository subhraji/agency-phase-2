package com.example.agencyphase2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agencyphase2.adapter.DashOpenJobsAdapter
import com.example.agencyphase2.databinding.ActivityMainBinding
import com.example.agencyphase2.databinding.ActivitySplashBinding
import com.example.agencyphase2.model.TestModel
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val quickCallList = ArrayList<TestModel>()
        quickCallList.add(TestModel("a"))
        quickCallList.add(TestModel("b"))
        quickCallList.add(TestModel("c"))
        fillQuickCallsRecycler(quickCallList)
    }

    private fun fillQuickCallsRecycler(list: List<TestModel>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.postJobsRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = DashOpenJobsAdapter(list,this@MainActivity,true)
        }
    }
}