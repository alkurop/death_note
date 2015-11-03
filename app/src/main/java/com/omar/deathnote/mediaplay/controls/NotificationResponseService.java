package com.omar.deathnote.mediaplay.controls;

import android.app.IntentService;
import android.content.Intent;
import com.omar.deathnote.mediaplay.ports.AudioClient;

import static com.omar.deathnote.mediaplay.controls.INotificationControl.EVENT;
import static com.omar.deathnote.mediaplay.controls.INotificationControl.SERVICE_EVENT;

/**
 * Created by omar on 11/3/15.
 */
public class NotificationResponseService extends IntentService {
    private EVENT event;

    public NotificationResponseService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        event = EVENT.valueOf(intent.getStringExtra(SERVICE_EVENT));
        switch (event) {
            case GO:
                break;

            case PAUSE:
                MediaManager.I().pauseAudio();
                break;

            case PLAY_RECORDED:
                MediaManager.I().playAudio(MediaManager.I().getMediaState().getClient());
                break;

            case STOP:
                MediaManager.I().stopAudio();
                break;

            case RESUME:
                MediaManager.I().resumeAudio();
                break;

            case PREV:
                MediaManager.I().playPrev();
                break;

            case NEXT:
                MediaManager.I().playNext();
                break;

            case SET_REPEAT:
                ((AudioClient) MediaManager.I().getMediaState().getClient()).setRepeatState(!((AudioClient)
                        MediaManager.I().getMediaState().getClient()).isRepeat());
                break;

            case SET_SHUFFLE:
                MediaManager.I().setShuffle(!MediaManager.I().isShuffle());
                break;
        }
    }
}
