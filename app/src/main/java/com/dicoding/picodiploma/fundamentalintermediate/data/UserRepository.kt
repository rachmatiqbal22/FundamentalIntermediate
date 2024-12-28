package com.dicoding.picodiploma.fundamentalintermediate.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dicoding.picodiploma.fundamentalintermediate.data.retrofit.ApiService
import com.dicoding.picodiploma.fundamentalintermediate.data.response.LoginResponse
import com.dicoding.picodiploma.fundamentalintermediate.data.response.PostStoryResponse
import com.dicoding.picodiploma.fundamentalintermediate.data.response.StoriesResponse
import com.dicoding.picodiploma.fundamentalintermediate.data.response.StoryItem
import com.dicoding.picodiploma.fundamentalintermediate.data.response.StoryPagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody


class UserRepository private constructor(
    private val userPreference: UserPreference,
    val apiService: ApiService
) {

    fun saveSession(user: UserModel): LiveData<Result<Unit>> = liveData {
        emit(Result.Loading)
        try {
            userPreference.saveSession(user)
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun logout(): LiveData<Result<Unit>> = liveData {
        emit(Result.Loading)
        try {
            userPreference.logout()
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun login(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = withContext(Dispatchers.IO) {
                apiService.postLogin(email, password)
            }

            if (response.isSuccessful) {
                val loginResponse = response.body()
                loginResponse?.let {
                    val userModel = UserModel(
                        email = email,
                        token = it.loginResult.token,
                        isLogin = true
                    )
                    userPreference.saveSession(userModel)
                    emit(Result.Success(loginResponse))
                } ?: emit(Result.Error("Login failed"))
            } else {
                emit(Result.Error("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("LoginViewModel", "postLogin: ${e.message}")
            emit(Result.Error(e.message ?: "Unknown error"))
        }
    }

    fun postStory(
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ): LiveData<Result<PostStoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val token = userPreference.getSession()
                .map { it.token }
                .first()
            val response = apiService.postStory("Bearer $token", file, description, lat, lon)
            emit(Result.Success(response))
        } catch (e: Exception) {
            Log.e("UserRepository", "postStory: ${e.message}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getStoriesWithLocation(): LiveData<Result<StoriesResponse>> = liveData {
        emit(Result.Loading)
        try {
            val token = userPreference.getSession()
                .map { it.token }
                .first()
            val response = apiService.getStoriesWithLocation("Bearer $token", 1)
            if (response.isSuccessful) {
                response.body()?.let { storiesResponse ->
                    emit(Result.Success(storiesResponse))
                } ?: emit(Result.Error("Response body is null"))
            } else {
                emit(Result.Error("Error: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("ListStoryViewModel", "getStoriesWithLocation: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }

    fun getStoriesPagingData(token: String): Flow<PagingData<StoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { StoryPagingSource(apiService, token) }
        ).flow
    }
}
