package com.dogplace.project2.data

import okhttp3.ResponseBody
import retrofit2.Response

interface UserRepository {
    suspend fun login(username: String, password: String): Response<ResponseBody>
    suspend fun signUp(username: String, password: String, email: String): Response<ResponseBody>
}
