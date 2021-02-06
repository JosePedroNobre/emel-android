package com.emel.app.utils

import android.content.Context
import com.emel.app.utils.AppConstants.PIONEER_PREFERENCES
import com.emel.app.utils.AppConstants.PREF_REFRESH_TOKEN
import com.emel.app.utils.AppConstants.PREF_TOKEN


fun Context.setToken(token: String) {
    getSharedPreferences(PIONEER_PREFERENCES, Context.MODE_PRIVATE).edit()
        .putString(PREF_TOKEN, token).apply()
}

fun Context.getToken(): String? {
    return getSharedPreferences(PIONEER_PREFERENCES, Context.MODE_PRIVATE).getString(
        PREF_TOKEN,
        null
    )
}

fun Context.setRefreshToken(token: String) {
    getSharedPreferences(PIONEER_PREFERENCES, Context.MODE_PRIVATE).edit()
        .putString(PREF_REFRESH_TOKEN, token).apply()
}

fun Context.getRefreshToken(): String? {
    return getSharedPreferences(PIONEER_PREFERENCES, Context.MODE_PRIVATE).getString(
        PREF_REFRESH_TOKEN,
        null
    )
}

fun Context.clearSharedPreferences() {
    val preferences = getSharedPreferences(PIONEER_PREFERENCES, Context.MODE_PRIVATE)
    preferences.edit().clear().apply()
}