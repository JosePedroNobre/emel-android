package com.emel.app.utils

import androidx.fragment.app.FragmentManager

object LoadingUtils {

    private val dialog = LoadingWidget()

    fun showLoading(fragmentManager: FragmentManager) {
        val ft = fragmentManager.beginTransaction()
        dialog.show(ft, LoadingWidget.TAG)
    }

    fun dismiss() {
        dialog.dismissDialog()
    }
}