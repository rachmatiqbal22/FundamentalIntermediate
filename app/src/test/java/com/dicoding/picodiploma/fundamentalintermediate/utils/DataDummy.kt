package com.dicoding.picodiploma.fundamentalintermediate.utils

import com.dicoding.picodiploma.fundamentalintermediate.data.response.LoginResponse
import com.dicoding.picodiploma.fundamentalintermediate.data.response.LoginResult
import com.dicoding.picodiploma.fundamentalintermediate.data.response.StoriesResponse
import com.dicoding.picodiploma.fundamentalintermediate.data.response.StoryItem

object SampleDataGenerator {
    fun createSampleStories(): StoriesResponse {
        val storiesList = mutableListOf<StoryItem>()
        for (index in 1..15) {
            val story = StoryItem(
                createdAt = "2023-08-15T10:30:00Z",
                description = "This is story description $index",
                id = "story_$index",
                lat = index.toDouble() * 5,
                lon = index.toDouble() * 6,
                name = "Story Author $index",
                photoUrl = "https://example.com/images/story_$index.jpg"
            )
            storiesList.add(story)
        }

        return StoriesResponse(
            error = false,
            message = "Sample stories retrieved successfully",
            listStory = storiesList
        )
    }

    fun createSampleLoginResponse(): LoginResponse {
        return LoginResponse(
            error = false,
            message = "Login successful",
            loginResult = LoginResult(
                userId = "user-abc123_xyz456",
                name = "John Doe",
                token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLWFiYzEyM195eXo0NTYiLCJpYXQiOjE2NTg5ODU2MDB9.7c0VUz5X9Xm_mScUK59l3mG02CEqRkbzL9nGH98oi3k"
            )
        )
    }
}
