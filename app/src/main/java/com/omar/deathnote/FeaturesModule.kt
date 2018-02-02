package com.omar.deathnote

import com.omar.deathnote.lagacy_db.LegacyDbComponent
import com.omar.deathnote.main.MainScreenComponent
import com.omar.deathnote.notes.ContentViewComponent
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(
    subcomponents = [
        ContentViewComponent::class,
        LegacyDbComponent::class,
        MainScreenComponent::class
    ]
)
interface FeaturesModule {

    @Binds
    @IntoMap
    @ComponentKey(MainScreenComponent::class)
    fun getMainScreen(builder: MainScreenComponent.Builder): ComponentBuilder<*>

    @Binds
    @IntoMap
    @ComponentKey(ContentViewComponent::class)
    fun getContentView(builder: ContentViewComponent.Builder): ComponentBuilder<*>

    @Binds
    @IntoMap
    @ComponentKey(LegacyDbComponent::class)
    fun getLegacyDbCOmponent(builder: LegacyDbComponent.Builder): ComponentBuilder<*>

}
