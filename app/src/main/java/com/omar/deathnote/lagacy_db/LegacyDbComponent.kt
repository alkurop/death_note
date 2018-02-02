package com.omar.deathnote.lagacy_db

import com.omar.deathnote.BaseComponent
import com.omar.deathnote.ComponentBuilder
import com.omar.deathnote.notes.ContentViewComponent
import dagger.Subcomponent

@Subcomponent
interface LegacyDbComponent : BaseComponent {

    @Subcomponent.Builder
    interface Builder : ComponentBuilder<LegacyDbComponent> {
        override fun build(): LegacyDbComponent
    }

    fun inject(database: DB.DBHelper)
}

