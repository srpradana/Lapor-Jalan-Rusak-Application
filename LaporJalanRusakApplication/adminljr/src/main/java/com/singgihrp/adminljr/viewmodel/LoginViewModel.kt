package com.singgihrp.adminljr.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.singgihrp.adminljr.model.AppRepositoryImplement
import com.singgihrp.adminljr.model.entity.User
import com.singgihrp.adminljr.util.Event
import com.singgihrp.adminljr.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel: ViewModel() {
    private val appRepositoryImplement = AppRepositoryImplement()
    var userdata = MutableLiveData<Event<User>>()

    fun loginUser(email: String, password: String) = liveData(Dispatchers.IO){
        emit(Event(Resource.loading(null)))
        try{
            emit(Event(Resource.success(appRepositoryImplement.login(email, password))))
        }catch (e: Exception){
            emit(Event(Resource.error(data = null, message = e.message ?: "Error Occured")))
        }
    }

    fun getUserData(){
        viewModelScope.launch(Dispatchers.IO){
            val result = appRepositoryImplement.getDataUserFromDb()
            withContext(Dispatchers.Main){
                userdata.postValue(Event(result))
            }
        }
    }
}