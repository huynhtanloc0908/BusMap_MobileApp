package com.example.ui_login

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ui_login.api.RetrofitClient
import com.example.ui_login.models.ApiResponse
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var etEmail: TextInputEditText
    private lateinit var etNewPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var btnResetPassword: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        // Ánh xạ View
        etEmail = findViewById(R.id.etEmail)
        etNewPassword = findViewById(R.id.etNewPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnResetPassword = findViewById(R.id.btnResetPassword)

        // Nút quay lại
        findViewById<ImageButton>(R.id.btnBack)?.setOnClickListener {
            finish()
        }

        btnResetPassword.setOnClickListener {
            resetPassword()
        }
    }

    private fun resetPassword() {
        val email = etEmail.text.toString().trim()
        val newPass = etNewPassword.text.toString().trim()
        val confirmPass = etConfirmPassword.text.toString().trim()

        // Validate
        if (email.isEmpty()) {
            etEmail.error = "Vui lòng nhập email"
            etEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Email không hợp lệ"
            etEmail.requestFocus()
            return
        }

        if (newPass.isEmpty()) {
            etNewPassword.error = "Vui lòng nhập mật khẩu mới"
            etNewPassword.requestFocus()
            return
        }

        if (newPass.length < 6) {
            etNewPassword.error = "Mật khẩu phải ít nhất 6 ký tự"
            etNewPassword.requestFocus()
            return
        }

        if (confirmPass != newPass) {
            etConfirmPassword.error = "Mật khẩu xác nhận không khớp"
            etConfirmPassword.requestFocus()
            return
        }

        // Gọi API
        RetrofitClient.api.forgotPassword(email, newPass)
            .enqueue(object : Callback<ApiResponse> {

                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    val apiResponse = response.body()

                    when (apiResponse?.status) {

                        "success" -> {
                            Toast.makeText(
                                this@ForgotPasswordActivity,
                                "Đặt lại mật khẩu thành công!",
                                Toast.LENGTH_LONG
                            ).show()
                            finish()  // Quay lại Login
                        }

                        "not_exist" -> {
                            Toast.makeText(
                                this@ForgotPasswordActivity,
                                "Email không tồn tại!",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        else -> {
                            Toast.makeText(
                                this@ForgotPasswordActivity,
                                "Lỗi: Không thể đặt lại mật khẩu",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        "Lỗi kết nối: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}
