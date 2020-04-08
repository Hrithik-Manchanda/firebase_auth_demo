package com.hritik.demoapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities


object NetworkUtils {
    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val activeNW =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        return when {
            activeNW.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNW.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNW.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}