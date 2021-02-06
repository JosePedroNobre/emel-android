package com.emel.app.ui.base

import android.os.Bundle
import android.view.WindowManager
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

abstract class BaseActivity<VM : ViewModel> : AppCompatActivity(), HasSupportFragmentInjector {

    companion object {
        const val DEFAULT_CONTAINER_ID = 0
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    protected val viewModel: VM by lazy {
        defineViewModel()
    }

    @LayoutRes
    abstract fun layoutToInflate(): Int

    abstract fun defineViewModel(): VM

    abstract fun doOnCreated()

    @IdRes
    open fun containerId() =
        com.emel.app.ui.base.BaseActivity.Companion.DEFAULT_CONTAINER_ID

    override fun supportFragmentInjector() = fragmentInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(layoutToInflate())
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        doOnCreated()

        if (savedInstanceState == null && containerId() != com.emel.app.ui.base.BaseActivity.Companion.DEFAULT_CONTAINER_ID) {
            loadInitialFragment()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    protected open fun loadInitialFragment() {}
}