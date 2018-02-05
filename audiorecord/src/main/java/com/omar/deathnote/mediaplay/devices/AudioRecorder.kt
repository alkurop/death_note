package com.omar.deathnote.mediaplay.devices

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Process
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

object AudioRecorder {

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

    private val channelConfig = CHANNEL_PRESETS[0]
    private val quality = QUALITY_PRESETS[1]
    private val audioFormat = AUDIO_FORMAT_PRESETS[1]
    private val TIMER_RESOLUTION_SLEEP_MILLIS = 100L


    private var minBufferSize: Int = 0
    private val mSampleRate: Int = SAMPLE_RATE_PRESETS[0]
    private val mBitRate: Int = BIT_RATE_PRESETS[1]
    private lateinit var buffer: ShortArray
    private lateinit var mp3buffer: ByteArray

    private var isStarted = AtomicBoolean(false)

    fun startRecord(filePath: String): Observable<Long> {
        if (isStarted.get()) {
            stopRecord()
        }
        minBufferSize = AudioRecord.getMinBufferSize(
            mSampleRate,
            channelConfig.toInt(),
            audioFormat.toInt()
        )

        val resultBus = PublishSubject.create<Long>()
        val observer = Subscriber<Long>(resultBus)

        Observable.interval(TIMER_RESOLUTION_SLEEP_MILLIS, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .subscribeWith(observer)

        record(filePath).toObservable<Long>()
            .subscribeOn(Schedulers.io())
            .subscribeWith(observer)

        return resultBus
            .doOnDispose { stopRecord() }
    }

    fun stopRecord() {
        if (isStarted.get()) {
            isStarted.set(false)
        }
    }

    private fun record(filePath: String): Completable {
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
                var readSize: Int
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
        minBufferSize = AudioRecord.getMinBufferSize(
            mSampleRate,
            channelConfig.toInt(),
            audioFormat.toInt()
        )
        if (minBufferSize != AudioRecord.ERROR_BAD_VALUE) {

            val recorder = AudioRecord(
                MediaRecorder.AudioSource.DEFAULT,
                mSampleRate, channelConfig.toInt(),
                audioFormat.toInt(),
                minBufferSize * 4
            )
            SimpleLame.init(
                mSampleRate,
                1,
                mSampleRate,
                mBitRate,
                quality
            )

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

private operator fun CompositeDisposable.plusAssign(dis: Disposable) {
    this.add(dis)
}

internal class Subscriber<T>(
        val resultBus: Subject<T>
) : Observer<T> {
    override fun onSubscribe(d: Disposable) {

    }

    override fun onComplete() {
        resultBus.onComplete()
        println("Sub completed")
    }

    override fun onNext(value: T) {
        resultBus.onNext(value)
    }

    override fun onError(e: Throwable) {
        resultBus.onError(e)
    }
}

