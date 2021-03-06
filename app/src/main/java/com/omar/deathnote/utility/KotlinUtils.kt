package com.omar.deathnote.utility

import com.alkurop.database.Content
import com.omar.deathnote.Constants
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import timber.log.Timber
import java.io.File

operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    this.add(disposable)
}


fun Content.deleteContentFile() {
    if (this.type == Constants.Frags.PicFragment.ordinal
        || this.type == Constants.Frags.AudioRecord.ordinal
        || this.type == Constants.Frags.AudioPlay.ordinal) {
        val path = content?.replace("file://", "") ?: ""
        val file = File(path)
        if (file.exists()) {
            try {
                file.delete()
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }
}
