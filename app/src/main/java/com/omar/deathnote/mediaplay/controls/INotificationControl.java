package com.omar.deathnote.mediaplay.controls;

import com.omar.deathnote.mediaplay.ports.IMediaClient;

/**
 * Created by omar on 9/23/15.
 */
public interface INotificationControl {

    enum TYPE {
        PLAYING,
        PAUSED,
        RECORDING,
        WAITING_TO_PLAY_RECORDED
    }

    enum EVENT {
        GO,
        STOP,
        PAUSE,
        RESUME,
        NEXT,
        PREV,
        PLAY_RECORDED,
        SET_SHUFFLE,
        SET_REPEAT
    }

    String SERVICE_EVENT = "event";


    void showNotification(TYPE type, IMediaClient client);

    void cancelNotification();
}
