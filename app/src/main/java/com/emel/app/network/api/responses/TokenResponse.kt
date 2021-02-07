package com.emel.app.network.api.responses

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TokenResponse(val token: String, val refreshToken: String) : Parcelable