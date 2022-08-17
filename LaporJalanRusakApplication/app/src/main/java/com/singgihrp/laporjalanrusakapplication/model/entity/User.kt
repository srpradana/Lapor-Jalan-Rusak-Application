package com.singgihrp.laporjalanrusakapplication.model.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val userid: String? = "",
    val email: String? = "",
    val name: String? = "",
    val date: String? = "",
    val nohp: String? = "",
    val role: String? = "",
    val kelamin: String? = "",
    var lengkap_profil: Boolean? = false,
    var verified: Boolean? = false
):Parcelable
