package com.singgihrp.adminljr.view.main

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.singgihrp.adminljr.R
import com.singgihrp.adminljr.databinding.FragmentLoginBinding
import com.singgihrp.adminljr.util.Status
import com.singgihrp.adminljr.view.support.Dialog
import com.singgihrp.adminljr.viewmodel.LoginViewModel

class LoginFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var loginViewModel: LoginViewModel
    private var sharedIdValue: Boolean = false

    private lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        dialog = Dialog(requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        binding.btnLogin.setOnClickListener(this)
    }

    private fun login(email: String, password: String){
        loginViewModel.loginUser(email, password).observe(viewLifecycleOwner){event->
            event.getContentIfNotHandled().let {
                if(it!=null){
                    when(it.status) {
                        Status.SUCCESS -> {
                            dialog.dismissDialog()
                            checkUser()
                        }
                        Status.ERROR -> {
                            dialog.dismissDialog()
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }
                        Status.LOADING -> dialog.startDialog()
                    }
                }else{
                    Toast.makeText(requireContext(), it?.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkUser(){
        loginViewModel.getUserData()
        loginViewModel.userdata.observe(viewLifecycleOwner){event->
            event.getContentIfNotHandled().let {
                if(it!=null && it.role == "admin"){
                    Toast.makeText(requireActivity(), "Berhasil Masuk", Toast.LENGTH_SHORT).show()
                    val sharedPreferences: SharedPreferences = context?.getSharedPreferences("AppAuth", Context.MODE_PRIVATE)!!
                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    editor.putBoolean("login", true)
                    editor.apply()
                    onStart()
                }else if(it!=null && it.role == "pengguna"){
                    Toast.makeText(requireContext(), "User tidak diijinkan masuk", Toast.LENGTH_SHORT).show()
                }else{
                    println(it)
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_login -> {
                val email = binding.etfieldEmail.text.toString().trim()
                val pwd = binding.etfieldPassword.text.toString().trim()
                login(email, pwd)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val sharedPreferences: SharedPreferences = context?.getSharedPreferences("AppAuth", Context.MODE_PRIVATE)!!
        sharedIdValue = sharedPreferences.getBoolean("login", false)
        if(sharedIdValue){
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}