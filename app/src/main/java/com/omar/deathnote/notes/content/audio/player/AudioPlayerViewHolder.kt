package com.omar.deathnote.notes.content.audio.player

import android.view.View
import android.widget.SeekBar
import com.alkurop.database.Content
import com.jakewharton.rxbinding2.view.RxView
import com.omar.deathnote.ComponentContainer
import com.omar.deathnote.R
import com.omar.deathnote.notes.ContentViewComponent
import com.omar.deathnote.notes.content.ContentViewHolder
import com.omar.deathnote.utility.plusAssign
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.note_elem_audio_playback.view.btnPause
import kotlinx.android.synthetic.main.note_elem_audio_playback.view.btnStart
import kotlinx.android.synthetic.main.note_elem_audio_playback.view.del
import kotlinx.android.synthetic.main.note_elem_audio_playback.view.seekBar
import kotlinx.android.synthetic.main.note_elem_audio_playback.view.songTime
import javax.inject.Inject

class AudioPlayerViewHolder(
        itemView: View?,
        onDeleteCallback: (Long) -> Unit
) : ContentViewHolder(itemView, onDeleteCallback) {

    val dis = CompositeDisposable()

    @Inject
    lateinit var playerPresenter: AudioPlayerPresenter

    override fun bind(content: Content) {
        itemView.songTime.text = itemView.context.getString(R.string.rec_countet)
        ComponentContainer.instance[ContentViewComponent::class.java].inject(this)
        playerPresenter.content = content

        RxView.clicks(itemView.del).subscribe { onDeleteCallback.invoke(content.id) }
        RxView.clicks(itemView.btnStart).subscribe {
            dis += playerPresenter.startPlayback()
        }
        RxView.clicks(itemView.btnPause).subscribe {
            playerPresenter.stopPlayback()
        }

        itemView.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    playerPresenter.seek((progress * 100).toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        if (content.additionalContent.isNullOrBlank().not()) {
            itemView.songTime.text = content.additionalContent
        }

        dis += playerPresenter
            .stateBus
            .subscribeOn(mainThread())
            .subscribe {
                when (it) {
                    AudioPlayerState.Waiting -> {
                        itemView.btnPause.visibility = View.GONE
                        itemView.seekBar.visibility = View.GONE
                        itemView.btnStart.visibility = View.VISIBLE
                    }
                    AudioPlayerState.Playing -> {
                        itemView.btnStart.visibility = View.GONE
                        itemView.btnPause.visibility = View.VISIBLE
                        itemView.seekBar.visibility = View.VISIBLE
                    }
                    is AudioPlayerState.AudioProgress -> {
                        itemView.seekBar.max = (it.progress.duration / 100).toInt()
                        itemView.seekBar.progress = (it.progress.position / 100).toInt()
                    }
                }
            }
    }

    override fun unbind() {
        dis.clear()
        playerPresenter.stopPlayback()
    }
}
