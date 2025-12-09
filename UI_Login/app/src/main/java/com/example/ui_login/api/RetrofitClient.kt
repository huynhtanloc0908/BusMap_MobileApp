package com.example.ui_login.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // LƯU Ý VỀ URL:
    // - http://10.0.2.2/busmap_api/ : Chỉ dùng cho Android Emulator
    // - Nếu test trên thiết bị thật, đổi thành IP máy tính chạy XAMPP
    //   Ví dụ: http://192.168.1.100/busmap_api/
    // - Để lấy IP máy tính: Mở CMD/PowerShell, gõ: ipconfig
    //   Tìm "IPv4 Address" trong phần WiFi hoặc Ethernet
    private const val BASE_URL = "http://10.0.2.2:8080/busmap_api/"

    // OkHttpClient với timeout dài hơn
    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)  // Timeout kết nối
            .readTimeout(30, TimeUnit.SECONDS)     // Timeout đọc dữ liệu
            .writeTimeout(30, TimeUnit.SECONDS)    // Timeout ghi dữ liệu
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)  // Thêm OkHttpClient
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
