package com.emel.app.ui.flows.main

import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.emel.app.R
import com.emel.app.ui.base.BaseActivity
import com.emel.app.ui.common.NavigationManager
import com.emel.app.utils.clearSharedPreferences
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : BaseActivity<MainActivityVM>() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var navigationManager: NavigationManager

    override fun containerId() = R.id.fragment_container

    override fun layoutToInflate() = R.layout.activity_main

    override fun defineViewModel() =
        ViewModelProviders.of(this, viewModelFactory).get(MainActivityVM::class.java)

    override fun doOnCreated() {
        navigationManager.goToMap(supportFragmentManager, containerId())

        tasksIcon.setOnClickListener {
            navigationManager.goToTasks(supportFragmentManager, containerId())
            backButton.visibility = View.VISIBLE
            backButton.setOnClickListener {
                navigationManager.goToMap(
                    supportFragmentManager,
                    containerId()
                )
            }
            tasksIcon.visibility = View.GONE
        }

        logout.setOnClickListener {
            clearSharedPreferences()
            navigationManager.goToAuthentication(applicationContext)
            finish()
        }
    }
}