package com.example.ui_login.models

data class ApiResponse(
    val status: String,
    val user: User? = null
)
