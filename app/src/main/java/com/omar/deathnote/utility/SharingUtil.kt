package com.omar.deathnote.utility

import android.content.Intent
import android.net.Uri
import android.support.v4.content.FileProvider
import com.alkurop.database.Content
import com.alkurop.database.ContentDao
import com.alkurop.github.mediapicker.MediaPicker
import com.omar.deathnote.App
import com.omar.deathnote.Constants
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.io
import java.io.File
import javax.inject.Inject


class SharingUtil @Inject constructor(
        private val contentDao: ContentDao,
        private val application: App
) {

    fun share(noteId: Long): Completable {
        return contentDao.getRelatedToNote(noteId)
            .subscribeOn(io())
            .observeOn(mainThread())
            .firstOrError()
            .flatMapCompletable {
                Completable.fromAction {
                    val sharingIntent = getSharingIntent(it)
                    application.startActivity(sharingIntent)
                }
            }
    }

    private fun getSharingIntent(contentList: List<Content>): Intent {
        val subject = contentList.filter { it.type == Constants.Frags.TitleFragment.ordinal }
            .first().content

        val textStringBuilder = StringBuilder()
        contentList
            .filter {
                it.type == Constants.Frags.NoteFragment.ordinal
                        || it.type == Constants.Frags.LinkFragment.ordinal
            }
            .map { it.content }
            .filter { it.isNullOrEmpty().not() }
            .forEach {
                textStringBuilder.append(it).append("\n")
            }
        val text = textStringBuilder.toString()

        val mediaPickerUris = contentList.filter { it.type == Constants.Frags.PicFragment.ordinal }
            .map { it.content }
            .map {
                Uri.parse(it)
            }

        val urisArrayList = ArrayList(mediaPickerUris)


        val intent = Intent()
        intent.action = Intent.ACTION_SEND_MULTIPLE
        intent.putExtra(Intent.EXTRA_TEXT, text)
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.type = "message/rfc822"
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
        intent.putParcelableArrayListExtra(
            android.content.Intent.EXTRA_STREAM, urisArrayList
        )
        return intent
    }

}
