package com.singgihrp.adminljr.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.singgihrp.adminljr.databinding.ItemReportBinding
import com.singgihrp.adminljr.model.entity.Report

class ReportAdapter(private val listReport: List<Report>): RecyclerView.Adapter<ReportAdapter.ViewHolder>() {

    private lateinit var mListener: OnItemsClickListener

    inner class ViewHolder(private val binding: ItemReportBinding, listener: OnItemsClickListener) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(report: Report) = with(binding){
            Glide.with(binding.root)
                .load(report.images)
                .into(binding.imgGambar)
            binding.tvCity.text = report.city
            binding.tvDate.text = "Dikirim pada: ${report.date}"
            binding.tvLokasi.text = report.lokasi
            binding.tvTingkatRusak.text = "Tingkat kerusakan ${report.rusak}"
        }

        init {
            binding.root.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemReportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val report = listReport[position]
        holder.bind(report)
    }

    override fun getItemCount(): Int = listReport.size

    interface OnItemsClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemsClickListener){
        mListener = listener
    }

}