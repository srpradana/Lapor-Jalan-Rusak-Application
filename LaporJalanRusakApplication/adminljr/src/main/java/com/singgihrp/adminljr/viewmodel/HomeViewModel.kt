package com.singgihrp.adminljr.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.singgihrp.adminljr.model.AppRepositoryImplement
import com.singgihrp.adminljr.util.Event
import com.singgihrp.adminljr.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel: ViewModel() {
    private val appRepositoryImplement = AppRepositoryImplement()
    var countAllReport = MutableLiveData<String>()
    var countAccReport = MutableLiveData<String>()

    fun getUserDataFromDb() = liveData(Dispatchers.IO){
        emit(Event(Resource.loading(null)))
        try{
            emit(Event(Resource.success(appRepositoryImplement.getDataUserFromDb())))
        }catch (e: Exception){
            emit(Event(Resource.error(data = null, message = e.message ?: "Error Occured")))
        }
    }

    fun getLogoutUser() = liveData(Dispatchers.IO){
        emit(Event(Resource.loading(null)))
        try{
            emit(Event(Resource.success(appRepositoryImplement.logout())))
        }catch (e: Exception){
            emit(Event(Resource.error(data = null, message = e.message ?: "Error Occured")))
        }
    }

    fun countAllReport(){
        viewModelScope.launch(Dispatchers.IO){
            val result = appRepositoryImplement.countAllReport()
            withContext(Dispatchers.Main){
                countAllReport.postValue(result)
            }
        }
    }

    fun countAccReport(){
        viewModelScope.launch(Dispatchers.IO){
            val result = appRepositoryImplement.countAccReport()
            withContext(Dispatchers.Main){
                countAccReport.postValue(result)
            }
        }
    }
}