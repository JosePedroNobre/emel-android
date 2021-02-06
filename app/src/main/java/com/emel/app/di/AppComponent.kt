package com.emel.app.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        com.emel.app.di.module.ActivityBuilderModule::class,
        com.emel.app.di.module.FragmentBuilderModule::class,
        com.emel.app.di.module.ViewModelModule::class,
        com.emel.app.di.module.NetworkModule::class
    ]
)
interface AppComponent : AndroidInjector<com.emel.app.App> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): com.emel.app.di.AppComponent.Builder

        fun build(): com.emel.app.di.AppComponent
    }

    override fun inject(app: com.emel.app.App)
}