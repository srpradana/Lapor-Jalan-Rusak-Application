package com.singgihrp.laporjalanrusakapplication.model

import com.singgihrp.laporjalanrusakapplication.model.entity.Report
import com.singgihrp.laporjalanrusakapplication.model.entity.User


interface AppRepository {
    /*
    User Management
     */
    suspend fun login(email: String, password: String)
    suspend fun register(email: String, password: String)
    suspend fun logout()
    suspend fun updateProfile(user: User)
    suspend fun updateVerified()
    suspend fun reloadDb()
    suspend fun sendResetPassword(email: String)

    /*
    Util
     */
    suspend fun subscribeRealtimeUpdate()
    suspend fun sendEmailVerification()
    suspend fun checkRole(): Boolean

    /*
    Get Data From DB
     */
    suspend fun userFromFirestore(): User
    suspend fun reportCurrentFirestore(): ArrayList<Report>
    suspend fun reportAccFirestore(): ArrayList<Report>
    suspend fun reportRejFirestore(): ArrayList<Report>
    suspend fun countReportTotal(): String
    suspend fun countReportAcc(): String
    suspend fun countReportRej(): String
    suspend fun availableLocation(location: String): Boolean

    /*
    Report
     */
    suspend fun sendReport(report: Report, reportid: String)
    suspend fun uploadImage(byteArray: ByteArray): String

}