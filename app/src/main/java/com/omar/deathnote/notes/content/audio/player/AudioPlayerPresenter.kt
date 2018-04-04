package com.omar.deathnote.notes.content.audio.player

import com.alkurop.audioplay.AudioPlayerProgress
import com.alkurop.database.ContentDao
import com.omar.deathnote.notes.content.BaseContentPresenter
import com.omar.deathnote.notes.content.audio.recorder.AudioRecordState
import com.omar.deathnote.utility.AudioWrapper
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers.io
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class AudioPlayerPresenter @Inject constructor(
        contentDao: ContentDao,
        val audioWrapper: AudioWrapper
) : BaseContentPresenter(contentDao) {

    var latestState: AudioPlayerProgress? = null
    val stateBus = BehaviorSubject.create<AudioPlayerState>()

    fun startPlayback(): Disposable {
        stateBus.onNext(AudioPlayerState.Playing)
        val contentUri = content.content ?: return CompositeDisposable()
        return audioWrapper.startPlayback(contentUri, latestState)
            .subscribeOn(io())
            .doOnTerminate { stopPlayback() }
            .subscribe {
                latestState = if (it.duration != it.position) {
                    it
                } else {
                    null
                }
                stateBus.onNext(AudioPlayerState.AudioProgress(it))
            }
    }

    fun stopPlayback() {
        audioWrapper.stopPlayback()
        stateBus.onNext(AudioPlayerState.Waiting)
    }

    fun seek(progress: Long) {
        audioWrapper.seekTo(progress)
    }
}

sealed class AudioPlayerState {
    object Playing : AudioPlayerState()
    object Waiting : AudioPlayerState()
    data class AudioProgress(val progress: AudioPlayerProgress) : AudioPlayerState()
}
