package com.emel.app

import android.app.Activity
import android.content.Context
import androidx.multidex.MultiDexApplication
import com.emel.app.di.DaggerAppComponent
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject


class App @Inject constructor() : HasActivityInjector, MultiDexApplication() {

    companion object {
        lateinit var instance: App
            private set

    }

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector() = activityInjector

    override fun onCreate() {
        super.onCreate()
    }



    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        DaggerAppComponent.builder().application(this).build().inject(this)
    }
}