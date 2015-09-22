package com.omar.deathnote.audioPlay;

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
        switch (mediaState.getState()) {
            case PLAYING:
            case PLAYING_REPEAT:
            case RECORDING:
            case PAUSED:
            case PLAYING_SHUFFLE:
                stopAudio();
                mediaState.setState(MediaState.STATES.PLAYING);
                mediaState.setClinet(mediaClient);
                ((IAudioClient)mediaState.getClient()).play();
        }
    }

    @Override
    public void stopAudio() {
        mediaState.setState(MediaState.STATES.STOPPED);
        ((IAudioClient)mediaState.getClient()).stop();

    }

    @Override
    public void recordAudio(IMediaClient mediaClient) {

    }

    @Override
    public void playPrev() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void playNext() {

    }

    @Override
    public void onPlayEnded() {

    }

    @Override
    public void setRepeat(boolean repeat) {

    }

    @Override
    public void setShuffle(boolean shuffle) {

    }
}
