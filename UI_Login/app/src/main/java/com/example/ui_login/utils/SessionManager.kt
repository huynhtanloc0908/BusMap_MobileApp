package com.example.ui_login.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.ui_login.models.User
import com.google.gson.Gson

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = prefs.edit()
    private val gson = Gson()

    companion object {
        private const val PREF_NAME = "UserSession"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_USER = "user"
        private const val KEY_EMAIL = "email"
        private const val KEY_PASSWORD = "password"
        private const val KEY_REMEMBER_PASSWORD = "rememberPassword"
    }

    fun saveUserSession(user: User) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putString(KEY_USER, gson.toJson(user))
        editor.apply()
    }

    fun getUser(): User? {
        val userJson = prefs.getString(KEY_USER, null)
        return if (userJson != null) {
            gson.fromJson(userJson, User::class.java)
        } else {
            null
        }
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun logout() {
        editor.clear()
        editor.apply()
    }

    fun saveCredentials(email: String, password: String) {
        editor.putString(KEY_EMAIL, email)
        editor.putString(KEY_PASSWORD, password)
        editor.putBoolean(KEY_REMEMBER_PASSWORD, true)
        editor.apply()
    }

    fun getSavedEmail(): String? {
        return prefs.getString(KEY_EMAIL, null)
    }

    fun getSavedPassword(): String? {
        return prefs.getString(KEY_PASSWORD, null)
    }

    fun shouldRememberPassword(): Boolean {
        return prefs.getBoolean(KEY_REMEMBER_PASSWORD, false)
    }

    fun clearCredentials() {
        editor.remove(KEY_EMAIL)
        editor.remove(KEY_PASSWORD)
        editor.putBoolean(KEY_REMEMBER_PASSWORD, false)
        editor.apply()
    }
}

