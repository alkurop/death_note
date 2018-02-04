package com.omar.deathnote.notes.content.audio

import com.alkurop.database.ContentDao
import com.omar.deathnote.notes.content.BaseContentPresenter
import javax.inject.Inject

class AudioPresenter @Inject constructor(contentDao: ContentDao) : BaseContentPresenter(contentDao)
