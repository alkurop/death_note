package com.omar.deathnote.mediaplay.ports;

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

    void updateRecordingProgress(  int progress);

    void updateProgress(int position);

    void updateShuffleState(boolean state);

    void updateRepeatState(boolean state);

    void setShuffleState(boolean state);

    void setRepeatState(boolean state);


    boolean isShuffle();

    boolean isRepeat();

    AudioClient.State getThisState();


    void playNext();

    void playPrev();

    void resume();
}
