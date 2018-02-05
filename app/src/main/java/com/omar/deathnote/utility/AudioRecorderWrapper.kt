package com.omar.deathnote.utility

import android.content.Context
import com.alkurop.github.mediapicker.MediaPicker
import com.alkurop.github.mediapicker.createFileName
import com.alkurop.github.mediapicker.getFileDirectory
import com.omar.deathnote.App
import com.omar.deathnote.mediaplay.devices.AudioRecorder
import io.reactivex.Observable
import javax.inject.Inject

class AudioRecorderWrapper @Inject constructor(val application: App) {
    private fun generateLink(context: Context): String {
        return getFileDirectory(MediaPicker.fileDirectory) + "/" + createFileName(".mp3")
    }

    fun startRecording(): Observable<Long> {
        return AudioRecorder.startRecord(generateLink(application))
    }

    fun stopRecording(){
        AudioRecorder.stopRecord()
    }
}
