package com.omar.deathnote.media_play.ports;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by omar on 9/17/15.
 */
public class MediaManager implements IMediaManager {

    private static MediaState mediaState;
    private List<IMediaClient> mediaClientList;
    private static MediaManager mediaManager;
    private boolean isShuffle;
    private boolean isRepeat;

    private MediaManager() {
        mediaClientList = new ArrayList<>();
        mediaState = new MediaState();
    }

    public static IMediaManager I() {
        if (mediaManager == null) mediaManager = new MediaManager();
        return mediaManager;
    }

    @Override
    public void addMediaCLient(IMediaClient mediaClient) {
        mediaClientList.add(mediaClient);
    }

    @Override
    public void remmoveMediaClient(IMediaClient mediaClient) {
        mediaClientList.remove(mediaClient);
    }

    @Override
    public void playAudio(IMediaClient mediaClient) {
                stopAudio();
                mediaState.setClinet(mediaClient);
                mediaState.setState(MediaState.STATES.PLAYING_AUDIO);
                ((IAudioClient)(mediaState.getClient())).playCallback();
    }

    @Override
    public void stopAudio() {
        if(mediaState.getState() == MediaState.STATES.STOPPED)
            return;
        mediaState.setState(MediaState.STATES.STOPPED);
        ( (IAudioClient)  (mediaState.getClient())).stopCallback();
    }

    @Override
    public void recordAudio(IMediaClient mediaClient) {
         mediaState.setClinet(mediaClient);
        mediaState.setState(MediaState.STATES.RECORDING_AUDIO);
        ((IAudioClient)(mediaState.getClient())).recordCallback();
    }

    @Override
    public void playPrev() {

    }

    @Override
    public void pause() {
        mediaState.setState(MediaState.STATES.PAUSED_AUDIO);
        ((IAudioClient)(mediaState.getClient())).pauseCallback();
    }

    @Override
    public void playNext() {

    }

    @Override
    public void onPlayEnded() {
        mediaState.setState(MediaState.STATES.STOPPED);
    }

    @Override
    public void setRepeat(boolean repeat) {
        isRepeat = repeat;
    }

    @Override
    public void setShuffle(boolean shuffle) {
        isShuffle = shuffle;
    }

    @Override
    public MediaState getMediaState() {
        return mediaState;
    }
}
