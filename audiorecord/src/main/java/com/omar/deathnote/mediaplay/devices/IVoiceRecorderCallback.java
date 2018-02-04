package com.omar.deathnote.mediaplay.devices;

/**
 * Created by omar on 11/2/15.
 */
public interface IVoiceRecorderCallback {
        void onErrorOccured(String error);

        void normalMessage(String message);

        void sendPositionUpdate(long position);
}
