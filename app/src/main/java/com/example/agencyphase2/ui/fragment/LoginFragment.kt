package com.example.agencyphase2.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.agencyphase2.MainActivity
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.FragmentLoginBinding
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.ui.activity.AskLocationActivity
import com.example.agencyphase2.ui.activity.SignUpActivity
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.viewmodel.LoginViewModel
import com.example.agencyphase2.viewmodel.SignUpViewModel
import com.user.caregiver.isConnectedToInternet
import com.user.caregiver.loadingDialog
import com.user.caregiver.showKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var loader: androidx.appcompat.app.AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginObserve()

        //validation
        emailFocusListener()
        passwordFocusListener()

        binding.backBtn.setOnClickListener {
            requireActivity().finish()
        }

        binding.loginBtn.setOnClickListener {
            val validEmail = binding.emailTxtLay.helperText == null
            val validPassword = binding.passwordTxtLay.helperText == null

            if(validEmail){
                if(validPassword){
                    if(requireActivity().isConnectedToInternet()){
                        loginViewModel.login(
                            binding.emailTxt.text.toString(),
                            binding.passwordTxt.text.toString()
                        )
                        loader = requireActivity().loadingDialog()
                        loader.show()
                    }else{
                        Toast.makeText(requireActivity(),"No internet connection.", Toast.LENGTH_LONG).show()
                    }
                }else{
                    Toast.makeText(requireActivity(),binding.passwordTxtLay.helperText.toString(), Toast.LENGTH_SHORT).show()
                    binding.passwordTxt.showKeyboard()
                }
            }else{
                Toast.makeText(requireActivity(),binding.emailTxtLay.helperText.toString(), Toast.LENGTH_SHORT).show()
                binding.emailTxt.showKeyboard()
            }
        }

        binding.register.setOnClickListener {
            val intent = Intent(requireActivity(), SignUpActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        binding.forgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotPassEmailFragment)
        }
    }

    private fun emailFocusListener(){
        binding.emailTxt.doOnTextChanged { text, start, before, count ->
            binding.emailTxtLay.helperText = validEmail()
        }
    }

    private fun validEmail(): String? {
        val emailText = binding.emailTxt.text.toString()

        if(!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()){
            return "Invalid Email Address"
        }
        if(emailText.isEmpty()){
            return "provide email address"
        }

        return null
    }

    private fun passwordFocusListener() {
        binding.passwordTxt.doOnTextChanged { text, start, before, count ->
            binding.passwordTxtLay.helperText = validPassword()
        }
    }

    private fun validPassword(): String? {
        val passwordText = binding.passwordTxt.text.toString()

        if(passwordText.isEmpty()){
            return "provide password."
        }
        return  null
    }

    private fun loginObserve(){
        loginViewModel.response.observe(viewLifecycleOwner, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        if (outcome.data!!.token != null) {
                            outcome.data!!.token?.let { PrefManager.setKeyAuthToken(it) }
                        }
                        PrefManager.setLogInStatus(true)
                        val intent = Intent(requireActivity(), MainActivity::class.java)
                        startActivity(intent)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        requireActivity().finish()
                        loginViewModel.navigationComplete()
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