package com.singgihrp.adminljr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.singgihrp.adminljr.model.AppRepositoryImplement
import com.singgihrp.adminljr.util.Event
import com.singgihrp.adminljr.util.Resource
import kotlinx.coroutines.Dispatchers

class DetailViewModel: ViewModel() {

    private val appRepositoryImplement = AppRepositoryImplement()

    fun updateStatusTerikirim(docid: String, date: String) = liveData(Dispatchers.IO) {
        emit(Event(Resource.loading(null)))
        try {
            emit(Event(Resource.success(appRepositoryImplement.updateStatusTerimaReport(docid, date))))
        } catch (e: Exception) {
            emit(Event(Resource.error(data = null, message = e.message ?: "Error Occured")))
        }
    }

    fun updateStatusTertolak(docid: String, date: String, catatan: String) = liveData(Dispatchers.IO) {
        emit(Event(Resource.loading(null)))
        try {
            emit(Event(Resource.success(appRepositoryImplement.updateStatusTolakReport(docid, date, catatan))))
        } catch (e: Exception) {
            emit(Event(Resource.error(data = null, message = e.message ?: "Error Occured")))
        }
    }
}