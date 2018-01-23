package com.omar.deathnote.dagger

import com.omar.deathnote.App
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(modules = arrayOf(
        FeaturesModule::class,
        DatabaseModule::class
))
@Singleton
interface AppComponent : BaseComponent {

    @Component.Builder
    interface Builder : ComponentBuilder<AppComponent> {

        @BindsInstance
        fun application(application: App): Builder

        override fun build(): AppComponent

    }

    fun inject(daggerComponentContainer: ComponentContainer)

}
