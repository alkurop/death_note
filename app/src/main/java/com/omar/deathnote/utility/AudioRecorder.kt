package com.omar.deathnote.utility

class AudioRecorder {
    sealed class AudioMessage {
        data class Timer(val time: Long)
    }
}
