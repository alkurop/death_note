package com.omar.deathnote.dagger

import com.omar.deathnote.App
import dagger.BindsInstance
import dagger.Component

@Component(modules = arrayOf(
        FeaturesModule::class,
        DatabaseModule::class
))
interface AppComponent : BaseComponent {

    @Component.Builder
    interface Builder : ComponentBuilder<AppComponent> {

        @BindsInstance
        fun application(application: App): Builder

        override fun build(): AppComponent

    }

    fun inject(daggerComponentContainer: DaggerComponentContainer)

}
