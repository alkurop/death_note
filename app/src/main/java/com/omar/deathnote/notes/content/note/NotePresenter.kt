package com.omar.deathnote.notes.content.note

import com.alkurop.database.ContentDao
import com.omar.deathnote.notes.content.BaseContentPresenter
import javax.inject.Inject

class NotePresenter @Inject constructor(contentDao: ContentDao) : BaseContentPresenter(contentDao)
