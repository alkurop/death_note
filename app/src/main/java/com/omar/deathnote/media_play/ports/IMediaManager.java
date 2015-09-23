package com.omar.deathnote.media_play.ports;

/**
 * Created by omar on 9/8/15.
 */
public interface IMediaManager {

    void addMediaCLient(IMediaClient mediaClient);

    void remmoveMediaClient(IMediaClient mediaClient);

    void playAudio(IMediaClient mediaClient);

    void stopAudio();

    void recordAudio(IMediaClient mediaClient);

    void playPrev();

    void pause();

    void playNext();

    void onPlayEnded();

    void setRepeat(boolean repeat);

    void setShuffle(boolean shuffle);

    MediaState getMediaState();
}
