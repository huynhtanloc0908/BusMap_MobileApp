package com.example.ui_login

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ui_login.api.RetrofitClient
import com.example.ui_login.models.ApiResponse
import com.example.ui_login.utils.SessionManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInActivity : AppCompatActivity() {
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var cbRememberPassword: android.widget.CheckBox
    private lateinit var btnSubmit: android.widget.Button
    private lateinit var sessionManager: SessionManager
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_in)

        val rootView =
            findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.rootSignIn)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        cbRememberPassword = findViewById(R.id.cbRememberPassword)
        btnSubmit = findViewById(R.id.btnSubmit)
        sessionManager = SessionManager(this)




        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .requestIdToken("505676312200-592v2395lrgtq0qijtuvhritajnf7mdi.apps.googleusercontent.com")
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Load saved credentials if remember password was checked
        if (sessionManager.shouldRememberPassword()) {
            val savedEmail = sessionManager.getSavedEmail()
            val savedPassword = sessionManager.getSavedPassword()
            if (savedEmail != null && savedPassword != null) {
                etEmail.setText(savedEmail)
                etPassword.setText(savedPassword)
                cbRememberPassword.isChecked = true
            }
        }

        // Back button functionality
        findViewById<android.widget.ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Submit button - Login
        btnSubmit.setOnClickListener {
            performLogin()
        }

        // Google Sign In button
        findViewById<android.widget.ImageButton>(R.id.btnGoogleSignIn).setOnClickListener {
            signInWithGoogle()
        }

        // Remember Password Checkbox
        cbRememberPassword.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                // Clear saved credentials if user unchecks
                sessionManager.clearCredentials()
            }
        }

        // Forgot Password Text (clickable)
        findViewById<TextView>(R.id.tvForgotPassword).setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        // Validate inputs
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

        // Disable button and show loading
        btnSubmit.isEnabled = false
        btnSubmit.text = "Đang đăng nhập..."

        // Call API
        RetrofitClient.api.login(email, password).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                btnSubmit.isEnabled = true
                btnSubmit.text = "Login"

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null) {
                        if (apiResponse.status == "success" && apiResponse.user != null) {
                            // Save user session
                            sessionManager.saveUserSession(apiResponse.user!!)

                            // Save credentials if remember password is checked
                            if (cbRememberPassword.isChecked) {
                                sessionManager.saveCredentials(email, password)
                            } else {
                                sessionManager.clearCredentials()
                            }

                            // Show success message
                            Toast.makeText(
                                this@SignInActivity,
                                "Đăng nhập thành công!",
                                Toast.LENGTH_SHORT
                            ).show()

                            // Navigate to HomeActivity
                            val intent = Intent(this@SignInActivity, HomeActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this@SignInActivity,
                                "Email hoặc mật khẩu không đúng",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        android.util.Log.e(
                            "SignIn",
                            "Response body is null. Code: ${response.code()}"
                        )
                        Toast.makeText(
                            this@SignInActivity,
                            "Lỗi: Server không trả về dữ liệu (Code: ${response.code()})",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e(
                        "SignIn",
                        "Response not successful. Code: ${response.code()}, Body: $errorBody"
                    )
                    Toast.makeText(
                        this@SignInActivity,
                        "Lỗi server (Code: ${response.code()}). Kiểm tra XAMPP và file PHP!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                btnSubmit.isEnabled = true
                btnSubmit.text = "Login"
                android.util.Log.e("SignIn", "Network error: ${t.message}", t)

                val errorMessage = when {
                    t.message?.contains("Unable to resolve host", ignoreCase = true) == true ->
                        "Không thể kết nối server. Kiểm tra:\n- XAMPP đã chạy chưa?\n- URL đúng chưa?\n- Đang dùng Emulator hay thiết bị thật?"

                    t.message?.contains("Connection refused", ignoreCase = true) == true ->
                        "Server từ chối kết nối. Kiểm tra Apache trong XAMPP đã chạy chưa?"

                    t.message?.contains("timeout", ignoreCase = true) == true ->
                        "Hết thời gian kết nối. Kiểm tra mạng và server."

                    else -> "Lỗi kết nối: ${t.message}\nKiểm tra XAMPP và URL API!"
                }
                Toast.makeText(this@SignInActivity, errorMessage, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun signInWithGoogle() {

        // Bắt buộc: signOut để luôn yêu cầu chọn tài khoản
        googleSignInClient.signOut().addOnCompleteListener {
            googleSignInClient.revokeAccess().addOnCompleteListener {

                // Sau khi signOut xong mới mở Google popup
                val signInIntent = googleSignInClient.signInIntent
                googleSignInLauncher.launch(signInIntent)
            }
        }
    }

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        handleGoogleSignInResult(task)
    }

    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // 🔥 LẤY ID TOKEN TẠI ĐÂY
            val idToken = account?.idToken

            if (idToken == null) {
                Toast.makeText(this, "Không lấy được ID Token!", Toast.LENGTH_SHORT).show()
                return
            }

            // Gửi idToken lên PHP để xác thực
            sendIdTokenToServer(idToken)

        } catch (e: ApiException) {
            android.util.Log.e("GoogleSignIn", "signInResult:failed code=" + e.statusCode)
            when (e.statusCode) {
                10 -> Toast.makeText(this, "Lỗi phát triển. Kiểm tra SHA-1 fingerprint", Toast.LENGTH_LONG).show()
                12501 -> Toast.makeText(this, "Đăng nhập Google bị hủy", Toast.LENGTH_SHORT).show()
                else -> Toast.makeText(this, "Lỗi đăng nhập Google: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun sendIdTokenToServer(idToken: String) {

        RetrofitClient.api.googleLogin(idToken).enqueue(object : Callback<ApiResponse> {

            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {

                val apiResponse = response.body()

                if (apiResponse?.status == "success" && apiResponse.user != null) {
                    sessionManager.saveUserSession(apiResponse.user!!)
                    Toast.makeText(this@SignInActivity, "Đăng nhập Google thành công!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@SignInActivity, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()

                } else {
                    Toast.makeText(this@SignInActivity, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                android.util.Log.e("GoogleSignIn", "Network error: ${t.message}", t)
                Toast.makeText(this@SignInActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}

