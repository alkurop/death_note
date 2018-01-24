package com.omar.deathnote.notes.v2

import com.alkurop.database.ContentDao
import com.alkurop.database.NoteDao

class ContentPresenter(val noteDao: NoteDao, val contentDao: ContentDao) {}
