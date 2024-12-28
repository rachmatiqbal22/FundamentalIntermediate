package com.dicoding.picodiploma.fundamentalintermediate.view

import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.fundamentalintermediate.data.UserRepository

class MapViewModel(private val storyRepository: UserRepository): ViewModel() {
    fun getStoriesWithLocation() = storyRepository.getStoriesWithLocation()
}