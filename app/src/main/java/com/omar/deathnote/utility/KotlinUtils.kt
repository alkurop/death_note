package com.omar.deathnote.utility

import android.content.Context
import com.alkurop.database.Content
import com.omar.deathnote.Constants
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.io.File

operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    this.add(disposable)
}


fun Content.deleteContentFile(context: Context) {
    if (this.type == Constants.Frags.PicFragment.ordinal
        || this.type == Constants.Frags.AudioRecord.ordinal
        || this.type == Constants.Frags.AudioPlay.ordinal
        && this.content.isNullOrBlank().not()) {
        val path = content!!.replace("file://", "")
        val file = File(path)
        if (file.exists()) {
            file.delete()
        }
    }
}
