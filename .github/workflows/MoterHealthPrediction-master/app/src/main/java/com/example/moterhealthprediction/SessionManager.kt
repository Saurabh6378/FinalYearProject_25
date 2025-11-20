package com.example.moterhealthprediction

import android.content.Context
import android.content.SharedPreferences

object SessionManager {
    private const val PREF_NAME = "motor_app_session"
    private const val KEY_USER_NAME = "user_name"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveUserName(context: Context, name: String) {
        getPrefs(context).edit().putString(KEY_USER_NAME, name).apply()
    }

    fun getUserName(context: Context): String {
        return getPrefs(context).getString(KEY_USER_NAME, "User") ?: "User"
    }

    fun clearSession(context: Context) {
        getPrefs(context).edit().clear().apply()
    }
}
