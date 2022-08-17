package com.singgihrp.laporjalanrusakapplication.view.main


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
import com.singgihrp.laporjalanrusakapplication.databinding.FragmentEmailVerificationBinding
import com.singgihrp.laporjalanrusakapplication.util.Status
import com.singgihrp.laporjalanrusakapplication.view.support.Dialog
import com.singgihrp.laporjalanrusakapplication.viewmodel.HomeViewModel

class EmailVerificationFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentEmailVerificationBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel

    private lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEmailVerificationBinding.inflate(inflater, container, false)
        dialog = Dialog(requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        getUserData()

        binding.btnSendVerif.setOnClickListener(this)
        binding.imgArrowback.setOnClickListener(this)

    }

    private fun sendEmailVerif(){
        homeViewModel.sendEmailVerification().observe(viewLifecycleOwner){event->
            event.getContentIfNotHandled().let {
                if(it!=null){
                    when(it.status) {
                        Status.SUCCESS -> {
                            dialog.dismissDialog()
                            Snackbar.make(binding.root, "Link Verifikasi sudah dikirim ke Email", Snackbar.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_emailVerificationFragment_to_homeFragment)
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

    private fun getUserData(){
        homeViewModel.getUserData().observe(viewLifecycleOwner){event->
            event.peekContent().let {
                when(it.status) {
                    Status.SUCCESS -> {
                        dialog.dismissDialog()
                        if(it.data?.verified == true){
                            finishedVerif()
                        }
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

    private fun finishedVerif(){
        binding.btnSendVerif.isClickable = false
        binding.btnSendVerif.isEnabled = false
        binding.tvTextAkunVerif.visibility = View.VISIBLE
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.img_arrowback -> {
                findNavController().navigate(R.id.action_emailVerificationFragment_to_editProfilFragment)
            }
            R.id.btn_sendVerif -> {
                sendEmailVerif()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}