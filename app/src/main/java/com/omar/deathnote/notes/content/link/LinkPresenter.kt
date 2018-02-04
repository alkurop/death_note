package com.omar.deathnote.notes.content.link

import com.alkurop.database.ContentDao
import com.omar.deathnote.notes.content.BaseContentPresenter
import javax.inject.Inject

class LinkPresenter @Inject constructor(contentDao: ContentDao) : BaseContentPresenter(contentDao)
