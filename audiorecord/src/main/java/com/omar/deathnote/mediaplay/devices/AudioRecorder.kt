package com.omar.deathnote.mediaplay.devices

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Process
import io.reactivex.Completable
import io.reactivex.Notification
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.atomic.AtomicBoolean

data class AudioTimer(val time: Long)

class AudioRecorder {

    companion object {
        private val BIT_RATE_PRESETS = intArrayOf(320, 192, 160, 128)
        private val SAMPLE_RATE_PRESETS = intArrayOf(44100, 22050, 11025, 8000)

        private val AUDIO_FORMAT_PRESETS = shortArrayOf(
            AudioFormat.ENCODING_PCM_8BIT.toShort(),
            AudioFormat.ENCODING_PCM_16BIT.toShort()
        )

        private val QUALITY_PRESETS = intArrayOf(2, 5, 7)  // the lower the better
        private val CHANNEL_PRESETS = shortArrayOf(
            AudioFormat.CHANNEL_IN_MONO.toShort(),
            AudioFormat.CHANNEL_IN_STEREO.toShort()
        )

        private val channelConfig = Constants.CHANNEL_PRESETS[0]
        private val quality = Constants.QUALITY_PRESETS[1]
        private val audioFormat = Constants.AUDIO_FORMAT_PRESETS[1]
        private val sleepMillis = 100

    }

    private var minBufferSize: Int = 0
    private val mSampleRate: Int
    private val mBitRate: Int
    private lateinit var buffer: ShortArray
    private lateinit var mp3buffer: ByteArray

    init {
        this.mSampleRate = 41000
        this.mBitRate = 192
    }

    private var isStarted = AtomicBoolean(false)
    private val statusBus = PublishSubject.create<Notification<AudioTimer>>()
    private var counter: Int = 0
    private lateinit var filePath: String

    private val disposable = CompositeDisposable()

    fun startRecord(filePath: String): Observable<AudioTimer> {
        if (isStarted.get()) {
            stopRecord()
        }
        this.filePath = filePath

        minBufferSize = AudioRecord.getMinBufferSize(
            mSampleRate,
            channelConfig.toInt(),
            audioFormat.toInt()
        )

        return statusBus
            .dematerialize<AudioTimer>()
            .doOnTerminate { stopRecord() }
            .doOnDispose { stopRecord() }
    }

    fun stopRecord() {
        if (isStarted.get()) {
            isStarted.set(false)
            counter = 0
            statusBus.onNext(Notification.createOnComplete())
        }
    }

    private fun record(): Completable {
        return Completable.fromAction {
            isStarted.set(true)
            Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO)
            val outFile = File(filePath)
            if (outFile.exists()) {
                outFile.delete()
            }
            buffer = ShortArray(mSampleRate * (16 / 8) * 5) // SampleRate[Hz]
            mp3buffer = ByteArray((7200 + buffer.size.toDouble() * 2.0 * 1.25).toInt())
            val output = FileOutputStream(File(filePath))
            output.use {
                val recorder = findAudioRecord()
                recorder.startRecording()
                var readSize = 0
                while (isStarted.get()) {

                    readSize = recorder.read(buffer, 0, minBufferSize)
                    val encResult = SimpleLame.encode(buffer, buffer, readSize, mp3buffer)
                    output.write(mp3buffer, 0, encResult)

                }
                val flushResult = SimpleLame.flush(mp3buffer)
                if (flushResult != 0) {
                    output.write(mp3buffer, 0, flushResult)
                }
                recorder.stop()
                recorder.release()
                SimpleLame.close()
            }
        }
    }

    private fun findAudioRecord(): AudioRecord {
        minBufferSize = AudioRecord.getMinBufferSize(mSampleRate, channelConfig.toInt(), audioFormat.toInt())
        if (minBufferSize != AudioRecord.ERROR_BAD_VALUE) {

            val recorder = AudioRecord(MediaRecorder.AudioSource.DEFAULT, mSampleRate, channelConfig.toInt(), audioFormat.toInt(), minBufferSize * 4)
            SimpleLame.init(mSampleRate, 1, mSampleRate, mBitRate, quality)

            if (recorder.state == AudioRecord.STATE_INITIALIZED) {
                return recorder

            } else {
                throw Exception("Error init recorder")
            }

        } else {
            throw Exception("Error init recorder")
        }
    }
}

internal class Subscriber(
        val resultBus: Subject<AudioTimer>
) : DisposableObserver<AudioTimer>() {
    override fun onComplete() {
        resultBus.onComplete()
    }

    override fun onNext(value: AudioTimer) {
        resultBus.onNext(value)
    }

    override fun onError(e: Throwable) {
        resultBus.onError(e)
    }
}
