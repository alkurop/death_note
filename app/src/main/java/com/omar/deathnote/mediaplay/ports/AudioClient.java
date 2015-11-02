package com.omar.deathnote.mediaplay.ports;

/**
 * Created by omar on 9/17/15.
 */
abstract public class AudioClient implements IAudioClient, IMediaClient {
    protected String filepath;
    protected IMediaManager mediaManager;

    public enum State {
        IS_PLAYING_THIS,
        IS_RECORDING_THIS,
        IS_PAUSED_THIS,
        NOT_THIS_OR_STOPPED
    }

    public AudioClient() { mediaManager = MediaManager.I();}

    @Override
    public void setFilePath(String filepath) {
        this.filepath = filepath;
    }

    @Override
    public State getThisState() {
        if (mediaManager.getMediaState().getClient() != this)
            return State.NOT_THIS_OR_STOPPED;
        switch (mediaManager.getMediaState().getState()) {
            case PAUSED_AUDIO:
                return State.IS_PAUSED_THIS;
            case RECORDING_AUDIO:
                return State.IS_RECORDING_THIS;
            case PLAYING_AUDIO:
                return State.IS_PLAYING_THIS;
            default:
                return State.NOT_THIS_OR_STOPPED;
        }
    }

    @Override
    public void stop() {
        mediaManager.stopAudio();
    }

    @Override
    public void record() {
        mediaManager.recordAudio(this);
    }

    @Override
    public void play() {
        mediaManager.playAudio(this);
    }

    @Override
    public void resume() {
        mediaManager.resumeAudio();
    }

    @Override
    public void pause() {
        mediaManager.pauseAudio();
    }


    @Override
    public String getFilePath() {
        return filepath;
    }

    @Override
    public void updateProgress(int position) {
        mediaManager.updateAudioProgress(position);
    }


    @Override
    public void playNext() {
        mediaManager.playNext();
    }

    @Override
    public void playPrev() {
        mediaManager.playPrev();
    }
}
