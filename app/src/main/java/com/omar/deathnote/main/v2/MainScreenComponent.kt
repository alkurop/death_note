package com.omar.deathnote.main.v2

import com.omar.deathnote.dagger.BaseComponent
import com.omar.deathnote.dagger.ComponentBuilder
import dagger.Subcomponent

@Subcomponent(modules = arrayOf(MainScreenModule::class))
@MainViewScope
interface MainScreenComponent : BaseComponent {

    @Subcomponent.Builder
    interface Builder : ComponentBuilder<MainScreenComponent> {
        override fun build(): MainScreenComponent
    }

    fun inject(activity: MainActivity1)
}
