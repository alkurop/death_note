package com.omar.deathnote.notes.content.audio.player

import com.alkurop.database.ContentDao
import com.omar.deathnote.notes.content.BaseContentPresenter
import javax.inject.Inject

class AudioPlayerPresenter @Inject constructor(contentDao: ContentDao) : BaseContentPresenter(contentDao)
