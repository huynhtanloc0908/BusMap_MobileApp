package com.example.ui_login.models

data class User(
    val id: Int,
    val full_name: String,
    val email: String,
    val phone: String,
    val avatar: String?,       // có thể null khi login thường
    val google_id: String?     // có thể null khi không login Google
)
