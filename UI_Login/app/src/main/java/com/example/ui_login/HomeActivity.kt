package com.example.ui_login

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ui_login.utils.SessionManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    
    private lateinit var sessionManager: SessionManager
    private lateinit var tvUserName: TextView
    private lateinit var bottomNavigation: BottomNavigationView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        
        // Initialize SessionManager
        sessionManager = SessionManager(this)
        
        // Initialize views
        tvUserName = findViewById(R.id.tvUserName)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        
        // Load user data
        loadUserData()
        
        // Setup bottom navigation
        setupBottomNavigation()
        
        // Setup click listeners
        setupClickListeners()
    }
    
    private fun loadUserData() {
        val user = sessionManager.getUser()
        if (user != null) {
            // Display user's full name
            tvUserName.text = user.full_name
        } else {
            // If no user session, redirect to MainActivity
            tvUserName.text = "Đăng nhập"
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    
    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Already on home
                    true
                }
                R.id.nav_map -> {
                    Toast.makeText(this, "Bản đồ", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_notification -> {
                    Toast.makeText(this, "Thông báo", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_account -> {
                    Toast.makeText(this, "Tài khoản", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
        
        // Set home as selected by default
        bottomNavigation.selectedItemId = R.id.nav_home
    }
    
    private fun setupClickListeners() {
        // Main action buttons
        findViewById<android.view.View>(R.id.btnBuytMetro).setOnClickListener {
            Toast.makeText(this, "Buyt/Metro", Toast.LENGTH_SHORT).show()
        }
        
        findViewById<android.view.View>(R.id.btnTimDuong).setOnClickListener {
            Toast.makeText(this, "Tìm đường", Toast.LENGTH_SHORT).show()
        }
        
        findViewById<android.view.View>(R.id.btnGopY).setOnClickListener {
            Toast.makeText(this, "Góp ý", Toast.LENGTH_SHORT).show()
        }
        
        findViewById<android.view.View>(R.id.btnYeuThich).setOnClickListener {
            Toast.makeText(this, "Yêu thích", Toast.LENGTH_SHORT).show()
        }
        
        // Service buttons

        
        findViewById<android.view.View>(R.id.btnThongTin).setOnClickListener {
            Toast.makeText(this, "Thông tin", Toast.LENGTH_SHORT).show()
        }
        
        findViewById<android.view.View>(R.id.btnCongDong).setOnClickListener {
            Toast.makeText(this, "Cộng đồng BusMap", Toast.LENGTH_SHORT).show()
        }
        
        // User name click - show profile or logout option
        tvUserName.setOnClickListener {
            Toast.makeText(this, "Xin chào ${sessionManager.getUser()?.full_name}", Toast.LENGTH_SHORT).show()
        }
    }
}