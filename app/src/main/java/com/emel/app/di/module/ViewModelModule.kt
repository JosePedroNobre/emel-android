package com.emel.app.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.emel.app.di.ViewModelFactory
import com.emel.app.di.ViewModelKey
import com.emel.app.ui.flows.authentication.AuthenticationActivityVM
import com.emel.app.ui.flows.main.MainActivityVM
import com.emel.app.ui.flows.main.MapFragmentVM
import com.emel.app.ui.flows.main.TasksFragmentVM
import com.emel.app.ui.flows.welcome.splash.SplashVM
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindsViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(SplashVM::class)
    abstract fun bindsSplashVM(vm: SplashVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MapFragmentVM::class)
    abstract fun bindsMap(vm: MapFragmentVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TasksFragmentVM::class)
    abstract fun bindsTaskVM(vm: TasksFragmentVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainActivityVM::class)
    abstract fun binds(vm: MainActivityVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AuthenticationActivityVM::class)
    abstract fun bindsAuthenticationVM(vm: AuthenticationActivityVM): ViewModel

}