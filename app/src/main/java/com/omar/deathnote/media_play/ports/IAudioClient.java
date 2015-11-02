package com.omar.deathnote.media_play.ports;

/**
 * Created by omar on 9/17/15.
 */
public interface IAudioClient {

    void setFilePath(String filepath);

    String getFilePath();

    void stop();

    void record();

    void play();

    void pause();

    void stopCallback();

    void recordCallback();

    void playCallback();

    void pauseCallback();

    void updateSeekBar(int max, int progress);

    void updateProgress(int position);

    void setShuffleState(boolean state);

    void setRepeatState(boolean state);

    AudioClient.State getThisState();


    void playNext();

    void playPrev();

    void resume();
}
