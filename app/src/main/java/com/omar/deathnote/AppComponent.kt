package com.omar.deathnote

import com.omar.deathnote.picview.SingleViewFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(modules = arrayOf(
        AppModule::class,
        DatabaseModule::class,
        FeaturesModule::class
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

    fun inject(fragment: SingleViewFragment)

}
