package com.singgihrp.adminljr.view.main

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
import com.singgihrp.adminljr.R
import com.singgihrp.adminljr.databinding.FragmentListAllReportBinding
import com.singgihrp.adminljr.util.Status
import com.singgihrp.adminljr.view.adapter.ReportAdapter
import com.singgihrp.adminljr.view.support.Dialog
import com.singgihrp.adminljr.viewmodel.ListReportViewModel

class ListAllReportFragment : Fragment() {

    private var _binding: FragmentListAllReportBinding? = null
    private val binding get() = _binding!!
    private lateinit var listReportViewModel: ListReportViewModel

    private lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentListAllReportBinding.inflate(inflater, container, false)
        dialog = Dialog(requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listReportViewModel = ViewModelProvider(this)[ListReportViewModel::class.java]
        showList()

        binding.imgArrowback.setOnClickListener{
            findNavController().navigate(R.id.action_listAllReportFragment_to_homeFragment)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showList(){
        listReportViewModel.getAllReport().observe(viewLifecycleOwner) { event ->
            event.peekContent().let {
                when (it.status) {
                    Status.SUCCESS -> {
                        dialog.dismissDialog()
                        binding.rvReport.layoutManager = LinearLayoutManager(context)
                        val adapter = ReportAdapter(it.data!!)
                        binding.rvReport.adapter = adapter
                        adapter.notifyDataSetChanged()
                        adapter.setOnItemClickListener(object : ReportAdapter.OnItemsClickListener {
                            override fun onItemClick(position: Int) {
                                val bundle = Bundle()
                                bundle.putParcelable("report", it.data[position])
                                findNavController().navigate(R.id.action_listAllReportFragment_to_detailReportFragment, bundle)
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}