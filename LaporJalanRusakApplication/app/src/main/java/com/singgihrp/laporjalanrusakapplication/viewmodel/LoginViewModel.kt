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

class LoginViewModel: ViewModel() {
    private val appRepositoryImplemen = AppRepositoryImplemen()
    var userData = MutableLiveData<Event<User>>()

    fun loginNew(email: String, password: String) = liveData(Dispatchers.IO){
        emit(Event(Resource.loading(null)))
        try{
            emit(Event(Resource.success(appRepositoryImplemen.login(email, password))))
        }catch (e: Exception){
            emit(Event(Resource.error(data = null, message = e.message ?: "Error Occured")))
        }
    }

    fun sendResetPassword(email: String) = liveData(Dispatchers.IO){
        emit(Event(Resource.loading(null)))
        try{
            emit(Event(Resource.success(appRepositoryImplemen.sendResetPassword(email))))
        }catch (e: Exception){
            emit(Event(Resource.error(data = null, message = e.message ?: "Error Occured")))
        }
    }

    fun getUserData(){
        viewModelScope.launch(Dispatchers.IO){
            val result = appRepositoryImplemen.userFromFirestore()
            withContext(Dispatchers.Main){
                userData.postValue(Event(result))
            }
        }
    }
}