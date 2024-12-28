package com.dicoding.picodiploma.fundamentalintermediate.view.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.fundamentalintermediate.data.UserRepository
import kotlinx.coroutines.launch

class SignupViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun postSignUp(name: String, email: String, password: String, callback: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = userRepository.apiService.postSignUp(name, email, password)
                callback(response.error, response.message)
            } catch (e: Exception) {
                callback(false, e.message ?: "Unknown error")
            }
        }
    }
}
