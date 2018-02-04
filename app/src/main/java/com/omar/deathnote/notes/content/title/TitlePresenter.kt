package com.omar.deathnote.notes.content.title

import com.alkurop.database.ContentDao
import com.omar.deathnote.notes.content.BaseContentPresenter
import javax.inject.Inject

class TitlePresenter @Inject constructor(contentDao: ContentDao) : BaseContentPresenter(contentDao)
