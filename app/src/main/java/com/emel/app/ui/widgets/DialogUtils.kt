package com.emel.app.ui.widgets

import androidx.fragment.app.FragmentManager
import com.emel.app.ui.common.NavigationManager
import com.emel.app.ui.widgets.dialogs.*

object DialogUtils {

    private var navigationManager: NavigationManager = NavigationManager()

    fun showGenericSuccessDialog(fragment: FragmentManager, textTitle: String, textBody: String) {
        navigationManager.showDialog(
            GenericSuccessDialog.newInstance(textTitle, textBody),
            fragment,
            GenericSuccessDialog.TAG
        )
    }
}