package com.singgihrp.adminljr.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.singgihrp.adminljr.model.AppRepositoryImplement
import com.singgihrp.adminljr.util.Event
import com.singgihrp.adminljr.util.Resource
import kotlinx.coroutines.Dispatchers

class ListReportViewModel: ViewModel() {
    private val appRepositoryImplement: AppRepositoryImplement = AppRepositoryImplement()

    fun getAllReport() = liveData(Dispatchers.IO){
        emit(Event(Resource.loading(null)))
        try{
            emit(Event(Resource.success(appRepositoryImplement.getAllReport())))
        }catch (e: Exception){
            emit(Event(Resource.error(data = null, message = e.message ?: "Error Occured")))
        }
    }

    fun getAccReport() = liveData(Dispatchers.IO){
        emit(Event(Resource.loading(null)))
        try{
            emit(Event(Resource.success(appRepositoryImplement.getAccReport())))
        }catch (e: Exception){
            emit(Event(Resource.error(data = null, message = e.message ?: "Error Occured")))
        }
    }
}