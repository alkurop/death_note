package com.omar.deathnote.utility

import com.alkurop.audioplay.AudioPlayer
import com.alkurop.audioplay.AudioPlayerImpl
import com.alkurop.audioplay.AudioPlayerProgress
import com.alkurop.github.mediapicker.MediaPicker
import com.alkurop.github.mediapicker.createFileName
import com.alkurop.github.mediapicker.getFileDirectory
import com.omar.deathnote.App
import com.omar.deathnote.mediaplay.devices.AudioRecorder
import com.omar.deathnote.notes.ContentViewScope
import io.reactivex.Observable
import javax.inject.Inject

@ContentViewScope
class AudioWrapper @Inject constructor(appplication: App) {
    private val audioPlayer: AudioPlayer = AudioPlayerImpl(appplication)

    fun generateFilePath(): String {
        return getFileDirectory(MediaPicker.fileDirectory) + "/" + createFileName(".mp3")
    }

    fun startRecording(filePath: String): Observable<Long> {
        stopPlayback()
        return AudioRecorder.startRecord(filePath)
    }

    fun stopRecording() {
        AudioRecorder.stopRecord()
    }

    fun startPlayback(filePath: String, audioPosition: AudioPlayerProgress?): Observable<AudioPlayerProgress> {
        stopRecording()
        audioPlayer.startPlayback(filePath, audioPosition?.position ?: 0)
        return audioPlayer.playerProgress
    }

    fun stopPlayback() {
        audioPlayer.stopPlayback()
    }

    fun seekTo(position: Long) {
        audioPlayer.seekTo(position)
    }

    fun tearDown() {
        audioPlayer.tearDown()
    }
}
