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
import com.google.android.material.snackbar.Snackbar
import com.singgihrp.laporjalanrusakapplication.R
import com.singgihrp.laporjalanrusakapplication.databinding.FragmentRegisterBinding
import com.singgihrp.laporjalanrusakapplication.util.Status
import com.singgihrp.laporjalanrusakapplication.view.support.Dialog
import com.singgihrp.laporjalanrusakapplication.viewmodel.RegisterViewModel

class RegisterFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var registerViewModel: RegisterViewModel

    private lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        dialog = Dialog(requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerViewModel = ViewModelProvider(this)[RegisterViewModel::class.java]

        binding.btnRegister.setOnClickListener(this)
        binding.tvToLogin.setOnClickListener(this)

    }


    private fun registerNew(email: String, password: String){
        registerViewModel.registerNew(email, password).observe(viewLifecycleOwner){event->
            event.getContentIfNotHandled().let {
                if(it!=null){
                    when(it.status){
                        Status.SUCCESS -> {
                            dialog.dismissDialog()
                            Toast.makeText(context, "Berhasil Daftar", Toast.LENGTH_SHORT).show()
                            val sharedPreferences: SharedPreferences = context?.getSharedPreferences("AppAuth", Context.MODE_PRIVATE)!!
                            val editor: SharedPreferences.Editor = sharedPreferences.edit()
                            editor.putBoolean("login", true)
                            editor.apply()
                            Snackbar.make(binding.root, "Link Verifikasi sudah dikirim ke Email", Snackbar.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
                        }
                        Status.LOADING -> {
                            dialog.startDialog()
                        }
                        Status.ERROR -> {
                            dialog.dismissDialog()
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_register -> {
                val email = binding.etfieldRegEmail.text.toString().trim()
                val password = binding.etfieldRegPassword.text.toString().trim()
                if(validation()){
                    registerNew(email, password)
                }
            }
            R.id.tv_toLogin ->
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun validation(): Boolean{
        var validasi = true
        val emailField = binding.etfieldRegEmail.text.toString()
        val emailReg = binding.etRegEmail
        val passwordReg = binding.etRegPassword
        val passField = binding.etfieldRegPassword.text.toString()
        val confirmField = binding.etfieldRegConfirmPassword.text.toString()
        val confirmReg = binding.etRegConfirmPassword
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
        if(passField.length < 6){
            validasi = false
            passwordReg.error = "Password minimal 6 karakter"
            passwordReg.errorIconDrawable = null
            return validasi
        }else{
            passwordReg.error = null
            passwordReg.isErrorEnabled = false
            passwordReg.helperText = null
            passwordReg.isHelperTextEnabled = false
        }
        if(confirmField.isEmpty()){
            validasi = false
            confirmReg.error = "Password jangan kosong"
            confirmReg.errorIconDrawable = null
            return validasi
        }else if(confirmField!=passField){
            validasi = false
            confirmReg.error = "Password tidak sama"
            confirmReg.errorIconDrawable = null
            return validasi
        }else{
            confirmReg.error = null
            confirmReg.isErrorEnabled = false
        }
        return validasi
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}