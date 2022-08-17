package com.singgihrp.laporjalanrusakapplication.view.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.singgihrp.laporjalanrusakapplication.R
import com.singgihrp.laporjalanrusakapplication.databinding.FragmentListReportAccBinding
import com.singgihrp.laporjalanrusakapplication.util.Status
import com.singgihrp.laporjalanrusakapplication.view.adapter.CurrentReportAdapter
import com.singgihrp.laporjalanrusakapplication.view.support.Dialog
import com.singgihrp.laporjalanrusakapplication.viewmodel.HomeViewModel

class ListReportAccFragment : Fragment(), View.OnClickListener {

    private var _binding : FragmentListReportAccBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel

    private lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentListReportAccBinding.inflate(inflater, container, false)
        dialog = Dialog(requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        showItem()

        binding.imgArrowback.setOnClickListener(this)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showItem() {
        homeViewModel.getAccReport().observe(viewLifecycleOwner){event->
            event.peekContent().let {
                when(it.status) {
                    Status.SUCCESS -> {
                        binding.rvReport.layoutManager = LinearLayoutManager(context)
                        val adapter = CurrentReportAdapter(it.data!!)
                        binding.rvReport.adapter = adapter
                        dialog.dismissDialog()
                        adapter.notifyDataSetChanged()
                        adapter.setOnItemClickListener(object : CurrentReportAdapter.OnItemClickListener{
                            override fun onItemClick(position: Int) {
                                val bundle = Bundle()
                                bundle.putParcelable("report", it.data[position])
                                findNavController().navigate(R.id.action_listReportAccFragment_to_detailReportFragment, bundle)
                            }
                        })
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
            R.id.img_arrowback -> {
                findNavController().navigate(R.id.action_listReportAccFragment_to_homeFragment)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}