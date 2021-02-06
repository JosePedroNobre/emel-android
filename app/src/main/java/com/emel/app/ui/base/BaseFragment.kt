package com.emel.app.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

abstract class BaseFragment<VM : ViewModel> : Fragment(), HasSupportFragmentInjector {

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    protected val viewModel: VM by lazy {
        defineViewModel()
    }

    @LayoutRes
    abstract fun layoutToInflate(): Int

    abstract fun defineViewModel(): VM

    abstract fun doOnCreated()

    open fun initializesArguments(arguments: Bundle) {}

    override fun supportFragmentInjector() = fragmentInjector

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        AndroidSupportInjection.inject(this)
        return inflater.inflate(layoutToInflate(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            initializesArguments(arguments!!)
        }
        doOnCreated()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    fun getContainerId() = (activity as com.emel.app.ui.base.BaseActivity<*>).containerId()
}