package com.gns.billing.session

import android.content.Context
import android.content.SharedPreferences

class SessionManager(
    context: Context
) {

    companion object {

        private const val PREF_NAME = "gns_session"

        private const val KEY_TOKEN = "token"

        private const val KEY_NAME = "name"

        private const val KEY_EMAIL = "email"

    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )

    /**
     * Menyimpan data session setelah login berhasil
     */
    fun saveSession(
        token: String,
        name: String,
        email: String
    ) {

        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_NAME, name)
            .putString(KEY_EMAIL, email)
            .apply()

    }

    /**
     * Mengambil token
     */
    fun getToken(): String? {

        return prefs.getString(KEY_TOKEN, null)

    }

    /**
     * Mengambil nama user
     */
    fun getName(): String {

        return prefs.getString(KEY_NAME, "") ?: ""

    }

    /**
     * Mengambil email user
     */
    fun getEmail(): String {

        return prefs.getString(KEY_EMAIL, "") ?: ""

    }

    /**
     * Mengecek apakah user sudah login
     */
    fun isLoggedIn(): Boolean {

        return !getToken().isNullOrEmpty()

    }

    /**
     * Menghapus seluruh session (Logout)
     */
    fun logout() {

        prefs.edit().clear().apply()

    }

}