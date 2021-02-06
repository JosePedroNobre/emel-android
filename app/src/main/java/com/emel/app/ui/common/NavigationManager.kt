package com.emel.app.ui.common

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.emel.app.BuildConfig
import com.emel.app.ui.flows.authentication.AuthenticationActivity
import com.emel.app.ui.flows.main.*
import javax.inject.Inject

class NavigationManager @Inject constructor() {

    companion object {
        // put bundle ids here
    }

    fun goToAuthentication(context: Context) {
        val intent = Intent(context, AuthenticationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(context, intent, null)
    }

    fun goToMainScreen(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(context, intent, null)
    }

    fun goToMap(fragmentManager: FragmentManager, @IdRes containerId: Int) {
        replaceFragment(MapFragment.newInstance(), fragmentManager, containerId)
    }

    fun goToTasks(fragmentManager: FragmentManager, @IdRes containerId: Int) {
        replaceFragment(TasksFragment.newInstance(), fragmentManager, containerId)
    }

    private fun replaceFragment(
        fragment: Fragment,
        fragmentManager: FragmentManager,
        @IdRes containerId: Int
    ) {
        fragmentManager
            .beginTransaction()
            .replace(containerId, fragment, fragment.javaClass.name)
            .commitNow()
    }

    fun showDialog(fragment: DialogFragment, fragmentManager: FragmentManager, tag: String?) {
        val ft = fragmentManager.beginTransaction()
        val dialog = fragmentManager.findFragmentByTag(tag) as? DialogFragment
        dialog?.let {
            ft.remove(dialog)
        }
        ft.addToBackStack(tag)
        fragment.show(ft, tag)
    }

    fun dismissDialog(dialog: Dialog) {
        dialog.dismiss()
    }
}