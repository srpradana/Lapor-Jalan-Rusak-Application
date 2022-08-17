package com.singgihrp.laporjalanrusakapplication.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.singgihrp.laporjalanrusakapplication.model.AppRepositoryImplemen
import com.singgihrp.laporjalanrusakapplication.model.entity.Report
import com.singgihrp.laporjalanrusakapplication.util.Event
import com.singgihrp.laporjalanrusakapplication.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FormViewModel: ViewModel() {

    private val appRepositoryImplemen = AppRepositoryImplemen()
    val availableLocation = MutableLiveData<Event<Boolean>>()
    var countFormData = MutableLiveData<Event<String>>()

    fun sendReport(report: Report, reportid: String) = liveData(Dispatchers.IO){
        emit(Event(Resource.loading(null)))
        try{
            emit(Event(Resource.success(appRepositoryImplemen.sendReport(report, reportid))))
        }catch (e: Exception){
            emit(Event(Resource.error(data = null, message = e.message ?: "Error Occured")))
        }
    }

    fun getImageUrl(byteArray: ByteArray) = liveData(Dispatchers.IO){
        emit(Event(Resource.loading(null)))
        try{
            emit(Event(Resource.success(appRepositoryImplemen.uploadImage(byteArray))))
        }catch (e: Exception){
            emit(Event(Resource.error(data = null, message = e.message ?: "Error Occured")))
        }
    }

    fun getUserData() = liveData(Dispatchers.IO){
        emit(Event(Resource.loading(null)))
        try{
            emit(Event(Resource.success(appRepositoryImplemen.userFromFirestore())))
        }catch (e: Exception){
            emit(Event(Resource.error(data = null, message = e.message ?: "Error Occured")))
        }
    }

    fun getLocationData(location: String){
        viewModelScope.launch(Dispatchers.IO) {
            val result = appRepositoryImplemen.availableLocation(location)
            withContext(Dispatchers.Main){
                availableLocation.postValue(Event(result))
            }
        }
    }

    fun getDataCountReport(){
        viewModelScope.launch(Dispatchers.IO) {
            val response = appRepositoryImplemen.countReportTotal()
            withContext(Dispatchers.Main){
                countFormData.postValue(Event(response))
            }
        }
    }
}