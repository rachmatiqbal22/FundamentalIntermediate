package com.dicoding.picodiploma.fundamentalintermediate.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.fundamentalintermediate.data.Result
import com.dicoding.picodiploma.fundamentalintermediate.data.response.LoginResponse
import com.dicoding.picodiploma.fundamentalintermediate.data.UserModel
import com.dicoding.picodiploma.fundamentalintermediate.data.UserRepository

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun login(email: String, password: String): LiveData<Result<LoginResponse>> {
        return userRepository.login(email, password)
    }

    fun saveSession(user: UserModel) {
        userRepository.saveSession(user)
    }
}
