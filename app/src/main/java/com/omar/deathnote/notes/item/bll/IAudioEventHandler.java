package com.omar.deathnote.notes.item.bll;

import com.omar.deathnote.media_play.ports.IMediaClient;

/**
 * Created by omar on 9/17/15.
 */
public interface IAudioEventHandler {

    IMediaClient getMediaClient();

    void playCLicked();

    void nextClicked();

    void prevClicked();

    void stopClicked();

    void progressUpdated(int position);

    void shuffleClicked();

    void repeatClicked();


    void setFilePath(String s);

    String getFilePath();





}
