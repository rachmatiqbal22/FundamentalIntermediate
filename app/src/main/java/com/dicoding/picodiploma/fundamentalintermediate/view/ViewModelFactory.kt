package com.dicoding.picodiploma.fundamentalintermediate.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.fundamentalintermediate.data.UserRepository
import com.dicoding.picodiploma.fundamentalintermediate.data.retrofit.ApiConfig
import com.dicoding.picodiploma.fundamentalintermediate.data.UserPreference
import com.dicoding.picodiploma.fundamentalintermediate.data.dataStore
import com.dicoding.picodiploma.fundamentalintermediate.view.login.LoginViewModel
import com.dicoding.picodiploma.fundamentalintermediate.view.main.CreateActivityViewModel
import com.dicoding.picodiploma.fundamentalintermediate.view.signup.SignupViewModel
import com.dicoding.picodiploma.fundamentalintermediate.view.main.MainViewModel

class ViewModelFactory(
    private val userRepository: UserRepository,
    private val userPreference: UserPreference
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(userRepository, userPreference) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(CreateActivityViewModel::class.java) -> {
                CreateActivityViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(MapViewModel::class.java) -> {
                MapViewModel(userRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    val userPreference = UserPreference.getInstance(context.dataStore)
                    val apiService = ApiConfig.getApiService(context)
                    val userRepository = UserRepository.getInstance(userPreference, apiService)
                    INSTANCE = ViewModelFactory(userRepository, userPreference)
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}
