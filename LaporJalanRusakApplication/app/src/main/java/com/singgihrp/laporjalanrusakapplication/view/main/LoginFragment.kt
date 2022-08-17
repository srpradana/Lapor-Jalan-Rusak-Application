package com.singgihrp.laporjalanrusakapplication.view.main

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
import com.singgihrp.laporjalanrusakapplication.R
import com.singgihrp.laporjalanrusakapplication.databinding.FragmentLoginBinding
import com.singgihrp.laporjalanrusakapplication.util.Status
import com.singgihrp.laporjalanrusakapplication.view.support.Dialog
import com.singgihrp.laporjalanrusakapplication.viewmodel.LoginViewModel

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
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        dialog = Dialog(requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        binding.tvToRegis.setOnClickListener(this)
        binding.btnLogin.setOnClickListener(this)
        binding.tvResetPassword.setOnClickListener(this)
    }

    private fun loginNew(email: String, password: String){
        loginViewModel.loginNew(email,password).observe(viewLifecycleOwner){event->
            event.getContentIfNotHandled().let {
                if(it!=null){
                    when (it.status) {
                        Status.SUCCESS -> {
                            dialog.dismissDialog()
                            checkUser()
                        }
                        Status.LOADING -> {
                            dialog.startDialog()
                        }
                        Status.ERROR -> {
                            dialog.dismissDialog()
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }else{
                    Toast.makeText(requireContext(), it?.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun checkUser(){
        loginViewModel.getUserData()
        loginViewModel.userData.observe(viewLifecycleOwner){event->
            event.getContentIfNotHandled().let {
                if(it!=null && it.role =="pengguna"){
                    Toast.makeText(context, "Berhasil Login", Toast.LENGTH_SHORT)
                        .show()
                    val sharedPreferences: SharedPreferences =
                        context?.getSharedPreferences(
                            "AppAuth",
                            Context.MODE_PRIVATE
                        )!!
                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    editor.putBoolean("login", true)
                    editor.apply()
                    onStart()
                }else if(it!=null && it.role == "admin"){
                    Toast.makeText(context, "Anda tidak memiliki kewenangan", Toast.LENGTH_SHORT)
                        .show()
                }else{
                    println(it)
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.tv_toRegis -> {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
            R.id.btn_login -> {
                val email = binding.etfieldLogUsername.text.toString().trim()
                val password = binding.etfieldLogPassword.text.toString().trim()
                if(validation()){
                        loginNew(email, password)
                }
            }
            R.id.tv_resetPassword -> {
                findNavController().navigate(R.id.action_loginFragment_to_resetPasswordFragment)
            }
        }
    }

    private fun validation(): Boolean{
        var validasi = true
        val emailField = binding.etfieldLogUsername.text.toString()
        val emailReg = binding.etLogUsername
        val passwordReg = binding.etRegPassword
        val passField = binding.etfieldLogPassword.text.toString()
        if(emailField.isEmpty()){
            validasi = false
            emailReg.error = "Email jangan kosong"
            emailReg.isErrorEnabled = true
            return validasi
        }else{
            emailReg.error = null
            emailReg.isErrorEnabled = false
        }
        if(passField.isEmpty()){
            validasi = false
            passwordReg.error = "Password jangan kosong"
            passwordReg.errorIconDrawable = null
            return validasi
        }else{
            passwordReg.error = null
            passwordReg.isErrorEnabled = false
        }
        return validasi
    }

    override fun onStart() {
        super.onStart()
        val sharedPreferences: SharedPreferences = context?.getSharedPreferences("AppAuth", Context.MODE_PRIVATE)!!
        sharedIdValue = sharedPreferences.getBoolean("login", false)
        if(sharedIdValue){
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}