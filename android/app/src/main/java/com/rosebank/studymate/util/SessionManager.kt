package com.rosebank.studymate.util

import android.content.Context
import android.content.SharedPreferences

// Stores the JWT auth token and basic user info locally so the user stays
// logged in between app launches. Uses plain SharedPreferences for
// simplicity in the prototype; EncryptedSharedPreferences would be a
// worthwhile upgrade for the final PoE submission.
class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("studymate_session", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun getBearerToken(): String = "Bearer ${getToken()}"

    fun isLoggedIn(): Boolean = !getToken().isNullOrEmpty()

    fun saveUserName(name: String) {
        prefs.edit().putString(KEY_NAME, name).apply()
    }

    fun getUserName(): String? = prefs.getString(KEY_NAME, null)

    fun clear() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_NAME = "user_name"
    }
}
