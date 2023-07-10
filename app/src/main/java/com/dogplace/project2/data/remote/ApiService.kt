package com.dogplace.project2.data.remote

import com.dogplace.project2.common.Constants.PATH_LOGIN
import com.dogplace.project2.common.Constants.PATH_SIGNUP
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST(PATH_LOGIN)
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST(PATH_SIGNUP)
    suspend fun signUp(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("email") email: String
    ): Response<ResponseBody>
}

