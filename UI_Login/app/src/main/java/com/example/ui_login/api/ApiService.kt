package com.example.ui_login.api

import com.example.ui_login.models.ApiResponse
import retrofit2.http.*
import retrofit2.Call

interface ApiService {

    @FormUrlEncoded
    @POST("register.php")
    fun register(
        @Field("full_name") fullName: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("password") password: String
    ): Call<ApiResponse>

    @FormUrlEncoded
    @POST("login.php")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<ApiResponse>

    // ⭐ GOOGLE LOGIN – chỉ gửi idToken
    @FormUrlEncoded
    @POST("google_login.php")
    fun googleLogin(
        @Field("id_token") idToken: String
    ): Call<ApiResponse>

    @FormUrlEncoded
    @POST("forgot_password.php")
    fun forgotPassword(
        @Field("email") email: String,
        @Field("new_password") newPassword: String
    ): Call<ApiResponse>

}
