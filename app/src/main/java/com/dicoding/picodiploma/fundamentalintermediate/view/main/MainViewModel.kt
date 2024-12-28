package com.dicoding.picodiploma.fundamentalintermediate.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.picodiploma.fundamentalintermediate.data.UserRepository
import com.dicoding.picodiploma.fundamentalintermediate.data.UserModel
import com.dicoding.picodiploma.fundamentalintermediate.data.UserPreference
import com.dicoding.picodiploma.fundamentalintermediate.data.response.StoryItem
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: UserRepository,
    private val userPreference: UserPreference
) : ViewModel() {

    private val _logoutStatus = MutableLiveData<Boolean>()
    val logoutStatus: LiveData<Boolean> = _logoutStatus

    val session: LiveData<UserModel> = userPreference.getSession().asLiveData()

    val stories: LiveData<PagingData<StoryItem>> = userPreference.getSession()
        .map { it.token }
        .asLiveData()
        .switchMap { token ->
            repository.getStoriesPagingData(token)
                .cachedIn(viewModelScope)
                .onStart {
                }
                .catch {
                    emit(PagingData.empty())
                }
                .asLiveData()
        }

    fun logout() {
        viewModelScope.launch {
            try {
                repository.logout()
                userPreference.logout()
                _logoutStatus.postValue(true)
            } catch (e: Exception) {
                _logoutStatus.postValue(false)
                e.printStackTrace()
            }
        }
    }
}
