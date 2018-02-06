package com.alkurop.audioplay;

public class AudioPlayerProgress {
    private final long mDuration;
    private final long mPosition;

    public AudioPlayerProgress(long duration, long position) {
        mDuration = duration;
        mPosition = position;
    }

    public long getDuration() {
        return mDuration;
    }

    public long getPosition() {
        return mPosition;
    }
}
