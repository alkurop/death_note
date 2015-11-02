package com.omar.deathnote.mediaplay.ports;

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
    private  IMediaClient audioClient;

    public MediaState() {
        this.state = STATES.STOPPED;
        this.audioClient =  new IMediaClient() {
        } ;
    }

    public void setClinet(IMediaClient audioClinet) {
        audioClient = audioClinet;
    }

    public void setState(STATES state) {
        this.state = state;
    }

    public IMediaClient getClient() {

        return audioClient ;
    }

    public STATES getState() {
        return state;
    }
}
