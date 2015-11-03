package com.omar.deathnote.mediaplay.controls;

import com.omar.deathnote.mediaplay.ports.IMediaClient;

/**
 * Created by omar on 11/3/15.
 */
public class NotificationManager implements INotificationControl {
    private IMediaClient client;
    private static NotificationManager sNotificationManager;

    private NotificationManager(){}

    public static NotificationManager I(){
        if(sNotificationManager == null)
            sNotificationManager = new NotificationManager();
        return sNotificationManager;
    }



    @Override
    public void showNotification(TYPE type, IMediaClient client) {
        this.client = client;
    }

    @Override
    public void cancelNotification() {

    }

    private void showPlayingNotification(){}
    private void showPausedNotification(){}
    private void showRecordingNotification(){}
    private void showWaitingNotification(){}

}

