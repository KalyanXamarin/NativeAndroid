package com.example.mynotes.repository

import android.net.NetworkRequest
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mynotes.repository.api.UserAPI
import com.example.mynotes.models.UserRequest
import com.example.mynotes.models.UserResponse
import com.example.mynotes.utils.NetworkResult
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class UserRepository @Inject constructor(private val userAPI: UserAPI) {

    private val _userResponseLiveData = MutableLiveData<NetworkResult<UserResponse>>()
    val userResponseLiveData: LiveData<NetworkResult<UserResponse>>
         get() = _userResponseLiveData

    suspend fun registerUser(userRequest: UserRequest){
        _userResponseLiveData.postValue(NetworkResult.Loading())
        val response = userAPI.signup(userRequest)
        handleReponse(response)
    }

    suspend fun loginUser(userRequest: UserRequest){
        _userResponseLiveData.postValue(NetworkResult.Loading())
        val response = userAPI.signin(userRequest);
        handleReponse(response)
    }

    private fun handleReponse(response: Response<UserResponse>){
        try {
            if (response.isSuccessful && response.body() != null) {
                _userResponseLiveData.postValue(NetworkResult.Success(response.body()!!))
            } else if (response.errorBody() != null) {
                val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
                _userResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
            } else {
                _userResponseLiveData.postValue(NetworkResult.Error("Something went wrong"))
            }
        }catch (ex: Exception){
            _userResponseLiveData.postValue(NetworkResult.Error("Exception Thrown ${ex.message}"))
        }
    }
}