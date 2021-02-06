package com.emel.app.di.module

import com.emel.app.ui.flows.authentication.AuthenticationActivity
import com.emel.app.ui.flows.main.MainActivity
import com.emel.app.ui.flows.welcome.splash.SplashActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilderModule {

    @ContributesAndroidInjector
    abstract fun contributesSplashActivity(): SplashActivity

    @ContributesAndroidInjector
    abstract fun contributesMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun contributesAuthenticationActivity(): AuthenticationActivity
}