package com.singgihrp.laporjalanrusakapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.singgihrp.laporjalanrusakapplication.model.AppRepositoryImplemen
import com.singgihrp.laporjalanrusakapplication.util.Event
import com.singgihrp.laporjalanrusakapplication.util.Resource
import kotlinx.coroutines.Dispatchers

class RegisterViewModel : ViewModel() {

    private val appRepositoryImplemen = AppRepositoryImplemen()

    fun registerNew(email: String, password: String) = liveData(Dispatchers.IO){
        emit(Event(Resource.loading(null)))
        try{
            emit(Event(Resource.success(appRepositoryImplemen.register(email, password))))
        }catch (e: Exception){
            emit(Event(Resource.error(data = null, message = e.message ?: "Error Occured")))
        }
    }
}