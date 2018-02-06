package com.omar.deathnote.notes.content.audio.recorder

import android.view.View
import com.alkurop.database.Content
import com.jakewharton.rxbinding2.view.RxView
import com.omar.deathnote.ComponentContainer
import com.omar.deathnote.R
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

    override fun bind(content: Content) {
        itemView.songTime.text = itemView.context.getString(R.string.rec_countet)
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
                        content.additionalContent = it.counter
                    }
                }
            }
    }

    override fun unbind() {
        recorderPresenter.stopRecording()
        dis.clear()
    }
}
