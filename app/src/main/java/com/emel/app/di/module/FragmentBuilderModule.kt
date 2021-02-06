package com.emel.app.di.module

import com.emel.app.ui.flows.main.MapFragment
import com.emel.app.ui.flows.main.TasksFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuilderModule {

    @ContributesAndroidInjector
    abstract fun contributes(): MapFragment

    @ContributesAndroidInjector
    abstract fun contributesTasksFragment(): TasksFragment

}