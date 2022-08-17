package com.singgihrp.laporjalanrusakapplication.view.main

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.singgihrp.laporjalanrusakapplication.R
import com.singgihrp.laporjalanrusakapplication.databinding.FragmentEditProfilBinding
import com.singgihrp.laporjalanrusakapplication.util.Status
import com.singgihrp.laporjalanrusakapplication.view.support.Dialog
import com.singgihrp.laporjalanrusakapplication.viewmodel.HomeViewModel

class EditProfilFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentEditProfilBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel

    private lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEditProfilBinding.inflate(inflater, container, false)
        dialog = Dialog(requireActivity())
        dialog.startDialog()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        dialog.dismissDialog()

        binding.linearOne.setOnClickListener(this)
        binding.linearTwo.setOnClickListener(this)
        binding.linearThree.setOnClickListener(this)
        binding.imgArrowback.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.linear_one -> {
                findNavController().navigate(R.id.action_editProfilFragment_to_editAccountFragment)
            }
            R.id.linear_two -> {
                findNavController().navigate(R.id.action_editProfilFragment_to_emailVerificationFragment)
            }
            R.id.linear_three -> {
                AlertDialog.Builder(requireActivity())
                    .setTitle("Keluar")
                    .setMessage("Apakah anda yakin ingin keluar?")
                    .setIcon(R.drawable.ic_baseline_logout_24)
                    .setPositiveButton("Ya") { dialog, _ ->
                        loggedOut()
                        dialog.dismiss()
                    }
                    .setNegativeButton("Tidak") { dialog, _ ->
                        dialog.cancel()
                    }
                    .create()
                    .show()
            }
            R.id.img_arrowback ->{
                findNavController().navigate(R.id.action_editProfilFragment_to_homeFragment)
            }
        }
    }

    private fun loggedOut() {
        homeViewModel.logout().observe(viewLifecycleOwner){event->
            event.getContentIfNotHandled().let {
                if(it!=null){
                    when(it.status){
                        Status.SUCCESS -> {
                            dialog.dismissDialog()
                            val sharedPreferences = context?.getSharedPreferences("AppAuth", Context.MODE_PRIVATE)
                            val editor = sharedPreferences?.edit()
                            editor?.clear()
                            editor?.apply()
                            findNavController().navigate(R.id.action_editProfilFragment_to_loginFragment)
                            Toast.makeText(requireContext(), "Berhasil Keluar", Toast.LENGTH_SHORT).show()
                        }
                        Status.ERROR -> {
                            dialog.dismissDialog()
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }
                        Status.LOADING -> {
                            dialog.startDialog()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}