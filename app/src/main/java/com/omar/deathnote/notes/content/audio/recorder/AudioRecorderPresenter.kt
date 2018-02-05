package com.omar.deathnote.notes.content.audio.recorder

import com.alkurop.database.ContentDao
import com.omar.deathnote.notes.content.BaseContentPresenter
import com.omar.deathnote.utility.AudioRecorderWrapper
import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AudioRecorderPresenter @Inject constructor(
        contentDao: ContentDao,
        val audioRecorderWrapper: AudioRecorderWrapper
) : BaseContentPresenter(contentDao) {

    val stateBus = BehaviorSubject.create<AudioRecordState>()
    fun startRecording():Disposable {
        stateBus.onNext(AudioRecordState.Recording)
        return audioRecorderWrapper.startRecording().subscribe { millis ->
            val string = "${TimeUnit.MILLISECONDS.toHours(millis)} : ${TimeUnit.MILLISECONDS.toMinutes(millis)} : ${TimeUnit.MILLISECONDS.toSeconds(millis)}"
            stateBus.onNext(AudioRecordState.CounterUpdate(string))
        }
    }

    fun stopRecording() {
        stateBus.onNext(AudioRecordState.Waiting)
        audioRecorderWrapper.stopRecording()
    }
}

sealed class AudioRecordState {
    object Recording : AudioRecordState()
    object Waiting : AudioRecordState()
    data class CounterUpdate(val counter: String) : AudioRecordState()
}
