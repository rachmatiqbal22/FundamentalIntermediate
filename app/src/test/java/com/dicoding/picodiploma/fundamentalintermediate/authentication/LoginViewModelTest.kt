package com.dicoding.picodiploma.fundamentalintermediate.authentication

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.dicoding.picodiploma.fundamentalintermediate.data.Result
import com.dicoding.picodiploma.fundamentalintermediate.data.UserRepository
import com.dicoding.picodiploma.fundamentalintermediate.data.UserModel
import com.dicoding.picodiploma.fundamentalintermediate.data.response.LoginResponse
import com.dicoding.picodiploma.fundamentalintermediate.data.response.LoginResult
import com.dicoding.picodiploma.fundamentalintermediate.view.login.LoginViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var loginViewModel: LoginViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        loginViewModel = LoginViewModel(userRepository)
    }

    @Test
    fun `when login is successful should return Success result`() {
        val email = "test@example.com"
        val password = "password123"
        val loginResult = LoginResult("user_id_123", "token123", "test@example.com")
        val loginResponse = LoginResponse(error = false, loginResult = loginResult, message = "Login successful")
        val result = Result.Success(loginResponse)
        val liveDataResult = MutableLiveData<Result<LoginResponse>>()
        liveDataResult.value = result

        Mockito.`when`(userRepository.login(email, password)).thenReturn(liveDataResult)

        val observer = Observer<Result<LoginResponse>> { actual ->
            Assert.assertTrue(actual is Result.Success)
            val successData = actual as Result.Success
            Assert.assertEquals(loginResponse, successData.data)
            Assert.assertEquals("token123", successData.data.loginResult.token)
        }

        loginViewModel.login(email, password).observeForever(observer)
    }

    @Test
    fun `when login fails should return Error result`() {
        val email = "wrong@example.com"
        val password = "wrongpassword"
        val errorMessage = "Invalid login credentials"
        val errorResult = Result.Error(errorMessage)

        val liveDataResult = MutableLiveData<Result<LoginResponse>>()
        liveDataResult.value = errorResult

        Mockito.`when`(userRepository.login(email, password)).thenReturn(liveDataResult)

        val observer = Observer<Result<LoginResponse>> { actual ->
            Assert.assertTrue(actual is Result.Error)
            val errorData = actual as Result.Error
            Assert.assertEquals(errorMessage, errorData.error)
        }

        loginViewModel.login(email, password).observeForever(observer)
    }

    @Test
    fun `saveSession should call saveSession method in UserRepository`() {
        val fakeUser = UserModel(
            email = "test@example.com",
            token = "token123",
            isLogin = true
        )

        loginViewModel.saveSession(fakeUser)

        Mockito.verify(userRepository).saveSession(fakeUser)
    }

    @Test
    fun `when login is loading should return Loading result`() {
        val loadingResult = Result.Loading

        val liveDataResult = MutableLiveData<Result<LoginResponse>>()
        liveDataResult.value = loadingResult

        Mockito.`when`(userRepository.login("test@example.com", "password123")).thenReturn(liveDataResult)

        val observer = Observer<Result<LoginResponse>> { actual ->
            Assert.assertTrue(actual is Result.Loading)
        }

        loginViewModel.login("test@example.com", "password123").observeForever(observer)
    }
}
