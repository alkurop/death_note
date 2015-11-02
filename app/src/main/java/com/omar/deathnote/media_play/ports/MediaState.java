package com.omar.deathnote.media_play.ports;

import java.lang.ref.WeakReference;

/**
 * Created by omar on 9/17/15.
 */
public class MediaState {

    public enum STATES {
        PLAYING_AUDIO,
        RECORDING_AUDIO,
        PAUSED_AUDIO,
        STOPPED
    }

    private STATES state;
    private WeakReference<IMediaClient> audioClient;

    public MediaState() {
        this.state = STATES.STOPPED;

    }

    public void setClinet(IMediaClient audioClinet) {
        audioClient = new WeakReference<IMediaClient>(audioClinet);
    }

    public void setState(STATES state) {
        this.state = state;
    }

    public IMediaClient getClient() {
        return audioClient.get();
    }

    public STATES getState() {
        return state;
    }
}
