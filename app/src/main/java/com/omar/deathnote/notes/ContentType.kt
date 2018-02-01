package com.omar.deathnote.notes

import com.omar.deathnote.Constants

enum class ContentType {
    AUDIO_FILE,
    AUDIO_RECORD,
    LINK,
    NOTE,
    PICTURE_FILE,
    PICTURE_CAPTURE
}

fun ContentType.toFragType(): Constants.Frags =
    when (this) {
        ContentType.AUDIO_FILE -> Constants.Frags.AudioFragment
        ContentType.AUDIO_RECORD -> Constants.Frags.AudioRecord
        ContentType.NOTE -> Constants.Frags.NoteFragment
        ContentType.LINK -> Constants.Frags.LinkFragment
        ContentType.PICTURE_FILE -> Constants.Frags.PicFragment
        ContentType.PICTURE_CAPTURE -> Constants.Frags.PicFragment
    }
