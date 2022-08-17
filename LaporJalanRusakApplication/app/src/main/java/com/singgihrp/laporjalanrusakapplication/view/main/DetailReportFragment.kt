package com.singgihrp.laporjalanrusakapplication.view.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.singgihrp.laporjalanrusakapplication.R
import com.singgihrp.laporjalanrusakapplication.databinding.FragmentDetailReportBinding
import com.singgihrp.laporjalanrusakapplication.model.entity.Report


class DetailReportFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentDetailReportBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDetailReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments?.getParcelable<Report>("report")
        binding.tvDate.text = "Dikirim pada: ${bundle?.date}"
        binding.tvNama.text = bundle?.nama
        binding.tvEmail.text = bundle?.email
        binding.tvPhone.text = bundle?.nohp
        binding.tvLokasi.text = bundle?.lokasi
        Glide.with(requireActivity())
            .load(bundle?.images)
            .into(binding.imgFoto)
        if(bundle?.status == "Terkirim"){
            binding.tvStatus.text = "Laporan berhasil terikirm "
        }else if(bundle?.status == "terima"){
            binding.tvStatus.text = "Laporan diterima (direspon pada ${bundle.response_time})"
        }else{
            binding.tvStatus.text = "Laporan ditolak dengan alasan '${bundle?.catatan}' (direspon pada ${bundle?.response_time})."
        }
        if(bundle?.rusak == "Ringan"){
            binding.imgTingkatrusak.setImageResource(R.drawable.ic_check_rusak_ringan)
            binding.tvTingkatRusak.text = bundle.rusak
        }else{
            binding.imgTingkatrusak.setImageResource(R.drawable.ic_check_rusak_parah)
            binding.tvTingkatRusak.text = bundle?.rusak
        }

        binding.imgArrowback.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.img_arrowback -> {
                findNavController().popBackStack()
            }
        }
    }

}