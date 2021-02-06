package com.emel.app.ui.flows.welcome.splash

import android.os.Handler
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.emel.app.R
import com.emel.app.ui.base.BaseActivity
import com.emel.app.ui.common.NavigationManager
import com.emel.app.utils.getToken
import javax.inject.Inject

class SplashActivity :
    BaseActivity<SplashVM>() {

    companion object {
        private const val SPLASH_DELAY: Long = 2000
    }

    @Inject
    lateinit var navigationManager: NavigationManager

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var mDelayHandler: Handler? = null

    private val mRunnable: Runnable = Runnable {
        if (!isFinishing) {

            if (getToken() != null) {
                navigationManager.goToMainScreen(applicationContext)
            } else {
                navigationManager.goToAuthentication(applicationContext)
            }
            finish()
        }
    }

    override fun layoutToInflate() = R.layout.activity_splash
    override fun defineViewModel() = ViewModelProviders.of(this, viewModelFactory)
        .get(SplashVM::class.java)

    override fun doOnCreated() {
        mDelayHandler = Handler()
        mDelayHandler!!.postDelayed(
            mRunnable,
            SPLASH_DELAY
        )
    }
}