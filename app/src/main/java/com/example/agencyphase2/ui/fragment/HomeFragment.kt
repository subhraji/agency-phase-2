package com.example.agencyphase2.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.agencyphase2.R
import com.example.agencyphase2.adapter.HomeViewPagerAdapter
import com.example.agencyphase2.databinding.FragmentHomeBinding
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.ui.activity.*
import com.example.agencyphase2.utils.Constants
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.viewmodel.GetProfileViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.user.caregiver.isConnectedToInternet
import dagger.hilt.android.AndroidEntryPoint
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject
import java.net.URISyntaxException


@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val tabTitles = arrayListOf("     Post     "," Accepted ", "    Completed    ", "Incomplete")

    private val mGetProfileViewModel: GetProfileViewModel by viewModels()
    private lateinit var accessToken: String
    private var mSocket: Socket? = null

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

        binding.userImageView.setOnClickListener {
            val intent = Intent(requireActivity(), ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.dashSearchLay.setOnClickListener {
            val intent = Intent(requireActivity(), SearchActivity::class.java)
            startActivity(intent)
        }

        binding.imageView1.setOnClickListener {
            /*val intent = Intent(requireActivity(), ChatActivity::class.java)
            startActivity(intent)*/
        }

        //observer
        getProfileObserver()


        //socket......
        try {
            mSocket = IO.socket("https://891b-2405-201-a805-1a07-85b3-27a4-601a-d8fd.ngrok-free.app")
        } catch (e: URISyntaxException) {
            e.printStackTrace()
            Log.d("test_msg","socket error => ${e.message}")
        }
        mSocket?.on("test-msg", onNewMessage);
        mSocket?.connect()
        Log.d("socket_connect", "socket => ${mSocket?.connected()}")

        attemptSend()
    }

    override fun onResume() {
        super.onResume()
        if(requireActivity().isConnectedToInternet()){
            mGetProfileViewModel.getProfile(accessToken)
        }else{
            Toast.makeText(requireActivity(),"No internet connection", Toast.LENGTH_SHORT).show()
        }

        setUpTabLayoutWithViewPager()
    }

    private fun attemptSend() {
        val message: String = "android connected"
        mSocket!!.emit("test-msg", message)
    }

    private val onNewMessage: Emitter.Listener = object : Emitter.Listener {
        override fun call(vararg args: Any) {
            activity!!.runOnUiThread(Runnable {
                val data = args[0] as JSONObject
                val msg: String
                try {
                    msg = data.getString("hello")
                } catch (e: JSONException) {
                    return@Runnable
                }

                Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show()
            })
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

                        binding.numberTv3.text = outcome.data?.data?.job_count.toString()
                        binding.numberTv1.text = outcome.data?.data?.rating_count.toString()

                        outcome.data?.data?.revenue_count?.let {
                            var numberString = ""
                            var number = outcome.data?.data?.revenue_count!!.toDouble()

                            if (Math.abs(number / 1000000) > 1) {
                                numberString = (number / 1000000).toString() + "m";

                            } else if (Math.abs(number / 1000) > 1) {
                                numberString = (number / 1000).toString() + "k";

                            } else {
                                numberString = number.toString();

                            }
                            binding.numberTv2.text = numberString.toString()
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

    override fun onDestroy() {
        super.onDestroy()
        mSocket!!.disconnect()
    }

}