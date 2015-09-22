package com.omar.deathnote.audioPlay;

/**
 * Created by omar on 9/17/15.
 */
public interface IAudioClient {


    void stop();

    void play();

    void setSeekbarMax(int max);

    void setSeekbarProgress(int max);

    void setAudioTitle(String title);

    void setShuffleState(boolean state);

    void setHidableState(boolean state);

    void setAudioController(IMediaManager audioController);
}
