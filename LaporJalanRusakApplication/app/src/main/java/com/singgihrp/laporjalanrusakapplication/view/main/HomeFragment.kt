package com.singgihrp.laporjalanrusakapplication.view.main

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.singgihrp.laporjalanrusakapplication.R
import com.singgihrp.laporjalanrusakapplication.databinding.FragmentHomeBinding
import com.singgihrp.laporjalanrusakapplication.util.Status
import com.singgihrp.laporjalanrusakapplication.view.support.Dialog
import com.singgihrp.laporjalanrusakapplication.viewmodel.HomeViewModel


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
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        dialog = Dialog(requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        homeViewModel.reloadDb()
        homeViewModel.subscribeSnapshot()

        binding.tvCekProfil.visibility = View.INVISIBLE
        binding.tvCekEmailVerified.visibility = View.INVISIBLE

        dataFromFireStore()
        getDataCountReport()
        getDataCountAccReport()
        getDataCountRejReport()

        binding.cvListLaporan.setOnClickListener(this)
        binding.btnGoToEditProfil.setOnClickListener(this)
        binding.cvListLaporanDiterima.setOnClickListener(this)
        binding.cvListLaporanDitolak.setOnClickListener(this)
        binding.tvRefresh.setOnClickListener(this)

    }

    private fun getDataCountReport(){
        homeViewModel.getDataCountReport()
        homeViewModel.countData.observe(viewLifecycleOwner){
            binding.tvAngkaLaporan.text = it.toString()
        }
    }

    private fun getDataCountAccReport(){
        homeViewModel.getDataCountAccReport()
        homeViewModel.countAccData.observe(viewLifecycleOwner){
            if(it=="0"){
                binding.tvAngkaLaporanDiterima.text = "0"
            }else{
                binding.tvAngkaLaporanDiterima.text = it.toString()
            }
        }
    }

    private fun getDataCountRejReport(){
        homeViewModel.getDataCountRejReport()
        homeViewModel.countRejData.observe(viewLifecycleOwner){
            if(it=="0"){
                binding.tvAngkaLaporanDitolak.text = "0"
            }else{
                binding.tvAngkaLaporanDitolak.text = it.toString()
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Suppress("DEPRECATION")
    private fun dataFromFireStore(){
        homeViewModel.getUserData().observe(viewLifecycleOwner){event->
            event.peekContent().let {
                when(it.status){
                    Status.SUCCESS -> {
                        dialog.dismissDialog()
                        updateVerified()
                        binding.tvEmailDisplay.text = it.data?.email
                        if(it.data?.verified == true){
                            binding.tvCekEmailVerified.setTextColor(Color.GREEN)
                            val img = context?.resources?.getDrawable(R.drawable.ic_check)
                            binding.tvCekEmailVerified.setCompoundDrawables(img, null, null, null)
                            binding.tvCekEmailVerified.compoundDrawables.first().setTint(Color.GREEN)
                            binding.tvInfoRefresh.visibility = View.GONE
                            binding.tvRefresh.visibility = View.GONE
                        }else{
                            binding.tvInfoRefresh.visibility = View.VISIBLE
                            binding.tvRefresh.visibility = View.VISIBLE
                        }
                        if(it.data?.lengkap_profil == true){
                            binding.tvCekProfil.setTextColor(Color.GREEN)
                            val img = context?.resources?.getDrawable(R.drawable.ic_check)
                            binding.tvCekProfil.setCompoundDrawables(img, null, null, null)
                            binding.tvCekProfil.compoundDrawables.first().setTint(Color.GREEN)
                        }
                        checkedAvaliableButton(it.data?.lengkap_profil == true,
                            it.data?.verified == true
                        )
                        binding.layoutHome.visibility = View.VISIBLE
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

    private fun checkedAvaliableButton(profil: Boolean, verifikasi: Boolean){
        Log.d("tes", "checkedAvaliableButton: $profil $verifikasi")
        binding.tvCekProfil.visibility = View.VISIBLE
        binding.tvCekEmailVerified.visibility = View.VISIBLE
        if(profil && verifikasi){
            binding.cvSendLaporan.isClickable = true
            binding.cvSendLaporan.isEnabled = true
            binding.cvSendLaporan.setOnClickListener(this)
        }else{
            binding.cvSendLaporan.isClickable = false
            binding.cvSendLaporan.isEnabled = false
            binding.cvSendLaporan.setCardBackgroundColor(Color.GRAY)
        }
    }

    private fun updateVerified(){
        homeViewModel.updateUserVerified().observe(viewLifecycleOwner){event->
            event.getContentIfNotHandled().let {
                if(it!=null){
                    when(it.status) {
                        Status.SUCCESS -> {
                            dialog.dismissDialog()
                        }
                        Status.ERROR -> {
                            dialog.dismissDialog()
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }
                        Status.LOADING -> dialog.dismissDialog()
                    }
                }else{
                    Toast.makeText(requireContext(), it?.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.cv_sendLaporan -> {
                findNavController().navigate(R.id.action_homeFragment_to_formFragment)
            }
            R.id.cv_listLaporan -> {
                findNavController().navigate(R.id.action_homeFragment_to_listReportFragment)
            }
            R.id.btn_goToEditProfil -> {
                findNavController().navigate(R.id.action_homeFragment_to_editProfilFragment)
            }
            R.id.cv_listLaporanDiterima -> {
                findNavController().navigate(R.id.action_homeFragment_to_listReportAccFragment)
            }
            R.id.cv_listLaporanDitolak -> {
                findNavController().navigate(R.id.action_homeFragment_to_listRejFragment)
            }
            R.id.tv_refresh -> {
                findNavController().navigate(R.id.action_homeFragment_self)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}