package com.omar.deathnote.notes.v2

import com.omar.deathnote.BaseComponent
import com.omar.deathnote.ComponentBuilder
import dagger.Subcomponent

@Subcomponent(modules = [(ContentViewModule::class)])
@ContentViewScope
interface ContentViewComponent : BaseComponent {

    @Subcomponent.Builder
    interface Builder : ComponentBuilder<ContentViewComponent> {
        override fun build(): ContentViewComponent
    }

    fun inject(activity: ContentActivity)
}
