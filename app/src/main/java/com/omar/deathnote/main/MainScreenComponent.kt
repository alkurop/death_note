package com.omar.deathnote.main

import com.omar.deathnote.BaseComponent
import com.omar.deathnote.ComponentBuilder
import dagger.Subcomponent

@Subcomponent(modules = [(MainScreenModule::class)])
@MainViewScope
interface MainScreenComponent : BaseComponent {

    @Subcomponent.Builder
    interface Builder : ComponentBuilder<MainScreenComponent> {
        override fun build(): MainScreenComponent
    }

    fun inject(activity: MainActivity)
}
