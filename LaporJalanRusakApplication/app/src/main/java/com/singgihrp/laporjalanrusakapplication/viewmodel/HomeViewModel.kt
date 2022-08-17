package com.singgihrp.laporjalanrusakapplication.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.singgihrp.laporjalanrusakapplication.model.AppRepositoryImplemen
import com.singgihrp.laporjalanrusakapplication.model.entity.User
import com.singgihrp.laporjalanrusakapplication.util.Event
import com.singgihrp.laporjalanrusakapplication.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel: ViewModel() {
    private val appRepositoryImplemen = AppRepositoryImplemen()
    val countData = MutableLiveData<String>()
    val countAccData = MutableLiveData<String>()
    val countRejData = MutableLiveData<String>()

    fun logout() = liveData(Dispatchers.IO){
        emit(Event(Resource.loading(null)))
        try{
            emit(Event(Resource.success(appRepositoryImplemen.logout())))
        }catch (e: Exception){
            emit(Event(Resource.error(data = null, message = e.message ?: "Error Occured")))
        }
    }

    fun getCurrentReports() = liveData(Dispatchers.IO){
        emit(Event(Resource.loading(null)))
        try{
            emit(Event(Resource.success(appRepositoryImplemen.reportCurrentFirestore())))
        }catch (e: Exception){
            emit(Event(Resource.error(data = null, message = e.message ?: "Error Occured")))
        }
    }

    fun getAccReport() = liveData(Dispatchers.IO){
        emit(Event(Resource.loading(null)))
        try{
            emit(Event(Resource.success(appRepositoryImplemen.reportAccFirestore())))
        }catch (e: Exception){
            emit(Event(Resource.error(data = null, message = e.message ?: "Error Occured")))
        }
    }

    fun getRejReport() = liveData(Dispatchers.IO){
        emit(Event(Resource.loading(null)))
        try{
            emit(Event(Resource.success(appRepositoryImplemen.reportRejFirestore())))
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

    fun subscribeSnapshot(){
        viewModelScope.launch(Dispatchers.IO){
            appRepositoryImplemen.subscribeRealtimeUpdate()
        }
    }

    fun updateUserData(user: User) = liveData(Dispatchers.IO){
        emit(Event(Resource.loading(null)))
        try{
            emit(Event(Resource.success(appRepositoryImplemen.updateProfile(user))))
        }catch (e: Exception){
            emit(Event(Resource.error(data = null, message = e.message ?: "Error Occured")))
        }
    }

    fun getDataCountReport(){
        viewModelScope.launch(Dispatchers.IO) {
            val response = appRepositoryImplemen.countReportTotal()
            withContext(Dispatchers.Main){
                countData.postValue(response)
            }
        }
    }

    fun getDataCountAccReport(){
        viewModelScope.launch(Dispatchers.IO) {
            val response = appRepositoryImplemen.countReportAcc()
            withContext(Dispatchers.Main){
                countAccData.postValue(response)
            }
        }
    }

    fun getDataCountRejReport(){
        viewModelScope.launch(Dispatchers.IO) {
            val response = appRepositoryImplemen.countReportRej()
            withContext(Dispatchers.Main){
                countRejData.postValue(response)
            }
        }
    }

    fun updateUserVerified() = liveData(Dispatchers.IO) {
        emit(Event(Resource.loading(null)))
        try{
            emit(Event(Resource.success(appRepositoryImplemen.updateVerified())))
        }catch (e: Exception){
            emit(Event(Resource.error(data = null, message = e.message ?: "Error Occured")))
        }
    }

    fun reloadDb(){
        viewModelScope.launch(Dispatchers.IO) {
            appRepositoryImplemen.reloadDb()
        }
    }

    fun sendEmailVerification() = liveData(Dispatchers.IO){
        emit(Event(Resource.loading(null)))
        try{
            emit(Event(Resource.success(appRepositoryImplemen.sendEmailVerification())))
        }catch (e: Exception){
            emit(Event(Resource.error(data = null, message = e.message ?: "Error Occured")))
        }
    }

}