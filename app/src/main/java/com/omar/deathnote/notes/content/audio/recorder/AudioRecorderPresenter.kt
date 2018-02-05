package com.omar.deathnote.notes.content.audio.recorder

import com.alkurop.database.ContentDao
import com.omar.deathnote.notes.content.BaseContentPresenter
import com.omar.deathnote.utility.AudioRecorderWrapper
import io.reactivex.Completable
import javax.inject.Inject

class AudioRecorderPresenter @Inject constructor(
        contentDao: ContentDao,
        audioRecorderWrapper: AudioRecorderWrapper
) : BaseContentPresenter(contentDao) {
    fun startRecording() {
    }

    fun stopRecording() {}
}
