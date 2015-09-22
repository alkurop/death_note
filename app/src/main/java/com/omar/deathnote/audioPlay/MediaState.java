package com.omar.deathnote.audioPlay;

/**
 * Created by omar on 9/17/15.
 */
public class MediaState {


    public enum STATES {
        PLAYING,
        PLAYING_SHUFFLE,
        PLAYING_REPEAT,
        RECORDING,
        PAUSED,
        STOPPED
    }

    private STATES state;
    private IMediaClient audioClinet;

    public MediaState(STATES state, IMediaClient client) {
        this.state = state;
        this.audioClinet = client;
    }

    public void setClinet(IMediaClient audioClinet) {
        this.audioClinet = audioClinet;
    }

    public void setState(STATES state) {
        this.state = state;
    }

    public IMediaClient getClient() {
        return audioClinet;
    }

    public STATES getState() {
        return state;
    }
}
