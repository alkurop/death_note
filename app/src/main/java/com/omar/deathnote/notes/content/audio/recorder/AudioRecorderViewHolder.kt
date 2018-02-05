package com.omar.deathnote.notes.content.audio.recorder

import android.animation.LayoutTransition
import android.view.View
import android.view.ViewGroup
import com.alkurop.database.Content
import com.jakewharton.rxbinding2.view.RxView
import com.omar.deathnote.BuildConfig
import com.omar.deathnote.ComponentContainer
import com.omar.deathnote.notes.ContentViewComponent
import com.omar.deathnote.notes.content.ContentViewHolder
import com.omar.deathnote.utility.plusAssign
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.note_elem_audio_record.view.btnRecord
import kotlinx.android.synthetic.main.note_elem_audio_record.view.btnStop
import kotlinx.android.synthetic.main.note_elem_audio_record.view.del
import kotlinx.android.synthetic.main.note_elem_audio_record.view.songTime
import javax.inject.Inject

class AudioRecorderViewHolder(
        itemView: View?,
        onDeleteCallback: (Long) -> Unit
) : ContentViewHolder(itemView, onDeleteCallback) {

    @Inject
    lateinit var recorderPresenter: AudioRecorderPresenter
    val dis = CompositeDisposable()

    init {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            (itemView as ViewGroup).layoutTransition
                .enableTransitionType(LayoutTransition.CHANGING)
        }
    }

    override fun bind(content: Content) {
        ComponentContainer.instance[ContentViewComponent::class.java].inject(this)
        recorderPresenter.content = content
        RxView.clicks(itemView.del).subscribe { onDeleteCallback.invoke(content.id) }
        RxView.clicks(itemView.btnRecord).subscribe {
            dis += recorderPresenter.startRecording()
        }
        RxView.clicks(itemView.btnStop).subscribe {
            recorderPresenter.stopRecording()
        }
        dis += recorderPresenter
            .stateBus
            .observeOn(mainThread())
            .subscribe {
                when (it) {
                    AudioRecordState.Recording -> {
                        itemView.btnRecord.visibility = View.GONE
                        itemView.btnStop.visibility = View.VISIBLE
                    }
                    AudioRecordState.Waiting -> {
                        itemView.btnStop.visibility = View.GONE
                        itemView.btnRecord.visibility = View.VISIBLE
                    }
                    is AudioRecordState.CounterUpdate -> {
                        itemView.songTime.text = it.counter
                    }
                }
            }
    }

    override fun unbind() {
        dis.clear()
    }
}
