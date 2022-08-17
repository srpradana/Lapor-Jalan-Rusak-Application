package com.singgihrp.adminljr.model

import com.singgihrp.adminljr.model.entity.Report
import com.singgihrp.adminljr.model.entity.User

interface AppRepository {
    /*
    User Auth
     */
    suspend fun login(email: String, password: String)
    suspend fun checkRole(): Boolean
    suspend fun logout()

    /*
    Util
     */

    /*
    Report
     */
    suspend fun getAllReport(): ArrayList<Report>
    suspend fun getAccReport(): ArrayList<Report>
    suspend fun countAllReport(): String
    suspend fun countAccReport(): String
    suspend fun updateStatusTerimaReport(docid: String, date: String)
    suspend fun updateStatusTolakReport(docid: String, date: String, catatan: String)

    /*
    Get Data
     */
    suspend fun getDataUserFromDb(): User


}