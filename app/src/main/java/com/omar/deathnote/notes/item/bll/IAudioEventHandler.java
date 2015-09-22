package com.omar.deathnote.notes.item.bll;

import com.omar.deathnote.audioPlay.IAudioClient;

/**
 * Created by omar on 9/17/15.
 */
public interface IAudioEventHandler {

    IAudioClient getAudioClinet();

    void playCLicked();

    void nextClicked();

    void prevClicked();

    void stopClicked();

    void progressUpdated(int position);

    void shuffleClicked();

    void repeatClicked();





}
