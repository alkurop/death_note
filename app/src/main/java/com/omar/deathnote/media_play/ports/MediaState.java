package com.omar.deathnote.media_play.ports;

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
    private IMediaClient audioClinet;

    public MediaState( ) {
        this.state = STATES.STOPPED;
        this.audioClinet = new IMediaClient() {
        } ;
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
