package com.singgihrp.adminljr.model.entity

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Report(
    val uid: String? = "",
    val nama: String? = "",
    val email: String? = "",
    val nohp: String? = "",
    val rusak: String? = "",
    val lokasi: String? = "",
    val images: String? = "",
    val city: String? = "",
    val date: String? = "",
    val time: String? = "",
    var status: String? = "",
    val report_id: String? = "",
    val response_time: String? = "",
    val catatan: String? = "",
    @ServerTimestamp
    var timestamp: Timestamp? = null
): Parcelable
