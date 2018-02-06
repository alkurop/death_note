package com.omar.deathnote.notes.content.audio.recorder

import com.alkurop.database.ContentDao
import com.omar.deathnote.Constants
import com.omar.deathnote.notes.content.BaseContentPresenter
import com.omar.deathnote.utility.AudioWrapper
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AudioRecorderPresenter @Inject constructor(
        contentDao: ContentDao,
        val audioRecorderWrapper: AudioWrapper
) : BaseContentPresenter(contentDao) {

    val stateBus = BehaviorSubject.create<AudioRecordState>()
    fun startRecording(): Disposable {
        stateBus.onNext(AudioRecordState.Recording)
        content.content = audioRecorderWrapper.generateFilePath()
        return audioRecorderWrapper
            .startRecording(content.content!!)
            .doOnTerminate { stopRecording() }
            .subscribe { millis ->
                val toHours = TimeUnit.MILLISECONDS.toHours(millis)
                val toMinutes = TimeUnit.MILLISECONDS.toMinutes(millis)
                val toSeconds = TimeUnit.MILLISECONDS.toSeconds(millis)

                val hours = if (toHours > 10) "$toHours" else "0$toHours"
                val minutes = if (toMinutes > 10) "$toMinutes" else "0$toMinutes"
                val seconds = if (toSeconds > 10) "$toSeconds" else "0$toSeconds"

                val string = "$hours : $minutes : $seconds"
                stateBus.onNext(AudioRecordState.CounterUpdate(string))
            }
    }

    fun stopRecording() {
        stateBus.onNext(AudioRecordState.Waiting)
        audioRecorderWrapper.stopRecording()
        content.type = Constants.Frags.AudioPlay.ordinal
        save()
    }
}

sealed class AudioRecordState {
    object Recording : AudioRecordState()
    object Waiting : AudioRecordState()
    data class CounterUpdate(val counter: String) : AudioRecordState()
}
