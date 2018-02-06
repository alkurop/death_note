package com.omar.deathnote.utility

import com.alkurop.database.Link
import com.alkurop.database.LinkDao
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LinkLoader @Inject constructor(val linkDao: LinkDao) {

    fun Link.iskExpired(): Boolean {
        val expirationPeriond = TimeUnit.DAYS.convert(13, TimeUnit.MILLISECONDS)
        return timeStamp!! < System.currentTimeMillis() - expirationPeriond
    }

    fun getLinkFromApi(path: String) {}

    fun getLink(path: String) {}
}
