package com.omar.deathnote.dagger

import com.omar.deathnote.main.di.MainScreenComponent
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(subcomponents = arrayOf(MainScreenComponent::class))
interface FeaturesModule {
    @Binds
    @IntoMap
    @ComponentKey(MainScreenComponent::class)
    fun getMainScreenComponent(builder: MainScreenComponent.Builder): ComponentBuilder<*>
}
