package com.hritik.demoapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

class PreferenceUtility(context: Context) {
    companion object {
        const val IS_LOGGED_IN = "IS_LOGGED_IN"
        const val NAME = "NAME"
    }

    private var preferences: SharedPreferences =
        context.getSharedPreferences("com.hritik.demo", Context.MODE_PRIVATE)

    var isLoggedIn: Boolean
        get() = preferences.getBoolean(IS_LOGGED_IN, false)
        set(isLoggedIn) = preferences.edit().putBoolean(IS_LOGGED_IN, isLoggedIn).apply()

    var name: String
        get() = preferences.getString(NAME, "")!!
        set(name) = preferences.edit().putString(NAME, name).apply()

    @SuppressLint("ApplySharedPref")
    fun logout(): Boolean {
        preferences.edit().clear().commit()
        return true
    }

}