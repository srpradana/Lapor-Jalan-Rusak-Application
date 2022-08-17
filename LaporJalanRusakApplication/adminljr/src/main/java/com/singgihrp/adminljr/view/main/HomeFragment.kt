package com.singgihrp.adminljr.view.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.singgihrp.adminljr.R
import com.singgihrp.adminljr.databinding.FragmentHomeBinding
import com.singgihrp.adminljr.util.Status
import com.singgihrp.adminljr.view.support.Dialog
import com.singgihrp.adminljr.viewmodel.HomeViewModel


class HomeFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel

    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this){
            val a = Intent(Intent.ACTION_MAIN)
            a.addCategory(Intent.CATEGORY_HOME)
            a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(a)
        }
        callback.isEnabled = true

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        dialog = Dialog(requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        getCountData()
        getAllCountData()
        getAccCountData()

        binding.imgLogout.setOnClickListener(this)
        binding.cvAllReport.setOnClickListener(this)
        binding.cvAccReport.setOnClickListener(this)
    }

    private fun getAllCountData(){
        homeViewModel.countAllReport()
        homeViewModel.countAllReport.observe(viewLifecycleOwner){
            binding.tvLaporantotal.text = it.toString()
        }
    }

    private fun getAccCountData(){
        homeViewModel.countAccReport()
        homeViewModel.countAccReport.observe(viewLifecycleOwner){
            binding.tvLaporantotalDiterima.text = it.toString()
        }
    }

    private fun logoutUser(){
        homeViewModel.getLogoutUser().observe(viewLifecycleOwner){event->
            event.getContentIfNotHandled().let {
                if(it!=null){
                    when(it.status) {
                        Status.SUCCESS -> {
                            dialog.dismissDialog()
                            val sharedPreferences = context?.getSharedPreferences("AppAuth", Context.MODE_PRIVATE)
                            val editor = sharedPreferences?.edit()
                            editor?.clear()
                            editor?.apply()
                            Toast.makeText(requireActivity(), "Berhasil Keluar", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
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

    private fun getCountData(){
        homeViewModel.getUserDataFromDb().observe(viewLifecycleOwner){event->
            event.peekContent().let {
                when(it.status) {
                    Status.SUCCESS -> {
                        dialog.dismissDialog()
                        binding.tvName.text = it.data?.nama
                    }
                    Status.ERROR -> {
                        dialog.dismissDialog()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    Status.LOADING -> dialog.startDialog()
                }
            }
        }
    }


    override fun onClick(v: View?) {
        when(v?.id){
            R.id.img_logout -> {
                AlertDialog.Builder(requireActivity())
                    .setTitle("Keluar")
                    .setMessage("Apakah anda yakin ingin keluar?")
                    .setIcon(R.drawable.ic_baseline_logout_24)
                    .setPositiveButton("Ya") { dialog, _ ->
                        logoutUser()
                        dialog.dismiss()
                    }
                    .setNegativeButton("Tidak") { dialog, _ ->
                        dialog.cancel()
                    }
                    .create()
                    .show()
            }
            R.id.cv_allReport -> {
                findNavController().navigate(R.id.action_homeFragment_to_listAllReportFragment)
            }
            R.id.cv_accReport -> {
                findNavController().navigate(R.id.action_homeFragment_to_listReportAccFragment)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}