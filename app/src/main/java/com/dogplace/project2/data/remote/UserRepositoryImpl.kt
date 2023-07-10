package com.dogplace.project2.data.remote

import com.dogplace.project2.data.UserRepository
import okhttp3.ResponseBody
import retrofit2.Response

class UserRepositoryImpl(private val apiService: ApiService) : UserRepository {

    override suspend fun login(username: String, password: String): Response<ResponseBody> {
        return apiService.login(username, password)
    }

    override suspend fun signUp(
        username: String,
        password: String,
        email: String
    ): Response<ResponseBody> {
        return apiService.signUp(username, password, email)
    }
}
