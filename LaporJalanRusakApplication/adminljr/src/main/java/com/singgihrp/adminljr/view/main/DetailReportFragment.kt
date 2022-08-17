package com.singgihrp.adminljr.view.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.singgihrp.adminljr.R
import com.singgihrp.adminljr.databinding.FragmentDetailReportBinding
import com.singgihrp.adminljr.model.entity.Report
import com.singgihrp.adminljr.util.Status
import com.singgihrp.adminljr.view.support.Dialog
import com.singgihrp.adminljr.viewmodel.DetailViewModel
import java.text.SimpleDateFormat
import java.util.*

class DetailReportFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentDetailReportBinding? = null
    private val binding get() = _binding!!
    private lateinit var detailViewModel: DetailViewModel

    private lateinit var dialog: Dialog
    private var isFabOpen: Boolean = false

    private var bundle: Report? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDetailReportBinding.inflate(inflater, container, false)
        dialog = Dialog(requireActivity())
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        detailViewModel = ViewModelProvider(this)[DetailViewModel::class.java]
        bundle = arguments?.getParcelable("report")
        binding.tvDate.text = "Dikirim pada: ${bundle?.date}"
        binding.tvNama.text = bundle?.nama
        binding.tvEmail.text = bundle?.email
        binding.tvPhone.text = bundle?.nohp
        binding.tvLokasi.text = bundle?.lokasi
        Glide.with(requireActivity())
            .load(bundle?.images)
            .into(binding.imgFoto)
        if(bundle?.status == "Terkirim"){
            binding.tvStatus.text = "Laporan diterima"
        }else{
            binding.tvStatus.text = "Laporan diproses (Diterima pada ${bundle?.response_time})"
            binding.fabAction.hide()
            binding.fabAccept.hide()
            binding.fabReject.hide()
        }
        if(bundle?.rusak == "Ringan"){
            binding.imgTingkatrusak.setImageResource(R.drawable.ic_check_rusak_ringan)
            binding.tvTingkatRusak.text = bundle?.rusak
        }else{
            binding.imgTingkatrusak.setImageResource(R.drawable.ic_check_rusak_parah)
            binding.tvTingkatRusak.text = bundle?.rusak
        }

        binding.imgArrowback.setOnClickListener(this)
        binding.fabAction.setOnClickListener(this)
        binding.fabAccept.setOnClickListener(this)
        binding.fabReject.setOnClickListener(this)
    }

    private fun tolakLaporan(docid: String, date: String, catatan: String){
        detailViewModel.updateStatusTertolak(docid, date, catatan).observe(viewLifecycleOwner){event->
            event.getContentIfNotHandled().let {
                if(it!=null){
                    when(it.status) {
                        Status.SUCCESS -> {
                            dialog.dismissDialog()
                            Toast.makeText(requireContext(), "Laporan ditolak", Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
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

    private fun terimaLaporan(docid: String, date: String){
        detailViewModel.updateStatusTerikirim(docid, date).observe(viewLifecycleOwner){event->
            event.getContentIfNotHandled().let {
                if(it!=null){
                    when(it.status) {
                        Status.SUCCESS -> {
                            dialog.dismissDialog()
                            Toast.makeText(requireContext(), "Laporan diproses", Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
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

    private fun getDateString(seconds: Long): String {
        return try {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = seconds * 1000
            val date = calendar.time
            dateFormat.format(date)
        } catch (e: Exception) {
            Log.e("utils", "Date format", e)
            ""
        }
    }

    private fun showFabMenu(){
        isFabOpen = true
        binding.fabAccept.animate().translationY(-resources.getDimension(R.dimen.standard_55))
        binding.fabReject.animate().translationY(-resources.getDimension(R.dimen.standard_105))
    }

    private fun closeFabMenu(){
        isFabOpen = false
        binding.fabAccept.animate().translationY(0F)
        binding.fabReject.animate().translationY(0F)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.img_arrowback -> {
                findNavController().popBackStack()
            }
            R.id.fab_accept -> {
                AlertDialog.Builder(requireActivity())
                    .setTitle("Proses Laporan")
                    .setMessage("Yakin ingin memproses laporan ini?")
                    .setPositiveButton("Ya") { dialog, _ ->
                        val dateResponse = getDateString(Timestamp.now().seconds)
                        terimaLaporan(bundle?.report_id.toString(), dateResponse)
                        dialog.dismiss()
                    }
                    .setNegativeButton("Tidak") { dialog, _ ->
                        dialog.cancel()
                    }
                    .create()
                    .show()
            }
            R.id.fab_reject -> {
                val catatan = EditText(requireContext())
                AlertDialog.Builder(requireActivity())
                    .setTitle("Tolak Laporan")
                    .setMessage("Alasan anda menolak")
                    .setView(catatan)
                    .setPositiveButton("Tolak") { dialog, _ ->
                        if (catatan.text.isNotEmpty()) {
                            val dateResponse = getDateString(Timestamp.now().seconds)
                            tolakLaporan(bundle?.report_id.toString(), dateResponse, catatan.text.toString().trim())
                            dialog.dismiss()
                        }
                    }
                    .setNegativeButton("Tidak") { dialog, _ ->
                        dialog.cancel()
                    }
                    .create()
                    .show()
            }
            R.id.fab_action -> {
                if(!isFabOpen){
                    showFabMenu()
                }else{
                    closeFabMenu()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}