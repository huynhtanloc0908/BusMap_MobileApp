package com.example.ui_login

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ui_login.api.RetrofitClient
import com.example.ui_login.models.ApiResponse
import com.example.ui_login.utils.SessionManager
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateAccountActivity : AppCompatActivity() {
    private lateinit var etFullName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var btnSubmit: android.widget.Button
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_account)
        
        val rootView = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.rootCreateAccountContainer)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnSubmit = findViewById(R.id.btnSubmit)
        sessionManager = SessionManager(this)

        // Back button functionality
        findViewById<android.widget.ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Submit button - Register
        btnSubmit.setOnClickListener {
            performRegister()
        }
    }

    private fun performRegister() {
        val fullName = etFullName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        // Validate inputs
        if (TextUtils.isEmpty(fullName)) {
            etFullName.error = "Vui lòng nhập họ tên"
            etFullName.requestFocus()
            return
        }

        if (fullName.length < 3) {
            etFullName.error = "Họ tên phải có ít nhất 3 ký tự"
            etFullName.requestFocus()
            return
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.error = "Vui lòng nhập email"
            etEmail.requestFocus()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Email không hợp lệ"
            etEmail.requestFocus()
            return
        }

        if (TextUtils.isEmpty(phone)) {
            etPhone.error = "Vui lòng nhập số điện thoại"
            etPhone.requestFocus()
            return
        }

        if (!android.util.Patterns.PHONE.matcher(phone).matches() || phone.length < 10) {
            etPhone.error = "Số điện thoại không hợp lệ"
            etPhone.requestFocus()
            return
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.error = "Vui lòng nhập mật khẩu"
            etPassword.requestFocus()
            return
        }

        if (password.length < 6) {
            etPassword.error = "Mật khẩu phải có ít nhất 6 ký tự"
            etPassword.requestFocus()
            return
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.error = "Vui lòng xác nhận mật khẩu"
            etConfirmPassword.requestFocus()
            return
        }

        if (password != confirmPassword) {
            etConfirmPassword.error = "Mật khẩu xác nhận không khớp"
            etConfirmPassword.requestFocus()
            return
        }

        // Disable button and show loading
        btnSubmit.isEnabled = false
        btnSubmit.text = "Đang đăng ký..."

        // Call API
        RetrofitClient.api.register(fullName, email, phone, password).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                btnSubmit.isEnabled = true
                btnSubmit.text = "Sign Up"

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null) {
                        if (apiResponse.status == "success" && apiResponse.user != null) {
                            // Save user session
                            sessionManager.saveUserSession(apiResponse.user!!)

                            // Show success message
                            Toast.makeText(this@CreateAccountActivity, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()

                            // Quay lại màn hình welcome (MainActivity)
                            finish()
                        } else {
                            // Handle error from API
                            val errorMessage = when {
                                apiResponse.status.contains("email", ignoreCase = true) -> "Email đã được sử dụng"
                                apiResponse.status.contains("phone", ignoreCase = true) -> "Số điện thoại đã được sử dụng"
                                else -> apiResponse.status.ifEmpty { "Đăng ký thất bại. Vui lòng thử lại!" }
                            }
                            Toast.makeText(this@CreateAccountActivity, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        android.util.Log.e("CreateAccount", "Response body is null. Code: ${response.code()}")
                        Toast.makeText(this@CreateAccountActivity, "Lỗi: Server không trả về dữ liệu (Code: ${response.code()})", Toast.LENGTH_LONG).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e("CreateAccount", "Response not successful. Code: ${response.code()}, Body: $errorBody")
                    Toast.makeText(this@CreateAccountActivity, "Lỗi server (Code: ${response.code()}). Kiểm tra XAMPP và file PHP!", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                btnSubmit.isEnabled = true
                btnSubmit.text = "Sign Up"
                android.util.Log.e("CreateAccount", "Network error: ${t.message}", t)
                
                val errorMessage = when {
                    t.message?.contains("Unable to resolve host", ignoreCase = true) == true -> 
                        "Không thể kết nối server. Kiểm tra:\n- XAMPP đã chạy chưa?\n- URL đúng chưa?\n- Đang dùng Emulator hay thiết bị thật?"
                    t.message?.contains("Connection refused", ignoreCase = true) == true -> 
                        "Server từ chối kết nối. Kiểm tra Apache trong XAMPP đã chạy chưa?"
                    t.message?.contains("timeout", ignoreCase = true) == true -> 
                        "Hết thời gian kết nối. Kiểm tra mạng và server."
                    else -> "Lỗi kết nối: ${t.message}\nKiểm tra XAMPP và URL API!"
                }
                Toast.makeText(this@CreateAccountActivity, errorMessage, Toast.LENGTH_LONG).show()
            }
        })
    }
}

