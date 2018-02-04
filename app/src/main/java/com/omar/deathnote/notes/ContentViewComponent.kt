package com.omar.deathnote.notes

import com.omar.deathnote.BaseComponent
import com.omar.deathnote.ComponentBuilder
import com.omar.deathnote.notes.content.audio.AudioViewHolder
import com.omar.deathnote.notes.content.link.LinkViewHolder
import com.omar.deathnote.notes.content.note.NoteViewHolder
import com.omar.deathnote.notes.content.picture.PictureViewHolder
import com.omar.deathnote.notes.content.title.TitleViewHolder
import dagger.Subcomponent

@Subcomponent()
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
