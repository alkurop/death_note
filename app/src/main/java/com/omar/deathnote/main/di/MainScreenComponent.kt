package com.omar.deathnote.main.di

import com.omar.deathnote.dagger.BaseComponent
import com.omar.deathnote.dagger.ComponentBuilder
import dagger.Subcomponent

@Subcomponent(modules = arrayOf(MainScreenModule::class))
interface MainScreenComponent : BaseComponent {

    @Subcomponent.Builder
    interface Builder : ComponentBuilder<MainScreenComponent> {
        override fun build(): MainScreenComponent
    }
}
