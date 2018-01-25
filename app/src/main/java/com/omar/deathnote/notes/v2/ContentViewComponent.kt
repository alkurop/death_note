package com.omar.deathnote.notes.v2

import com.omar.deathnote.BaseComponent
import com.omar.deathnote.ComponentBuilder
import com.omar.deathnote.notes.v2.audio.AudioViewHolder
import com.omar.deathnote.notes.v2.link.LinkViewHolder
import com.omar.deathnote.notes.v2.note.NoteViewHolder
import com.omar.deathnote.notes.v2.picture.PictureViewHolder
import com.omar.deathnote.notes.v2.title.TitleViewHolder
import dagger.Subcomponent

@Subcomponent(modules = [(ContentViewModule::class)])
@ContentViewScope
interface ContentViewComponent : BaseComponent {

    @Subcomponent.Builder
    interface Builder : ComponentBuilder<ContentViewComponent> {
        override fun build(): ContentViewComponent
    }

    fun inject(activity: ContentActivity)

    fun inject(activity: TitleViewHolder)

    fun inject(pictureViewHolder: PictureViewHolder)

    fun inject(noteViewHolder: NoteViewHolder)

    fun inject(audioViewHolder: AudioViewHolder)

    fun inject(linkViewHolder: LinkViewHolder)
}
